package com.mycompany.shoesunicor.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mycompany.shoesunicor.model.*;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Gestión de persistencia con archivos JSON
 * @author Victor Negrete
 */
public class JsonDatabase {
    private static final String DATA_DIR = "src/main/resources/data/";
    private static final String USERS_FILE = DATA_DIR + "users.json";
    private static final String PRODUCTS_FILE = DATA_DIR + "products.json";
    private static final String ORDERS_FILE = DATA_DIR + "orders.json";
    
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();
    
    static {
        initializeDataDirectory();
    }
    
    /**
     * Inicializa el directorio de datos si no existe
     */
    private static void initializeDataDirectory() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
            }
            
            // Crear archivos vacíos si no existen
            createFileIfNotExists(USERS_FILE, "[]");
            createFileIfNotExists(PRODUCTS_FILE, "[]");
            createFileIfNotExists(ORDERS_FILE, "[]");
        } catch (IOException e) {
            System.err.println("Error inicializando directorio de datos: " + e.getMessage());
        }
    }
    
    private static void createFileIfNotExists(String filePath, String defaultContent) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            Files.writeString(path, defaultContent);
        }
    }
    
    // ========== USUARIOS ==========
    
    public static List<User> loadUsers() {
        try {
            String json = Files.readString(Paths.get(USERS_FILE));
            Type listType = new TypeToken<ArrayList<User>>(){}.getType();
            List<User> users = gson.fromJson(json, listType);
            return users != null ? users : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error cargando usuarios: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static void saveUsers(List<User> users) {
        try {
            String json = gson.toJson(users);
            Files.writeString(Paths.get(USERS_FILE), json);
        } catch (IOException e) {
            System.err.println("Error guardando usuarios: " + e.getMessage());
        }
    }
    
    public static void saveUser(User user) {
        List<User> users = loadUsers();
        users.removeIf(u -> u.getId().equals(user.getId()));
        users.add(user);
        saveUsers(users);
    }
    
    // ========== PRODUCTOS ==========
    
    public static List<Product> loadProducts() {
        try {
            String json = Files.readString(Paths.get(PRODUCTS_FILE));
            Type listType = new TypeToken<ArrayList<Product>>(){}.getType();
            List<Product> products = gson.fromJson(json, listType);
            return products != null ? products : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error cargando productos: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static void saveProducts(List<Product> products) {
        try {
            String json = gson.toJson(products);
            Files.writeString(Paths.get(PRODUCTS_FILE), json);
        } catch (IOException e) {
            System.err.println("Error guardando productos: " + e.getMessage());
        }
    }
    
    public static void saveProduct(Product product) {
        List<Product> products = loadProducts();
        products.removeIf(p -> p.getId().equals(product.getId()));
        products.add(product);
        saveProducts(products);
    }
    
    // ========== ÓRDENES ==========
    
    public static List<Order> loadOrders() {
        try {
            String json = Files.readString(Paths.get(ORDERS_FILE));
            Type listType = new TypeToken<ArrayList<Order>>(){}.getType();
            List<Order> orders = gson.fromJson(json, listType);
            return orders != null ? orders : new ArrayList<>();
        } catch (IOException e) {
            System.err.println("Error cargando órdenes: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    public static void saveOrders(List<Order> orders) {
        try {
            String json = gson.toJson(orders);
            Files.writeString(Paths.get(ORDERS_FILE), json);
        } catch (IOException e) {
            System.err.println("Error guardando órdenes: " + e.getMessage());
        }
    }
    
    public static void saveOrder(Order order) {
        List<Order> orders = loadOrders();
        orders.removeIf(o -> o.getId().equals(order.getId()));
        orders.add(order);
        saveOrders(orders);
    }
    
    /**
     * Genera un ID único
     */
    public static String generateId(String prefix) {
        return prefix + "_" + System.currentTimeMillis();
    }
}

