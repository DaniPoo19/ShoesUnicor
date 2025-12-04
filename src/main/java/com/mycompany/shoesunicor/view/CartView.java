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
        
        // Dialog personalizado para dirección de envío completa
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🛒 Finalizar Compra");
        dialog.setHeaderText("Información de Envío");
        
        // Botones
        ButtonType confirmButtonType = new ButtonType("Confirmar Pedido", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        // Formulario de dirección
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(25));
        
        // Estilo para labels
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #2C3E50;";
        
        // Calle y número (obligatorio)
        Label streetLabel = new Label("Calle y Número *");
        streetLabel.setStyle(labelStyle);
        TextField streetField = new TextField();
        streetField.setPromptText("Ej: Calle 10 #15-20");
        streetField.setPrefWidth(300);
        
        // Apartamento/Piso (opcional)
        Label aptLabel = new Label("Apartamento/Piso");
        aptLabel.setStyle(labelStyle);
        TextField aptField = new TextField();
        aptField.setPromptText("Ej: Apto 301, Piso 3 (opcional)");
        
        // Ciudad (obligatorio)
        Label cityLabel = new Label("Ciudad *");
        cityLabel.setStyle(labelStyle);
        TextField cityField = new TextField();
        cityField.setPromptText("Ej: Montería");
        
        // Código Postal (obligatorio)
        Label postalLabel = new Label("Código Postal *");
        postalLabel.setStyle(labelStyle);
        TextField postalField = new TextField();
        postalField.setPromptText("Ej: 230001");
        postalField.setPrefWidth(150);
        
        // Notas adicionales (opcional)
        Label notesLabel = new Label("Notas de entrega");
        notesLabel.setStyle(labelStyle);
        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Instrucciones especiales para la entrega (opcional)");
        notesArea.setPrefRowCount(2);
        notesArea.setWrapText(true);
        
        // Resumen del pedido
        Label summaryLabel = new Label("📋 Resumen del Pedido");
        summaryLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3498DB;");
        
        VBox summaryBox = new VBox(5);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8;");
        
        int totalItems = session.getCartItemCount();
        double totalAmount = session.getCartTotal();
        
        Label itemsLabel = new Label("Productos: " + totalItems + " artículo(s)");
        itemsLabel.setStyle("-fx-font-size: 12px;");
        Label totalLabel = new Label("Total a pagar: " + String.format("$%.2f", totalAmount));
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27AE60;");
        
        summaryBox.getChildren().addAll(itemsLabel, totalLabel);
        
        // Nota de campos obligatorios
        Label requiredNote = new Label("* Campos obligatorios");
        requiredNote.setStyle("-fx-font-size: 11px; -fx-text-fill: #E74C3C;");
        
        // Añadir al grid
        grid.add(streetLabel, 0, 0);
        grid.add(streetField, 1, 0);
        grid.add(aptLabel, 0, 1);
        grid.add(aptField, 1, 1);
        grid.add(cityLabel, 0, 2);
        grid.add(cityField, 1, 2);
        grid.add(postalLabel, 0, 3);
        grid.add(postalField, 1, 3);
        grid.add(notesLabel, 0, 4);
        grid.add(notesArea, 1, 4);
        grid.add(new Separator(), 0, 5, 2, 1);
        grid.add(summaryLabel, 0, 6, 2, 1);
        grid.add(summaryBox, 0, 7, 2, 1);
        grid.add(requiredNote, 1, 8);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(500);
        
        // Validación y resultado
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                // Validar campos obligatorios
                String street = streetField.getText().trim();
                String city = cityField.getText().trim();
                String postal = postalField.getText().trim();
                
                if (street.isEmpty() || city.isEmpty() || postal.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Campos Incompletos", 
                        "Por favor completa todos los campos obligatorios:\n- Calle y Número\n- Ciudad\n- Código Postal");
                    return null;
                }
                
                // Construir dirección completa
                StringBuilder fullAddress = new StringBuilder();
                fullAddress.append(street);
                
                String apt = aptField.getText().trim();
                if (!apt.isEmpty()) {
                    fullAddress.append(", ").append(apt);
                }
                
                fullAddress.append(", ").append(city);
                fullAddress.append(" - CP: ").append(postal);
                
                String notes = notesArea.getText().trim();
                if (!notes.isEmpty()) {
                    fullAddress.append(" | Notas: ").append(notes);
                }
                
                return fullAddress.toString();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(address -> {
            if (address != null && !address.isEmpty()) {
                if (orderController.createOrder(address)) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("✓ Compra Exitosa");
                    successAlert.setHeaderText("¡Tu pedido ha sido registrado!");
                    successAlert.setContentText(
                        "Gracias por tu compra.\n\n" +
                        "📦 Tu pedido está siendo procesado.\n" +
                        "📍 Dirección de envío:\n" + address.split(" \\| Notas:")[0] + "\n\n" +
                        "Puedes revisar el estado en 'Mis Pedidos'."
                    );
                    successAlert.showAndWait();
                    refresh();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error en la Compra", 
                             "No se pudo procesar la compra.\nVerifica el stock disponible de los productos.");
                }
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

