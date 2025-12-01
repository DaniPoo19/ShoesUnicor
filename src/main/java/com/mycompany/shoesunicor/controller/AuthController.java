package com.mycompany.shoesunicor.controller;

import com.mycompany.shoesunicor.model.User;
import com.mycompany.shoesunicor.model.UserRole;
import com.mycompany.shoesunicor.util.JsonDatabase;
import com.mycompany.shoesunicor.util.PasswordUtil;
import com.mycompany.shoesunicor.util.Session;

import java.util.List;

/**
 * Controlador de autenticación
 * @author Victor Negrete
 */
public class AuthController {

    /**
     * Intenta iniciar sesión con las credenciales proporcionadas
     * El nombre de usuario es case-insensitive (no distingue mayúsculas/minúsculas)
     */
    public boolean login(String username, String password) {
        List<User> users = JsonDatabase.loadUsers();

        for (User user : users) {
            // Comparación case-insensitive para el username
            if (user.getUsername().equalsIgnoreCase(username) &&
                    PasswordUtil.verifyPassword(password, user.getPassword())) {
                Session.getInstance().login(user);
                return true;
            }
        }

        return false;
    }

    /**
     * Registra un nuevo usuario
     * El nombre de usuario es case-insensitive para evitar duplicados
     */
    public boolean register(String username, String password, String email, String fullName) {
        List<User> users = JsonDatabase.loadUsers();

        // Verificar si el usuario ya existe (case-insensitive)
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return false; // Usuario ya existe
            }
            if (user.getEmail().equalsIgnoreCase(email)) {
                return false; // Email ya registrado
            }
        }

        // Crear nuevo usuario
        String userId = JsonDatabase.generateId("USR");
        String hashedPassword = PasswordUtil.hashPassword(password);
        User newUser = new User(userId, username, hashedPassword, email, fullName, UserRole.USER);

        users.add(newUser);
        JsonDatabase.saveUsers(users);

        return true;
    }

    /**
     * Cierra la sesión actual
     */
    public void logout() {
        Session.getInstance().logout();
    }

    /**
     * Valida el formato de email
     */
    public boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    /**
     * Valida la fortaleza de la contraseña
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Verifica si un nombre de usuario está disponible (case-insensitive)
     */
    public boolean isUsernameAvailable(String username) {
        List<User> users = JsonDatabase.loadUsers();
        return users.stream().noneMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }
}
