package com.mycompany.shoesunicor.controller;

import com.mycompany.shoesunicor.model.Product;
import com.mycompany.shoesunicor.util.JsonDatabase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador de productos
 * @author Victor Negrete
 */
public class ProductController {

    /**
     * Obtiene todos los productos activos
     */
    public List<Product> getAllProducts() {
        return JsonDatabase.loadProducts().stream()
                .filter(Product::isActive)
                .collect(Collectors.toList());
    }

    /**
     * Obtiene todos los productos (incluidos inactivos) - solo admin
     */
    public List<Product> getAllProductsAdmin() {
        return JsonDatabase.loadProducts();
    }

    /**
     * Busca un producto por ID
     */
    public Product getProductById(String id) {
        return JsonDatabase.loadProducts().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Busca productos por nombre
     */
    public List<Product> searchProducts(String searchTerm) {
        String lowerSearch = searchTerm.toLowerCase();
        return getAllProducts().stream()
                .filter(p -> p.getName().toLowerCase().contains(lowerSearch) ||
                        p.getDescription().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toList());
    }

    /**
     * Filtra productos por categoría
     */
    public List<Product> getProductsByCategory(String category) {
        return getAllProducts().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    /**
     * Agrega un nuevo producto (admin)
     */
    public boolean addProduct(Product product) {
        try {
            product.setId(JsonDatabase.generateId("PROD"));
            JsonDatabase.saveProduct(product);
            return true;
        } catch (Exception e) {
            System.err.println("Error agregando producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza un producto existente (admin)
     */
    public boolean updateProduct(Product product) {
        try {
            JsonDatabase.saveProduct(product);
            return true;
        } catch (Exception e) {
            System.err.println("Error actualizando producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Elimina (desactiva) un producto (admin)
     */
    public boolean deleteProduct(String productId) {
        try {
            Product product = getProductById(productId);
            if (product != null) {
                product.setActive(false);
                JsonDatabase.saveProduct(product);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error eliminando producto: " + e.getMessage());
            return false;
        }
    }

    /**
     * Actualiza el stock de un producto
     */
    public boolean updateStock(String productId, int newStock) {
        try {
            Product product = getProductById(productId);
            if (product != null) {
                product.setStock(newStock);
                JsonDatabase.saveProduct(product);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error actualizando stock: " + e.getMessage());
            return false;
        }
    }

    /**
     * Reduce el stock después de una compra
     */
    public boolean reduceStock(String productId, int quantity) {
        Product product = getProductById(productId);
        if (product != null && product.getStock() >= quantity) {
            return updateStock(productId, product.getStock() - quantity);
        }
        return false;
    }
}
