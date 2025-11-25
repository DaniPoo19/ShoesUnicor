package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.AuthController;
import com.mycompany.shoesunicor.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/**
 * Vista Principal - Dashboard
 * VERSIÓN MÍNIMA: Solo productos + carrito + logout
 * 
 * @author Victor Negrete
 */
public class MainView extends BorderPane {
    private Session session;
    private AuthController authController;
    private Runnable onLogout;
    
    // Vistas activas
    private ProductsView productsView;
    
    // Botones de navegación
    private Button productsBtn;
    private Button cartBtn;
    
    // Badge del carrito
    private Label cartBadge;
    
    public MainView(Runnable onLogout) {
        this.session = Session.getInstance();
        this.authController = new AuthController();
        this.onLogout = onLogout;
        
        setupUI();
        showProductsView();
    }
    
    private void setupUI() {
        // Navbar superior
        setTop(createNavbar());
        
        // Inicializar vista de productos
        productsView = new ProductsView();
        
        // Actualizar badge del carrito cuando cambie
        session.getCart().addListener((javafx.collections.ListChangeListener.Change<? extends com.mycompany.shoesunicor.model.CartItem> c) -> {
            updateCartBadge();
        });
        updateCartBadge();
    }
    
    /**
     * Crea la barra de navegación superior
     */
    private HBox createNavbar() {
        HBox navbar = new HBox(20);
        navbar.getStyleClass().add("navbar");
        navbar.setPadding(new Insets(15, 30, 15, 30));
        navbar.setAlignment(Pos.CENTER_LEFT);
        
        // Logo
        Text logo = new Text("UNICOR SHOES");
        logo.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-fill: #2ECC71;");
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Info del usuario
        Label userLabel = new Label("Usuario: " + session.getCurrentUser().getFullName());
        userLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Botón Productos
        productsBtn = new Button("Productos");
        productsBtn.getStyleClass().add("navbar-button");
        productsBtn.setOnAction(e -> showProductsView());
        
        // Carrito con badge
        HBox cartBox = new HBox(5);
        cartBox.setAlignment(Pos.CENTER);
        cartBtn = new Button("Carrito");
        cartBtn.getStyleClass().add("navbar-button");
        cartBtn.setOnAction(e -> showCartInfo());
        
        cartBadge = new Label("0");
        cartBadge.getStyleClass().add("cart-badge");
        cartBadge.setVisible(false);
        cartBox.getChildren().addAll(cartBtn, cartBadge);
        
        // Botón Salir
        Button logoutBtn = new Button("Salir");
        logoutBtn.getStyleClass().add("btn-secondary");
        logoutBtn.setOnAction(e -> {
            authController.logout();
            onLogout.run();
        });
        
        navbar.getChildren().addAll(logo, spacer, userLabel, productsBtn, cartBox, logoutBtn);
        
        return navbar;
    }
    
    /**
     * Actualiza el contador del carrito
     */
    private void updateCartBadge() {
        int itemCount = session.getCartItemCount();
        if (itemCount > 0) {
            cartBadge.setText(String.valueOf(itemCount));
            cartBadge.setVisible(true);
        } else {
            cartBadge.setVisible(false);
        }
    }
    
    /**
     * Muestra la vista de productos
     */
    public void showProductsView() {
        productsBtn.getStyleClass().add("navbar-button-active");
        cartBtn.getStyleClass().remove("navbar-button-active");
        productsView.refresh();
        setCenter(productsView);
    }
    
    /**
     * Muestra información del carrito (versión simplificada)
     */
    private void showCartInfo() {
        productsBtn.getStyleClass().remove("navbar-button-active");
        cartBtn.getStyleClass().add("navbar-button-active");
        
        VBox cartInfo = new VBox(20);
        cartInfo.setAlignment(Pos.CENTER);
        cartInfo.setPadding(new Insets(50));
        
        Text title = new Text("Carrito de Compras");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        int items = session.getCartItemCount();
        double total = session.getCartTotal();
        
        Label itemsLabel = new Label("Productos en carrito: " + items);
        itemsLabel.setStyle("-fx-font-size: 18px;");
        
        Label totalLabel = new Label("Total: $" + String.format("%,.0f", total) + " COP");
        totalLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2ECC71;");
        
        Button backBtn = new Button("Volver a Productos");
        backBtn.getStyleClass().add("btn-primary");
        backBtn.setOnAction(e -> showProductsView());
        
        Label note = new Label("(Funcionalidad de checkout próximamente)");
        note.setStyle("-fx-font-size: 12px; -fx-text-fill: #95A5A6;");
        
        cartInfo.getChildren().addAll(title, itemsLabel, totalLabel, backBtn, note);
        setCenter(cartInfo);
    }
}
