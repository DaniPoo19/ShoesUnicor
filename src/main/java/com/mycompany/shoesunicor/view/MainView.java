package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.AuthController;
import com.mycompany.shoesunicor.model.CartItem;
import com.mycompany.shoesunicor.model.Product;
import com.mycompany.shoesunicor.util.Session;
import javafx.collections.ListChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/**
 * Vista principal con navegación
 * @author Victor Negrete
 */
public class MainView extends BorderPane {
    
    private static MainView instance;
    
    private Session session;
    private AuthController authController;
    private Runnable onLogout;
    
    // Todas las vistas
    private ProductsView productsView;
    private CartView cartView;
    private WishlistView wishlistView;
    private OrderHistoryView orderHistoryView;
    private AdminView adminView;
    
    // Botones de navegación
    private Button productsBtn;
    private Button cartBtn;
    private Button wishlistBtn;
    private Button ordersBtn;
    private Button adminBtn;
    
    // Badge del carrito
    private Label cartBadge;
    
    public MainView(Runnable onLogout) {
        instance = this;
        this.session = Session.getInstance();
        this.authController = new AuthController();
        this.onLogout = onLogout;
        
        setupUI();
        
        // Redirigir según el rol del usuario
        if (session.isAdmin()) {
            showAdminView();
        } else {
            showProductsView();
        }
    }
    
    /**
     * Obtiene la instancia actual de MainView
     */
    public static MainView getInstance() {
        return instance;
    }
    
    private void setupUI() {
        // Navbar superior
        setTop(createNavbar());
        
        // Inicializar todas las vistas
        productsView = new ProductsView();
        cartView = new CartView();
        wishlistView = new WishlistView();
        orderHistoryView = new OrderHistoryView();
        
        // Vista de admin solo para administradores
        if (session.isAdmin()) {
            adminView = new AdminView();
        }
        
        // Listener para actualizar badge del carrito
        session.getCart().addListener((ListChangeListener.Change<? extends CartItem> c) -> {
            updateCartBadge();
        });
        updateCartBadge();
    }
    
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
        cartBtn.setOnAction(e -> showCartView());
        
        cartBadge = new Label("0");
        cartBadge.getStyleClass().add("cart-badge");
        cartBadge.setVisible(false);
        cartBox.getChildren().addAll(cartBtn, cartBadge);
        
        // Botón Favoritos
        wishlistBtn = new Button("Favoritos");
        wishlistBtn.getStyleClass().add("navbar-button");
        wishlistBtn.setOnAction(e -> showWishlistView());
        
        // Botón Mis Pedidos
        ordersBtn = new Button("Mis Pedidos");
        ordersBtn.getStyleClass().add("navbar-button");
        ordersBtn.setOnAction(e -> showOrderHistoryView());
        
        // Botón Salir
        Button logoutBtn = new Button("Salir");
        logoutBtn.getStyleClass().add("btn-secondary");
        logoutBtn.setOnAction(e -> {
            authController.logout();
            onLogout.run();
        });
        
        navbar.getChildren().addAll(logo, spacer, userLabel, productsBtn, cartBox, wishlistBtn, ordersBtn);
        
        // Agregar botón admin si es administrador
        if (session.isAdmin()) {
            adminBtn = new Button("Admin");
            adminBtn.getStyleClass().add("navbar-button");
            adminBtn.setOnAction(e -> showAdminView());
            navbar.getChildren().add(adminBtn);
        }
        
        navbar.getChildren().add(logoutBtn);
        
        return navbar;
    }
    
    private void updateCartBadge() {
        int itemCount = session.getCartItemCount();
        if (itemCount > 0) {
            cartBadge.setText(String.valueOf(itemCount));
            cartBadge.setVisible(true);
        } else {
            cartBadge.setVisible(false);
        }
    }
    
    private void clearActiveButtons() {
        productsBtn.getStyleClass().remove("navbar-button-active");
        cartBtn.getStyleClass().remove("navbar-button-active");
        wishlistBtn.getStyleClass().remove("navbar-button-active");
        ordersBtn.getStyleClass().remove("navbar-button-active");
        if (adminBtn != null) {
            adminBtn.getStyleClass().remove("navbar-button-active");
        }
    }
    
    public void showProductsView() {
        clearActiveButtons();
        productsBtn.getStyleClass().add("navbar-button-active");
        productsView.refresh();
        setCenter(productsView);
    }
    
    public void showCartView() {
        clearActiveButtons();
        cartBtn.getStyleClass().add("navbar-button-active");
        cartView.refresh();
        setCenter(cartView);
    }
    
    public void showWishlistView() {
        clearActiveButtons();
        wishlistBtn.getStyleClass().add("navbar-button-active");
        wishlistView.refresh();
        setCenter(wishlistView);
    }
    
    public void showOrderHistoryView() {
        clearActiveButtons();
        ordersBtn.getStyleClass().add("navbar-button-active");
        orderHistoryView.refresh();
        setCenter(orderHistoryView);
    }
    
    public void showAdminView() {
        if (session.isAdmin()) {
            clearActiveButtons();
            adminBtn.getStyleClass().add("navbar-button-active");
            adminView.refresh();
            setCenter(adminView);
        }
    }
    
    /**
     * Muestra la vista de detalles de un producto
     * @param product El producto a mostrar
     */
    public void showProductDetailView(Product product) {
        clearActiveButtons();
        productsBtn.getStyleClass().add("navbar-button-active");
        ProductDetailView detailView = new ProductDetailView(product, this::showProductsView);
        setCenter(detailView);
    }
}
