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

/**
 * Vista de detalles del producto con todas las funcionalidades
 * @author Victor Negrete
 */
public class ProductDetailView extends VBox {
    
    private Product product;
    private ProductController productController;
    private UserController userController;
    private Session session;
    private Runnable onBack;
    
    // Componentes de la vista
    private ImageView mainImageView;
    private Label nameLabel;
    private Label descriptionLabel;
    private Label priceLabel;
    private Label stockLabel;
    private ComboBox<String> sizeComboBox;
    private Spinner<Integer> quantitySpinner;
    private Button wishlistBtn;
    private Button addToCartBtn;
    
    // Tallas disponibles
    private static final String[] SIZES = {"36", "37", "38", "39", "40", "41", "42", "43", "44", "45"};
    
    public ProductDetailView(Product product, Runnable onBack) {
        this.product = product;
        this.onBack = onBack;
        this.productController = new ProductController();
        this.userController = new UserController();
        this.session = Session.getInstance();
        
        setupUI();
        loadProductData();
        
        AnimationUtil.fadeIn(this);
    }
    
    private void setupUI() {
        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("root");
        setAlignment(Pos.TOP_CENTER);
        
        // Contenedor principal con scroll
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        // Contenedor del contenido
        VBox contentContainer = new VBox(25);
        contentContainer.setPadding(new Insets(20));
        contentContainer.setAlignment(Pos.TOP_CENTER);
        contentContainer.setMaxWidth(1000);
        
        // Header con botón volver
        HBox header = createHeader();
        
        // Contenedor principal del producto (imagen + detalles)
        HBox productContainer = new HBox(40);
        productContainer.setAlignment(Pos.TOP_CENTER);
        productContainer.setMaxWidth(950);
        
        // Panel izquierdo - Imagen
        VBox imagePanel = createImagePanel();
        
        // Panel derecho - Detalles
        VBox detailsPanel = createDetailsPanel();
        
        HBox.setHgrow(detailsPanel, Priority.ALWAYS);
        productContainer.getChildren().addAll(imagePanel, detailsPanel);
        
        contentContainer.getChildren().addAll(header, productContainer);
        scrollPane.setContent(contentContainer);
        
        getChildren().add(scrollPane);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));
        
        // Botón volver
        Button backBtn = new Button("← Volver al Catálogo");
        backBtn.getStyleClass().add("btn-secondary");
        backBtn.setStyle("-fx-font-size: 14px; -fx-padding: 10 20;");
        backBtn.setOnAction(e -> {
            AnimationUtil.bounce(backBtn);
            if (onBack != null) {
                onBack.run();
            }
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Título
        Text title = new Text("Detalles del Producto");
        title.getStyleClass().add("subtitle");
        
        header.getChildren().addAll(backBtn, spacer, title, new Region());
        HBox.setHgrow(header.getChildren().get(3), Priority.ALWAYS);
        
        return header;
    }
    
    private VBox createImagePanel() {
        VBox imagePanel = new VBox(15);
        imagePanel.setAlignment(Pos.TOP_CENTER);
        imagePanel.setPadding(new Insets(10));
        imagePanel.setMinWidth(400);
        imagePanel.setMaxWidth(450);
        
        // Contenedor de la imagen principal con sombra
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 20, 0, 0, 5);"
        );
        imageContainer.setPadding(new Insets(20));
        imageContainer.setMinSize(400, 400);
        imageContainer.setMaxSize(450, 450);
        
        // Imagen principal ampliada
        mainImageView = new ImageView();
        mainImageView.setFitWidth(380);
        mainImageView.setFitHeight(380);
        mainImageView.setPreserveRatio(true);
        mainImageView.setSmooth(true);
        
        imageContainer.getChildren().add(mainImageView);
        
        // Badge de estado
        Label statusBadge = new Label();
        statusBadge.setPadding(new Insets(8, 16, 8, 16));
        statusBadge.setStyle(
            "-fx-background-radius: 20; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px;"
        );
        
        if (product.isActive() && product.getStock() > 0) {
            if (product.getStock() <= 5) {
                statusBadge.setText("⚡ ¡Últimas unidades!");
                statusBadge.setStyle(statusBadge.getStyle() + 
                    "-fx-background-color: #FEF9E7; -fx-text-fill: #D68910;");
            } else {
                statusBadge.setText("✓ Disponible");
                statusBadge.setStyle(statusBadge.getStyle() + 
                    "-fx-background-color: #D5F4E6; -fx-text-fill: #27AE60;");
            }
        } else {
            statusBadge.setText("✗ Sin Stock");
            statusBadge.setStyle(statusBadge.getStyle() + 
                "-fx-background-color: #F2D7D5; -fx-text-fill: #E74C3C;");
        }
        
        imagePanel.getChildren().addAll(imageContainer, statusBadge);
        
        return imagePanel;
    }
    
    private VBox createDetailsPanel() {
        VBox detailsPanel = new VBox(20);
        detailsPanel.setAlignment(Pos.TOP_LEFT);
        detailsPanel.setPadding(new Insets(10, 20, 20, 20));
        detailsPanel.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 20; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 15, 0, 0, 3);"
        );
        
        // Nombre del producto
        nameLabel = new Label();
        nameLabel.setWrapText(true);
        nameLabel.setStyle(
            "-fx-font-size: 28px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #2C3E50;"
        );
        
        // Marca
        Label brandLabel = new Label("Marca: " + product.getBrand());
        brandLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D;");
        
        // Categoría
        Label categoryLabel = new Label("Categoría: " + product.getCategory());
        categoryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D;");
        
        // Separador
        Separator sep1 = new Separator();
        sep1.setStyle("-fx-background-color: #ECF0F1;");
        
        // Precio
        priceLabel = new Label();
        priceLabel.setStyle(
            "-fx-font-size: 32px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: #27AE60;"
        );
        
        // Stock disponible
        stockLabel = new Label();
        stockLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Separador
        Separator sep2 = new Separator();
        sep2.setStyle("-fx-background-color: #ECF0F1;");
        
        // Descripción
        Label descTitle = new Label("📝 Descripción");
        descTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        descriptionLabel = new Label();
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-text-fill: #5D6D7E; " +
            "-fx-line-spacing: 5;"
        );
        
        // Separador
        Separator sep3 = new Separator();
        sep3.setStyle("-fx-background-color: #ECF0F1;");
        
        // Sección de compra
        VBox purchaseSection = createPurchaseSection();
        
        detailsPanel.getChildren().addAll(
            nameLabel, brandLabel, categoryLabel, sep1,
            priceLabel, stockLabel, sep2,
            descTitle, descriptionLabel, sep3,
            purchaseSection
        );
        
        return detailsPanel;
    }
    
    private VBox createPurchaseSection() {
        VBox section = new VBox(15);
        section.setAlignment(Pos.CENTER_LEFT);
        
        Label sectionTitle = new Label("🛒 Opciones de Compra");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        // Selector de talla
        HBox sizeBox = new HBox(15);
        sizeBox.setAlignment(Pos.CENTER_LEFT);
        
        Label sizeLabel = new Label("Talla:");
        sizeLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        sizeLabel.setMinWidth(80);
        
        sizeComboBox = new ComboBox<>();
        sizeComboBox.getItems().addAll(SIZES);
        sizeComboBox.setValue("40"); // Talla por defecto
        sizeComboBox.setStyle("-fx-font-size: 14px;");
        sizeComboBox.setPrefWidth(100);
        sizeComboBox.setTooltip(new Tooltip("Selecciona tu talla"));
        
        sizeBox.getChildren().addAll(sizeLabel, sizeComboBox);
        
        // Selector de cantidad
        HBox quantityBox = new HBox(15);
        quantityBox.setAlignment(Pos.CENTER_LEFT);
        
        Label quantityLabel = new Label("Cantidad:");
        quantityLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        quantityLabel.setMinWidth(80);
        
        int maxStock = Math.max(1, product.getStock());
        quantitySpinner = new Spinner<>(1, maxStock, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(100);
        quantitySpinner.setTooltip(new Tooltip("Mínimo 1, máximo " + maxStock));
        
        // Validar entrada manual
        quantitySpinner.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                quantitySpinner.getEditor().setText(oldVal);
            }
        });
        
        Label maxLabel = new Label("(máx. " + maxStock + ")");
        maxLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        
        quantityBox.getChildren().addAll(quantityLabel, quantitySpinner, maxLabel);
        
        // Subtotal dinámico
        Label subtotalLabel = new Label("Subtotal: " + CurrencyFormatter.formatPrice(product.getPrice()));
        subtotalLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            double subtotal = product.getPrice() * newVal;
            subtotalLabel.setText("Subtotal: " + CurrencyFormatter.formatPrice(subtotal));
        });
        
        // Botones de acción
        HBox buttonsBox = new HBox(15);
        buttonsBox.setAlignment(Pos.CENTER_LEFT);
        buttonsBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Botón agregar al carrito
        addToCartBtn = new Button("🛒 Agregar al Carrito");
        addToCartBtn.getStyleClass().add("btn-primary");
        addToCartBtn.setStyle(
            "-fx-font-size: 16px; " +
            "-fx-padding: 15 30; " +
            "-fx-background-radius: 10;"
        );
        addToCartBtn.setOnAction(e -> addToCart());
        
        // Botón wishlist
        boolean isInWishlist = userController.isInWishlist(product.getId());
        wishlistBtn = new Button(isInWishlist ? "♥ En Favoritos" : "♡ Agregar a Favoritos");
        wishlistBtn.setStyle(
            "-fx-font-size: 14px; " +
            "-fx-padding: 15 25; " +
            "-fx-background-radius: 10; " +
            "-fx-background-color: " + (isInWishlist ? "#E74C3C" : "#ECF0F1") + "; " +
            "-fx-text-fill: " + (isInWishlist ? "white" : "#2C3E50") + "; " +
            "-fx-cursor: hand;"
        );
        wishlistBtn.setOnAction(e -> toggleWishlist());
        
        buttonsBox.getChildren().addAll(addToCartBtn, wishlistBtn);
        
        // Deshabilitar si no hay stock
        boolean isAvailable = product.isActive() && product.getStock() > 0;
        if (!isAvailable) {
            addToCartBtn.setDisable(true);
            addToCartBtn.setText("No Disponible");
            addToCartBtn.setStyle(
                "-fx-font-size: 16px; " +
                "-fx-padding: 15 30; " +
                "-fx-background-radius: 10; " +
                "-fx-background-color: #BDC3C7; " +
                "-fx-text-fill: #7F8C8D;"
            );
            sizeComboBox.setDisable(true);
            quantitySpinner.setDisable(true);
        }
        
        section.getChildren().addAll(sectionTitle, sizeBox, quantityBox, subtotalLabel, buttonsBox);
        
        return section;
    }
    
    private void loadProductData() {
        // Cargar imagen
        try {
            File imageFile = new File(product.getImagePath());
            if (imageFile.exists()) {
                Image image = new Image(imageFile.toURI().toString());
                mainImageView.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + e.getMessage());
        }
        
        // Cargar datos
        nameLabel.setText(product.getName());
        descriptionLabel.setText(product.getDescription());
        priceLabel.setText(CurrencyFormatter.formatPrice(product.getPrice()));
        
        // Stock con formato visual
        int stock = product.getStock();
        if (stock == 0) {
            stockLabel.setText("❌ Sin stock disponible");
            stockLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E74C3C;");
        } else if (stock <= 5) {
            stockLabel.setText("⚠️ Solo quedan " + stock + " unidades");
            stockLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #E67E22;");
        } else {
            stockLabel.setText("✓ " + stock + " unidades disponibles");
            stockLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27AE60;");
        }
    }
    
    private void addToCart() {
        if (product.getStock() <= 0) {
            showAlert(Alert.AlertType.WARNING, "Sin Stock", 
                "Este producto no está disponible actualmente.");
            return;
        }
        
        int quantity = quantitySpinner.getValue();
        String size = sizeComboBox.getValue();
        
        if (quantity > product.getStock()) {
            showAlert(Alert.AlertType.WARNING, "Stock Insuficiente", 
                "Solo hay " + product.getStock() + " unidades disponibles.");
            return;
        }
        
        // Crear item del carrito
        CartItem item = new CartItem(product, quantity);
        session.addToCart(item);
        
        // Animación y confirmación
        AnimationUtil.bounce(addToCartBtn);
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✓ Producto Agregado");
        alert.setHeaderText(null);
        alert.setContentText(
            "Se agregó al carrito:\n\n" +
            "• " + product.getName() + "\n" +
            "• Talla: " + size + "\n" +
            "• Cantidad: " + quantity + "\n" +
            "• Subtotal: " + CurrencyFormatter.formatPrice(product.getPrice() * quantity)
        );
        alert.showAndWait();
    }
    
    private void toggleWishlist() {
        boolean isInWishlist = userController.isInWishlist(product.getId());
        
        if (isInWishlist) {
            userController.removeFromWishlist(product.getId());
            wishlistBtn.setText("♡ Agregar a Favoritos");
            wishlistBtn.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-padding: 15 25; " +
                "-fx-background-radius: 10; " +
                "-fx-background-color: #ECF0F1; " +
                "-fx-text-fill: #2C3E50; " +
                "-fx-cursor: hand;"
            );
            AnimationUtil.shake(wishlistBtn);
        } else {
            userController.addToWishlist(product.getId());
            wishlistBtn.setText("♥ En Favoritos");
            wishlistBtn.setStyle(
                "-fx-font-size: 14px; " +
                "-fx-padding: 15 25; " +
                "-fx-background-radius: 10; " +
                "-fx-background-color: #E74C3C; " +
                "-fx-text-fill: white; " +
                "-fx-cursor: hand;"
            );
            AnimationUtil.bounce(wishlistBtn);
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

