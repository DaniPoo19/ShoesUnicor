package com.mycompany.shoesunicor.util;

import com.mycompany.shoesunicor.model.Product;
import com.mycompany.shoesunicor.model.User;
import com.mycompany.shoesunicor.model.UserRole;

import java.util.List;

/**
 * Inicializa la base de datos con datos por defecto
 * @author Victor Negrete
 */
public class DataInitializer {
    
    public static void initializeData() {
        initializeUsers();
        initializeProducts();
    }
    
    private static void initializeUsers() {
        List<User> users = JsonDatabase.loadUsers();
        
        // Si ya hay usuarios, no inicializar
        if (!users.isEmpty()) {
            return;
        }
        
        // Crear usuario administrador por defecto
        User admin = new User(
            JsonDatabase.generateId("USR"),
            "admin",
            PasswordUtil.hashPassword("admin123"),
            "admin@unicorshoes.com",
            "Administrador Unicor",
            UserRole.ADMIN
        );
        
        // Crear usuarios de prueba con nombres realistas
        User victor = new User(
            JsonDatabase.generateId("USR"),
            "Victor19",
            PasswordUtil.hashPassword("123456"),
            "victor.negrete@unicor.edu.co",
            "Victor Manuel Negrete",
            UserRole.USER
        );
        
        User maria = new User(
            JsonDatabase.generateId("USR"),
            "Maria23",
            PasswordUtil.hashPassword("123456"),
            "maria.garcia@unicor.edu.co",
            "Maria Alejandra Garcia",
            UserRole.USER
        );
        
        User carlos = new User(
            JsonDatabase.generateId("USR"),
            "Carlos_2000",
            PasswordUtil.hashPassword("123456"),
            "carlos.rodriguez@unicor.edu.co",
            "Carlos Andres Rodriguez",
            UserRole.USER
        );
        
        User andrea = new User(
            JsonDatabase.generateId("USR"),
            "Andrea_M",
            PasswordUtil.hashPassword("123456"),
            "andrea.martinez@unicor.edu.co",
            "Andrea Marcela Martinez",
            UserRole.USER
        );
        
        users.add(admin);
        users.add(victor);
        users.add(maria);
        users.add(carlos);
        users.add(andrea);
        
        JsonDatabase.saveUsers(users);
        
        System.out.println("✓ Usuarios iniciales creados");
        System.out.println("  - Admin: username=admin, password=admin123");
        System.out.println("  - Victor: username=Victor19, password=123456");
        System.out.println("  - Maria: username=Maria23, password=123456");
        System.out.println("  - Carlos: username=Carlos_2000, password=123456");
        System.out.println("  - Andrea: username=Andrea_M, password=123456");
    }
    
