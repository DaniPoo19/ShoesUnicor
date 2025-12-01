package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.OrderController;
import com.mycompany.shoesunicor.model.CartItem;
import com.mycompany.shoesunicor.model.Order;
import com.mycompany.shoesunicor.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.List;

/**
 * Vista de historial de pedidos
 * @author Victor Negrete
 */
public class OrderHistoryView extends VBox {
    private OrderController orderController;
    private Session session;
    private VBox ordersContainer;
    
    public OrderHistoryView() {
        this.orderController = new OrderController();
        this.session = Session.getInstance();
        
        setupUI();
    }
    
    private void setupUI() {
        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("root");
        
        // Header
        Text title = new Text("📦 Historial de Pedidos");
        title.getStyleClass().add("subtitle");
        
        // Scroll para órdenes
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        ordersContainer = new VBox(15);
        ordersContainer.setPadding(new Insets(10));
        scrollPane.setContent(ordersContainer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        getChildren().addAll(title, scrollPane);
    }
    
    public void refresh() {
        loadOrders();
    }
    
    private void loadOrders() {
        ordersContainer.getChildren().clear();
        
        List<Order> orders = orderController.getUserOrders(session.getCurrentUser().getId());
        
        if (orders.isEmpty()) {
            Label emptyLabel = new Label("No tienes pedidos aún");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7F8C8D;");
            ordersContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (Order order : orders) {
            ordersContainer.getChildren().add(createOrderCard(order));
        }
    }
    
    private VBox createOrderCard(Order order) {
        VBox card = new VBox(15);
        card.getStyleClass().add("cart-item");
        card.setPadding(new Insets(20));
        
        // Header de la orden
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        
        VBox infoBox = new VBox(5);
        
        Label orderIdLabel = new Label("Pedido #" + order.getId().substring(order.getId().length() - 8));
        orderIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label dateLabel = new Label("Fecha: " + order.getFormattedDate());
        dateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D;");
        
        infoBox.getChildren().addAll(orderIdLabel, dateLabel);
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Estado
        Label statusLabel = new Label(order.getStatus().getDisplayName());
        statusLabel.getStyleClass().add("badge");
        
        switch (order.getStatus()) {
            case PENDING:
                statusLabel.getStyleClass().add("badge-warning");
                break;
            case PROCESSING:
            case SHIPPED:
                statusLabel.setStyle("-fx-background-color: #D6EAF8; -fx-text-fill: #1F618D;");
                break;
            case DELIVERED:
                statusLabel.getStyleClass().add("badge-success");
                break;
            case CANCELLED:
                statusLabel.getStyleClass().add("badge-error");
                break;
        }
        
        header.getChildren().addAll(infoBox, spacer, statusLabel);
        
        // Dirección
        Label addressLabel = new Label("📍 " + order.getShippingAddress());
        addressLabel.setStyle("-fx-font-size: 13px;");
        
        // Items
        VBox itemsBox = new VBox(5);
        itemsBox.setStyle("-fx-background-color: #ECF0F1; -fx-padding: 10; -fx-background-radius: 5;");
        
        Label itemsTitle = new Label("Productos:");
        itemsTitle.setStyle("-fx-font-weight: bold;");
        itemsBox.getChildren().add(itemsTitle);
        
        for (CartItem item : order.getItems()) {
            Label itemLabel = new Label(String.format("• %s x%d - $%.2f", 
                    item.getProductName(), item.getQuantity(), item.getSubtotal()));
            itemsBox.getChildren().add(itemLabel);
        }
        
        // Total
        HBox totalBox = new HBox();
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        Label totalLabel = new Label(String.format("Total: $%.2f", order.getTotal()));
        totalLabel.getStyleClass().add("label-price");
        totalBox.getChildren().add(totalLabel);
        
        card.getChildren().addAll(header, addressLabel, itemsBox, totalBox);
        
        return card;
    }
}

