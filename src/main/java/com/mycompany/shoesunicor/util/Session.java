package com.mycompany.shoesunicor.util;

import com.mycompany.shoesunicor.model.User;
import com.mycompany.shoesunicor.model.CartItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Gestión de sesión del usuario actual
 * @author Victor Negrete
 */
public class Session {
    private static Session instance;
    private User currentUser;
    private ObservableList<CartItem> cart;
    
    private Session() {
        this.cart = FXCollections.observableArrayList();
    }
    
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }
    
    public void login(User user) {
        this.currentUser = user;
        this.cart.clear();
    }
    
    public void logout() {
        this.currentUser = null;
        this.cart.clear();
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }
    
    public ObservableList<CartItem> getCart() {
        return cart;
    }
    
    public void addToCart(CartItem item) {
        // Buscar si el producto ya está en el carrito
        for (CartItem cartItem : cart) {
            if (cartItem.getProductId().equals(item.getProductId())) {
                cartItem.setQuantity(cartItem.getQuantity() + item.getQuantity());
                return;
            }
        }
        cart.add(item);
    }
    
    public void removeFromCart(CartItem item) {
        cart.remove(item);
    }
    
    public void clearCart() {
        cart.clear();
    }
    
    public double getCartTotal() {
        return cart.stream()
                .mapToDouble(CartItem::getSubtotal)
                .sum();
    }
    
    public int getCartItemCount() {
        return cart.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    public boolean isAdmin() {
        return currentUser != null && currentUser.getRole().name().equals("ADMIN");
    }
}

