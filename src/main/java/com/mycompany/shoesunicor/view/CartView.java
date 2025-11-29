package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.OrderController;
import com.mycompany.shoesunicor.model.CartItem;
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
 * Vista del carrito de compra
 * @author Victor Negrete
 */
public class CartView extends VBox {
    private Session session;
    private OrderController orderController;
    private VBox cartItemsContainer;
    private Label totalLabel;
    
    public CartView() {
        this.session = Session.getInstance();
        this.orderController = new OrderController();
        
        setupUI();
    }
    
    private void setupUI() {
        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("root");
        
        // Header
        Text title = new Text("🛒 Mi Carrito");
        title.getStyleClass().add("subtitle");
        
        // Scroll para items
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        
        cartItemsContainer = new VBox(15);
        cartItemsContainer.setPadding(new Insets(10));
        scrollPane.setContent(cartItemsContainer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        // Footer con total y botón de compra
        VBox footer = new VBox(15);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setPadding(new Insets(20));
        footer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        totalLabel = new Label("Total: $0.00");
        totalLabel.getStyleClass().add("label-price");
        
        Button checkoutBtn = new Button("Finalizar Compra");
        checkoutBtn.getStyleClass().add("btn-primary");
        checkoutBtn.setPrefWidth(250);
        checkoutBtn.setOnAction(e -> checkout());
        
        Button clearBtn = new Button("Vaciar Carrito");
        clearBtn.getStyleClass().add("btn-secondary");
        clearBtn.setPrefWidth(250);
        clearBtn.setOnAction(e -> clearCart());
        
        footer.getChildren().addAll(totalLabel, checkoutBtn, clearBtn);
        
        getChildren().addAll(title, scrollPane, footer);
    }
    
    public void refresh() {
        displayCartItems();
        updateTotal();
    }
    
    private void displayCartItems() {
        cartItemsContainer.getChildren().clear();
        
        if (session.getCart().isEmpty()) {
            Label emptyLabel = new Label("Tu carrito está vacío");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7F8C8D;");
            cartItemsContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (CartItem item : session.getCart()) {
            cartItemsContainer.getChildren().add(createCartItemCard(item));
        }
    }
    
    private HBox createCartItemCard(CartItem item) {
        HBox card = new HBox(20);
        card.getStyleClass().add("cart-item");
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        
        // Imagen
        ImageView imageView = new ImageView();
        imageView.setFitWidth(100);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        
        try {
            File imageFile = new File(item.getImagePath());
            if (imageFile.exists()) {
                imageView.setImage(new Image(imageFile.toURI().toString()));
            }
        } catch (Exception e) {
            // Imagen por defecto
        }
        
        // Info del producto
        VBox infoBox = new VBox(8);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        
        Label nameLabel = new Label(item.getProductName());
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        Label priceLabel = new Label(String.format("$%.2f", item.getPrice()));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #2ECC71;");
        
        infoBox.getChildren().addAll(nameLabel, priceLabel);
        
        // Control de cantidad
        HBox quantityBox = new HBox(10);
        quantityBox.setAlignment(Pos.CENTER);
        
        Button decreaseBtn = new Button("-");
        decreaseBtn.getStyleClass().add("btn-secondary");
        decreaseBtn.setOnAction(e -> updateQuantity(item, -1));
        
        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-min-width: 30; -fx-alignment: center;");
        
        Button increaseBtn = new Button("+");
        increaseBtn.getStyleClass().add("btn-secondary");
        increaseBtn.setOnAction(e -> updateQuantity(item, 1));
        
        quantityBox.getChildren().addAll(decreaseBtn, quantityLabel, increaseBtn);
        
        // Subtotal
        Label subtotalLabel = new Label(String.format("$%.2f", item.getSubtotal()));
        subtotalLabel.getStyleClass().add("label-price");
        subtotalLabel.setMinWidth(100);
        subtotalLabel.setAlignment(Pos.CENTER_RIGHT);
        
        // Botón eliminar
        Button removeBtn = new Button("🗑️");
        removeBtn.getStyleClass().add("btn-danger");
        removeBtn.setOnAction(e -> removeItem(item));
        
        card.getChildren().addAll(imageView, infoBox, quantityBox, subtotalLabel, removeBtn);
        
        return card;
    }
    
    private void updateQuantity(CartItem item, int change) {
        int newQuantity = item.getQuantity() + change;
        if (newQuantity > 0) {
            item.setQuantity(newQuantity);
            refresh();
        } else {
            removeItem(item);
        }
    }
    
    private void removeItem(CartItem item) {
        session.removeFromCart(item);
        refresh();
    }
    
    private void clearCart() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Vaciar Carrito");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de vaciar el carrito?");
        
        if (alert.showAndWait().get() == ButtonType.OK) {
            session.clearCart();
            refresh();
        }
    }
    
    private void updateTotal() {
        double total = session.getCartTotal();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }
    
    private void checkout() {
        if (session.getCart().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Carrito vacío", 
                     "Agrega productos antes de finalizar la compra");
            return;
        }
        
        // Dialog para dirección de envío
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Finalizar Compra");
        dialog.setHeaderText("Ingresa tu dirección de envío");
        dialog.setContentText("Dirección:");
        
        dialog.showAndWait().ifPresent(address -> {
            if (address.trim().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "Debes ingresar una dirección");
                return;
            }
            
            if (orderController.createOrder(address)) {
                showAlert(Alert.AlertType.INFORMATION, "Compra exitosa", 
                         "Tu pedido ha sido registrado exitosamente");
                refresh();
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "No se pudo procesar la compra. Verifica el stock disponible.");
            }
        });
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

