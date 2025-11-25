package com.mycompany.shoesunicor.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Modelo de Usuario
 * @author Victor Negrete
 */
public class User {
    private String id;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private UserRole role;
    private List<String> wishlistProductIds;
    private List<String> orderIds;

    public User() {
        this.wishlistProductIds = new ArrayList<>();
        this.orderIds = new ArrayList<>();
    }

    public User(String id, String username, String password, String email, String fullName, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.wishlistProductIds = new ArrayList<>();
        this.orderIds = new ArrayList<>();
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public List<String> getWishlistProductIds() {
        return wishlistProductIds;
    }

    public void setWishlistProductIds(List<String> wishlistProductIds) {
        this.wishlistProductIds = wishlistProductIds;
    }

    public List<String> getOrderIds() {
        return orderIds;
    }

    public void setOrderIds(List<String> orderIds) {
        this.orderIds = orderIds;
    }

    public void addToWishlist(String productId) {
        if (!wishlistProductIds.contains(productId)) {
            wishlistProductIds.add(productId);
        }
    }

    public void removeFromWishlist(String productId) {
        wishlistProductIds.remove(productId);
    }

    public boolean isInWishlist(String productId) {
        return wishlistProductIds.contains(productId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role=" + role +
                '}';
    }
}
