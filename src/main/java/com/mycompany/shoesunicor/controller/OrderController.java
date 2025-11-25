package com.mycompany.shoesunicor.controller;

import com.mycompany.shoesunicor.model.CartItem;
import com.mycompany.shoesunicor.model.Order;
import com.mycompany.shoesunicor.model.OrderStatus;
import com.mycompany.shoesunicor.model.User;
import com.mycompany.shoesunicor.util.JsonDatabase;
import com.mycompany.shoesunicor.util.Session;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de órdenes/pedidos
 * @author Victor Negrete
 */
public class OrderController {
    private ProductController productController;

    public OrderController() {
        this.productController = new ProductController();
    }

    /**
     * Crea una nueva orden desde el carrito actual
     */
    public boolean createOrder(String shippingAddress) {
        try {
            Session session = Session.getInstance();
            User currentUser = session.getCurrentUser();

            if (currentUser == null || session.getCart().isEmpty()) {
                return false;
            }

            // Verificar stock disponible
            for (CartItem item : session.getCart()) {
                if (!productController.reduceStock(item.getProductId(), item.getQuantity())) {
                    return false; // Stock insuficiente
                }
            }

            // Crear la orden
            String orderId = JsonDatabase.generateId("ORD");
            Order order = new Order(
                    orderId,
                    currentUser.getId(),
                    currentUser.getUsername(),
                    session.getCart(),
                    session.getCartTotal(),
                    shippingAddress
            );

            // Guardar la orden
            JsonDatabase.saveOrder(order);

            // Agregar ID de orden al usuario
            currentUser.getOrderIds().add(orderId);
            JsonDatabase.saveUser(currentUser);

            // Limpiar el carrito
            session.clearCart();

            return true;
        } catch (Exception e) {
            System.err.println("Error creando orden: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene todas las órdenes de un usuario
     */
    public List<Order> getUserOrders(String userId) {
        return JsonDatabase.loadOrders().stream()
                .filter(o -> o.getUserId().equals(userId))
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todas las órdenes (admin)
     */
    public List<Order> getAllOrders() {
        return JsonDatabase.loadOrders().stream()
                .sorted((o1, o2) -> o2.getOrderDate().compareTo(o1.getOrderDate()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una orden por ID
     */
    public Order getOrderById(String orderId) {
        return JsonDatabase.loadOrders().stream()
                .filter(o -> o.getId().equals(orderId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Actualiza el estado de una orden (admin)
     */
    public boolean updateOrderStatus(String orderId, OrderStatus newStatus) {
        try {
            Order order = getOrderById(orderId);
            if (order != null) {
                order.setStatus(newStatus);
                JsonDatabase.saveOrder(order);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error actualizando estado de orden: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancela una orden
     */
    public boolean cancelOrder(String orderId) {
        return updateOrderStatus(orderId, OrderStatus.CANCELLED);
    }
}

