package com.mycompany.shoesunicor.model;

import java.util.Objects;

/**
 * Item del carrito de compra
 * @author Victor Negrete
 */
public class CartItem {
    private String productId;
    private String productName;
    private double price;
    private int quantity;
    private String imagePath;
    
    public CartItem() {
    }
    
    public CartItem(String productId, String productName, double price, int quantity, String imagePath) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.imagePath = imagePath;
    }
    
    public CartItem(Product product, int quantity) {
        this.productId = product.getId();
        this.productName = product.getName();
        this.price = product.getPrice();
        this.quantity = quantity;
        this.imagePath = product.getImagePath();
    }
    
    // Getters y Setters
    public String getProductId() {
        return productId;
    }
    
    public void setProductId(String productId) {
        this.productId = productId;
    }
    
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public double getSubtotal() {
        return price * quantity;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(productId, cartItem.productId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }
}