    private static void initializeProducts() {
        List<Product> products = JsonDatabase.loadProducts();
        
        // Lista de productos por defecto que deben existir
        String[] requiredProducts = {
            "Air Jordan 1 Azul",
            "Air Jordan 1 Beige",
            "Air Jordan 1 Naranja",
            "Air Jordan 1 Rojo",
            "Air Jordan 1 Spiderman",
            "Air Jordan 1 Verde"
        };
        
        // Verificar qué productos ya existen
        boolean[] productsExist = new boolean[requiredProducts.length];
        for (int i = 0; i < requiredProducts.length; i++) {
            final String productName = requiredProducts[i];
            productsExist[i] = products.stream()
                .anyMatch(p -> p.getName().equals(productName));
        }
        
        // Si todos los productos ya existen, no hacer nada
        boolean allExist = true;
        for (boolean exists : productsExist) {
            if (!exists) {
                allExist = false;
                break;
            }
        }
        
        if (allExist && !products.isEmpty()) {
            System.out.println("✓ Todos los productos ya están en la base de datos");
            return;
        }
        
        // Crear todos los productos basados en las imágenes disponibles
        // Orden alfabético para mejor organización
        int addedCount = 0;
        
        // 1. Air Jordan 1 Azul
        if (!productsExist[0]) {
            products.add(new Product(
                JsonDatabase.generateId("PROD"),
                "Air Jordan 1 Azul",
                "Zapatillas Air Jordan 1 en color azul vibrante. Diseño clásico y cómodo, perfecto para cualquier ocasión. Material de alta calidad y suela antideslizante.",
                350000.0,
                15,
                "images/jordan 1 azules.jpg",
                "Zapatillas",
                "Nike"
            ));
            addedCount++;
        }
        
        // 2. Air Jordan 1 Beige
        if (!productsExist[1]) {
            products.add(new Product(
                JsonDatabase.generateId("PROD"),
                "Air Jordan 1 Beige",
                "Zapatillas Air Jordan 1 en tono beige elegante. Estilo versátil que combina con todo. Ideal para uso diario y ocasiones especiales. Máximo confort garantizado.",
                340000.0,
                12,
                "images/jordan 1 beige.png",
                "Zapatillas",
                "Nike"
            ));
            addedCount++;
        }
        
        // 3. Air Jordan 1 Naranja
        if (!productsExist[2]) {
            products.add(new Product(
                JsonDatabase.generateId("PROD"),
                "Air Jordan 1 Naranja",
                "Zapatillas Air Jordan 1 en naranja brillante. Energético y audaz, para quienes no temen destacar. Diseño único con detalles premium.",
                345000.0,
                14,
                "images/jrodan 1 naranja.jpg",
                "Zapatillas",
                "Nike"
            ));
            addedCount++;
        }
        
        // 4. Air Jordan 1 Rojo
        if (!productsExist[3]) {
            products.add(new Product(
                JsonDatabase.generateId("PROD"),
                "Air Jordan 1 Rojo",
                "Zapatillas Air Jordan 1 en color rojo intenso. Llamativo y moderno, ideal para destacar. Edición limitada con acabados de lujo.",
                360000.0,
                10,
                "images/jordan 1 roja.png",
                "Zapatillas",
                "Nike"
            ));
            addedCount++;
        }
        
        // 5. Air Jordan 1 Spiderman (con el nombre exacto del archivo)
        if (!productsExist[4]) {
            products.add(new Product(
                JsonDatabase.generateId("PROD"),
                "Air Jordan 1 Spiderman",
                "Edición especial Air Jordan 1 inspirada en Spiderman. Diseño exclusivo para coleccionistas. Incluye detalles únicos del héroe arácnido. Edición limitada.",
                450000.0,
                5,
                "images/Jorad 1 Spierman.jpg",
                "Zapatillas",
                "Nike"
            ));
            addedCount++;
        }
        
        // 6. Air Jordan 1 Verde
        if (!productsExist[5]) {
            products.add(new Product(
                JsonDatabase.generateId("PROD"),
                "Air Jordan 1 Verde",
                "Zapatillas Air Jordan 1 en verde fresco. Perfecto para los fans de los colores institucionales de Unicor. Diseño deportivo con toque elegante.",
                355000.0,
                18,
                "images/jordan 1 verde.jpg",
                "Zapatillas",
                "Nike"
            ));
            addedCount++;
        }
        
        // Guardar productos solo si se añadieron nuevos
        if (addedCount > 0) {
            JsonDatabase.saveProducts(products);
            System.out.println("✓ " + addedCount + " producto(s) añadido(s) a la base de datos");
            System.out.println("✓ Total de productos en catálogo: " + products.size());
        }
        
        // Mostrar lista completa de productos
        System.out.println("\n📦 Catálogo de Productos Disponibles:");
        products.stream()
            .filter(Product::isActive)
            .sorted((p1, p2) -> p1.getName().compareTo(p2.getName()))
            .forEach(p -> System.out.println("  ✓ " + p.getName() + " - " + 
                CurrencyFormatter.formatPrice(p.getPrice()) + " - Stock: " + p.getStock()));
    }
}

