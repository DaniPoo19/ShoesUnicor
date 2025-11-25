package com.mycompany.shoesunicor.controller;

import com.mycompany.shoesunicor.model.User;
import com.mycompany.shoesunicor.util.JsonDatabase;
import com.mycompany.shoesunicor.util.Session;

import java.util.List;

/**
 * Controlador de gestión de usuarios
 * @author Victor Negrete
 */
public class UserController {

    /**
     * Obtiene todos los usuarios (admin)
     */
    public List<User> getAllUsers() {
        return JsonDatabase.loadUsers();
    }

    /**
     * Obtiene un usuario por ID
     */
    public User getUserById(String userId) {
        return JsonDatabase.loadUsers().stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);
    }

    /**
     * Agrega un producto a la wishlist del usuario actual
     */
    public boolean addToWishlist(String productId) {
        try {
            User currentUser = Session.getInstance().getCurrentUser();
            if (currentUser == null) return false;

            currentUser.addToWishlist(productId);
            JsonDatabase.saveUser(currentUser);
            Session.getInstance().setCurrentUser(currentUser);
            return true;
        } catch (Exception e) {
            System.err.println("Error agregando a wishlist: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina un producto de la wishlist del usuario actual
     */
    public boolean removeFromWishlist(String productId) {
        try {
            User currentUser = Session.getInstance().getCurrentUser();
            if (currentUser == null) return false;

            currentUser.removeFromWishlist(productId);
            JsonDatabase.saveUser(currentUser);
            Session.getInstance().setCurrentUser(currentUser);
            return true;
        } catch (Exception e) {
            System.err.println("Error eliminando de wishlist: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifica si un producto está en la wishlist
     */
    public boolean isInWishlist(String productId) {
        User currentUser = Session.getInstance().getCurrentUser();
        return currentUser != null && currentUser.isInWishlist(productId);
    }

    /**
     * Obtiene los productos de la wishlist del usuario actual
     */
    public List<String> getWishlistProductIds() {
        User currentUser = Session.getInstance().getCurrentUser();
        return currentUser != null ? currentUser.getWishlistProductIds() : List.of();
    }

    /**
     * Actualiza información del usuario
     */
    public boolean updateUser(User user) {
        try {
            JsonDatabase.saveUser(user);

            // Si es el usuario actual, actualizar en sesión
            User currentUser = Session.getInstance().getCurrentUser();
            if (currentUser != null && currentUser.getId().equals(user.getId())) {
                Session.getInstance().setCurrentUser(user);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando usuario: " + e.getMessage());
            return false;
        }
    }
}
