package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.ProductController;
import com.mycompany.shoesunicor.controller.UserController;
import com.mycompany.shoesunicor.model.CartItem;
import com.mycompany.shoesunicor.model.Product;
import com.mycompany.shoesunicor.util.AnimationUtil;
import com.mycompany.shoesunicor.util.CurrencyFormatter;
import com.mycompany.shoesunicor.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista de catálogo de productos con todas las funcionalidades
 * @author Victor Negrete
 */
public class ProductsView extends VBox {
    
    private ProductController productController;
    private UserController userController;
    private Session session;
    private TextField searchField;
    private FlowPane productsContainer;
    
    public ProductsView() {
        this.productController = new ProductController();
        this.userController = new UserController();
        this.session = Session.getInstance();
        
        setupUI();
        loadProducts();
        
        AnimationUtil.fadeIn(this);
    }
    
    private void setupUI() {
        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("root");
        
        // Header con búsqueda
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text("Catálogo de Productos");
        title.getStyleClass().add("subtitle");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        searchField = new TextField();
        searchField.setPromptText("Buscar productos...");
        searchField.getStyleClass().add("text-field");
        searchField.setPrefWidth(320);
        searchField.textProperty().addListener((obs, old, newVal) -> filterProducts(newVal));
        
        header.getChildren().addAll(title, spacer, searchField);
        
        // Container de productos
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        productsContainer = new FlowPane(25, 25);
        productsContainer.setPadding(new Insets(15));
        productsContainer.setAlignment(Pos.CENTER);
        
        scrollPane.setContent(productsContainer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        getChildren().addAll(header, scrollPane);
    }
    
    public void refresh() {
        loadProducts();
    }
    
    private void loadProducts() {
        List<Product> products = productController.getAllProducts();
        products = products.stream()
                .sorted(Comparator.comparing(Product::getName))
                .collect(Collectors.toList());
        displayProducts(products);
    }
    
    private void filterProducts(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            loadProducts();
        } else {
            List<Product> products = productController.searchProducts(searchTerm);
            products = products.stream()
                    .sorted(Comparator.comparing(Product::getName))
                    .collect(Collectors.toList());
            displayProducts(products);
        }
    }
    
    private void displayProducts(List<Product> products) {
        productsContainer.getChildren().clear();
        
        if (products.isEmpty()) {
            VBox emptyBox = new VBox(10);
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPadding(new Insets(50));
            
            Label noProducts = new Label("No se encontraron productos");
            noProducts.setStyle("-fx-font-size: 18px; -fx-text-fill: #7F8C8D; -fx-font-weight: bold;");
            
            emptyBox.getChildren().add(noProducts);
            productsContainer.getChildren().add(emptyBox);
            return;
        }
        
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            VBox card = createProductCard(product);
            productsContainer.getChildren().add(card);
            
            final int index = i;
            javafx.application.Platform.runLater(() -> {
                javafx.animation.PauseTransition pause = 
                    new javafx.animation.PauseTransition(javafx.util.Duration.millis(index * 50));
                pause.setOnFinished(e -> AnimationUtil.slideUp(card));
                pause.play();
            });
        }
    }
    
    private VBox createProductCard(Product product) {
        VBox card = new VBox(12);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(260);
        card.setMaxWidth(260);
        card.setMinWidth(260);
        card.setAlignment(Pos.TOP_CENTER);
        
        // Container para la imagen
        StackPane imageContainer = new StackPane();
        imageContainer.setPrefSize(240, 240);
        imageContainer.setMaxSize(240, 240);
        imageContainer.setMinSize(240, 240);
        imageContainer.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 12;");
        
        // Imagen del producto
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        
        try {
            File imageFile = new File(product.getImagePath());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                imageView.setImage(image);
            }
        } catch (Exception e) {
            // Sin imagen
        }
        
        imageContainer.getChildren().add(imageView);
        StackPane.setAlignment(imageView, Pos.CENTER);
        
        // Nombre del producto
        Label nameLabel = new Label(product.getName());
        nameLabel.getStyleClass().add("label-product-name");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(240);
        nameLabel.setAlignment(Pos.CENTER);
        
        // Precio
        Label priceLabel = new Label(CurrencyFormatter.formatPrice(product.getPrice()));
        priceLabel.getStyleClass().add("label-price");
        
        // Stock
        Label stockLabel = new Label(product.getStock() + " disponibles");
        stockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #27AE60; -fx-font-weight: bold;");
        
        // Botones
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        Button addToCartBtn = new Button("Agregar al Carrito");
        addToCartBtn.getStyleClass().add("btn-primary");
        addToCartBtn.setOnAction(e -> {
            addToCart(product);
            AnimationUtil.bounce(addToCartBtn);
        });
        
        // Botón de favoritos
        String heartIcon = userController.isInWishlist(product.getId()) ? "♥" : "♡";
        Button wishlistBtn = new Button(heartIcon);
        wishlistBtn.getStyleClass().add("btn-icon");
        wishlistBtn.setStyle("-fx-font-size: 18px; -fx-min-width: 40px;");
        wishlistBtn.setOnAction(e -> toggleWishlist(product, wishlistBtn));
        
        buttonsBox.getChildren().addAll(addToCartBtn, wishlistBtn);
        
        AnimationUtil.scaleOnHover(card, 1.05);
        
        card.getChildren().addAll(imageContainer, nameLabel, priceLabel, stockLabel, buttonsBox);
        
        return card;
    }
    
    private void addToCart(Product product) {
        if (product.getStock() > 0) {
            CartItem item = new CartItem(product, 1);
            session.addToCart(item);
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Producto agregado");
            alert.setHeaderText(null);
            alert.setContentText(product.getName() + " ha sido agregado al carrito");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin stock");
            alert.setHeaderText(null);
            alert.setContentText("Este producto no está disponible");
            alert.showAndWait();
        }
    }
    
    private void toggleWishlist(Product product, Button btn) {
        if (userController.isInWishlist(product.getId())) {
            userController.removeFromWishlist(product.getId());
            btn.setText("♡");
            AnimationUtil.shake(btn);
        } else {
            userController.addToWishlist(product.getId());
            btn.setText("♥");
            AnimationUtil.bounce(btn);
        }
    }
}
