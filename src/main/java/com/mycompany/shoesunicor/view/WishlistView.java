package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.ProductController;
import com.mycompany.shoesunicor.controller.UserController;
import com.mycompany.shoesunicor.model.CartItem;
import com.mycompany.shoesunicor.model.Product;
import com.mycompany.shoesunicor.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista de lista de deseos
 * @author Victor Negrete
 */
public class WishlistView extends VBox {
    private ProductController productController;
    private UserController userController;
    private Session session;
    private FlowPane wishlistContainer;
    
    public WishlistView() {
        this.productController = new ProductController();
        this.userController = new UserController();
        this.session = Session.getInstance();
        
        setupUI();
    }
    
    private void setupUI() {
        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("root");
        
        // Header
        Text title = new Text("❤️ Mis Favoritos");
        title.getStyleClass().add("subtitle");
        
        // Container de productos
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        wishlistContainer = new FlowPane(20, 20);
        wishlistContainer.setPadding(new Insets(10));
        
        scrollPane.setContent(wishlistContainer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        getChildren().addAll(title, scrollPane);
    }
    
    public void refresh() {
        loadWishlist();
    }
    
    private void loadWishlist() {
        wishlistContainer.getChildren().clear();
        
        List<String> wishlistIds = userController.getWishlistProductIds();
        
        if (wishlistIds.isEmpty()) {
            Label emptyLabel = new Label("Tu lista de favoritos está vacía");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7F8C8D;");
            wishlistContainer.getChildren().add(emptyLabel);
            return;
        }
        
        List<Product> wishlistProducts = wishlistIds.stream()
                .map(productController::getProductById)
                .filter(p -> p != null && p.isActive())
                .collect(Collectors.toList());
        
        for (Product product : wishlistProducts) {
            wishlistContainer.getChildren().add(createProductCard(product));
        }
    }
    
    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.getStyleClass().add("product-card");
        card.setPrefWidth(250);
        card.setAlignment(Pos.TOP_CENTER);
        
        // Imagen
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(220);
        imageView.setPreserveRatio(true);
        
        try {
            File imageFile = new File(product.getImagePath());
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            }
        } catch (Exception e) {
            // Imagen por defecto
        }
        
        imageView.getStyleClass().add("product-image");
        
        // Info
        Label nameLabel = new Label(product.getName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(220);
        
        Label priceLabel = new Label(String.format("$%.2f", product.getPrice()));
        priceLabel.getStyleClass().add("label-price");
        
        Label stockLabel = new Label(product.getStock() + " disponibles");
        stockLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        
        // Botones
        HBox buttonsBox = new HBox(10);
        buttonsBox.setAlignment(Pos.CENTER);
        
        Button addToCartBtn = new Button("🛒 Agregar");
        addToCartBtn.getStyleClass().add("btn-primary");
        addToCartBtn.setOnAction(e -> addToCart(product));
        
        Button removeBtn = new Button("❌");
        removeBtn.getStyleClass().add("btn-danger");
        removeBtn.setOnAction(e -> removeFromWishlist(product));
        
        buttonsBox.getChildren().addAll(addToCartBtn, removeBtn);
        
        card.getChildren().addAll(imageView, nameLabel, priceLabel, stockLabel, buttonsBox);
        
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
    
    private void removeFromWishlist(Product product) {
        userController.removeFromWishlist(product.getId());
        refresh();
    }
}

