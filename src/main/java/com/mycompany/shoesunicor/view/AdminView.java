package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.OrderController;
import com.mycompany.shoesunicor.controller.ProductController;
import com.mycompany.shoesunicor.controller.UserController;
import com.mycompany.shoesunicor.model.*;
import com.mycompany.shoesunicor.util.AnimationUtil;
import com.mycompany.shoesunicor.util.CurrencyFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Vista de administrador mejorada con gestión completa de productos
 * @author Victor Negrete
 */
public class AdminView extends VBox {
    private ProductController productController;
    private OrderController orderController;
    private UserController userController;
    
    private TabPane tabPane;
    private TableView<Product> productsTable;
    private TableView<Order> ordersTable;
    private TableView<User> usersTable;
    private TextField searchProductField;
    private FilteredList<Product> filteredProducts;
    
    // Estadísticas
    private Label totalProductsLabel;
    private Label activeProductsLabel;
    private Label lowStockLabel;
    
    public AdminView() {
        this.productController = new ProductController();
        this.orderController = new OrderController();
        this.userController = new UserController();
        
        setupUI();
        AnimationUtil.fadeIn(this);
    }
    
    private void setupUI() {
        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("root");
        
        // Header
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Text title = new Text("⚙️ Panel de Administración");
        title.getStyleClass().add("subtitle");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Botón de refrescar
        Button refreshBtn = new Button("Actualizar Datos");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setTooltip(new Tooltip("Recargar todos los datos de la base de datos"));
        refreshBtn.setOnAction(e -> {
            refresh();
            AnimationUtil.bounce(refreshBtn);
        });
        
        header.getChildren().addAll(title, spacer, refreshBtn);
        
        // Tabs
        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);
        
        // Tab de productos
        Tab productsTab = new Tab("📦 Productos", createProductsTab());
        
        // Tab de órdenes
        Tab ordersTab = new Tab("🛒 Pedidos", createOrdersTab());
        
        // Tab de usuarios
        Tab usersTab = new Tab("👥 Usuarios", createUsersTab());
        
        tabPane.getTabs().addAll(productsTab, ordersTab, usersTab);
        
        getChildren().addAll(header, tabPane);
    }
    
    private VBox createProductsTab() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(15));
        
        // Panel de estadísticas
        HBox statsPanel = createStatsPanel();
        
        // Barra de herramientas
        HBox toolbar = new HBox(15);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        
        // Botón agregar producto
        Button addProductBtn = new Button("+ Agregar Producto");
        addProductBtn.getStyleClass().add("btn-primary");
        addProductBtn.setTooltip(new Tooltip("Añadir un nuevo producto al catálogo"));
        addProductBtn.setOnAction(e -> showAddProductDialog());
        
        // Búsqueda
        searchProductField = new TextField();
        searchProductField.setPromptText("Buscar por nombre, ID, categoría...");
        searchProductField.getStyleClass().add("text-field");
        searchProductField.setPrefWidth(280);
        searchProductField.setTooltip(new Tooltip("Escribe para buscar productos"));
        searchProductField.textProperty().addListener((obs, old, newVal) -> filterProducts(newVal));
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Filtro por estado
        Label filterLabel = new Label("Filtrar por:");
        filterLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("Todos los productos", "Solo activos", "Solo inactivos", "Sin stock (agotados)", "Stock bajo (1-5)");
        statusFilter.setValue("Todos los productos");
        statusFilter.setPrefWidth(160);
        statusFilter.setTooltip(new Tooltip("Filtrar productos por su estado"));
        statusFilter.setOnAction(e -> {
            String selected = statusFilter.getValue();
            String filter = switch (selected) {
                case "Solo activos" -> "Activos";
                case "Solo inactivos" -> "Inactivos";
                case "Sin stock (agotados)" -> "Sin Stock";
                case "Stock bajo (1-5)" -> "Stock Bajo";
                default -> "Todos";
            };
            applyStatusFilter(filter);
        });
        
        toolbar.getChildren().addAll(addProductBtn, searchProductField, spacer, 
                                     filterLabel, statusFilter);
        
        // Tabla de productos
        productsTable = new TableView<>();
        productsTable.getStyleClass().add("table-view");
        VBox.setVgrow(productsTable, Priority.ALWAYS);
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Columna ID
        TableColumn<Product, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(100);
        idCol.setMaxWidth(120);
        
        // Columna Imagen
        TableColumn<Product, Void> imageCol = new TableColumn<>("Imagen");
        imageCol.setPrefWidth(60);
        imageCol.setMaxWidth(70);
        imageCol.setCellFactory(col -> new TableCell<>() {
            private final ImageView imageView = new ImageView();
            {
                imageView.setFitWidth(40);
                imageView.setFitHeight(40);
                imageView.setPreserveRatio(true);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    try {
                        File imageFile = new File(product.getImagePath());
                        if (imageFile.exists()) {
                            Image image = new Image(imageFile.toURI().toString(), 40, 40, true, true);
                            imageView.setImage(image);
                            setGraphic(imageView);
                        } else {
                            setGraphic(new Label("📷"));
                        }
                    } catch (Exception e) {
                        setGraphic(new Label("📷"));
                    }
                }
            }
        });
        
        // Columna Nombre
        TableColumn<Product, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(180);
        
        // Columna Precio (formateado)
        TableColumn<Product, String> priceCol = new TableColumn<>("Precio");
        priceCol.setCellValueFactory(data -> 
            new SimpleStringProperty(CurrencyFormatter.formatPriceShort(data.getValue().getPrice())));
        priceCol.setPrefWidth(120);
        priceCol.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        // Columna Stock con indicadores de color
        TableColumn<Product, Void> stockCol = new TableColumn<>("Stock");
        stockCol.setPrefWidth(100);
        stockCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    int stock = product.getStock();
                    
                    Label stockLabel = new Label(String.valueOf(stock));
                    stockLabel.setPadding(new Insets(4, 10, 4, 10));
                    
                    if (stock == 0) {
                        stockLabel.setStyle("-fx-background-color: #F2D7D5; -fx-text-fill: #E74C3C; " +
                                          "-fx-background-radius: 12; -fx-font-weight: bold;");
                    } else if (stock <= 5) {
                        stockLabel.setStyle("-fx-background-color: #FCF3CF; -fx-text-fill: #D68910; " +
                                          "-fx-background-radius: 12; -fx-font-weight: bold;");
                    } else {
                        stockLabel.setStyle("-fx-background-color: #D5F4E6; -fx-text-fill: #27AE60; " +
                                          "-fx-background-radius: 12; -fx-font-weight: bold;");
                    }
                    
                    HBox container = new HBox(stockLabel);
                    container.setAlignment(Pos.CENTER);
                    setGraphic(container);
                }
            }
        });
        
        // Columna Categoría
        TableColumn<Product, String> categoryCol = new TableColumn<>("Categoría");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        categoryCol.setPrefWidth(100);
        
        // Columna Estado con badge visual
        TableColumn<Product, Void> activeCol = new TableColumn<>("Estado");
        activeCol.setPrefWidth(100);
        activeCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    Label badge = new Label(product.isActive() ? "✓ Activo" : "✗ Inactivo");
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    
                    if (product.isActive()) {
                        badge.setStyle("-fx-background-color: #D5F4E6; -fx-text-fill: #27AE60; " +
                                     "-fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                    } else {
                        badge.setStyle("-fx-background-color: #F2D7D5; -fx-text-fill: #E74C3C; " +
                                     "-fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 11px;");
                    }
                    
                    HBox container = new HBox(badge);
                    container.setAlignment(Pos.CENTER);
                    setGraphic(container);
                }
            }
        });
        
        // Columna de acciones con botones más claros
        TableColumn<Product, Void> actionsCol = new TableColumn<>("Acciones");
        actionsCol.setPrefWidth(280);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Editar");
            private final Button toggleBtn = new Button();
            private final Button stockBtn = new Button("Stock");
            private final Button priceBtn = new Button("Precio");
            
            {
                // Estilo botón Editar - Azul
                editBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; " +
                               "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                editBtn.setTooltip(new Tooltip("Editar todos los datos del producto"));
                
                // Estilo botón Stock - Naranja
                stockBtn.setStyle("-fx-background-color: #E67E22; -fx-text-fill: white; " +
                                "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                stockBtn.setTooltip(new Tooltip("Modificar cantidad en inventario"));
                
                // Estilo botón Precio - Verde
                priceBtn.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; " +
                                "-fx-font-size: 11px; -fx-padding: 5 10; -fx-background-radius: 5; -fx-cursor: hand;");
                priceBtn.setTooltip(new Tooltip("Cambiar el precio del producto"));
                
                editBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showEditProductDialog(product);
                });
                
                toggleBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    toggleProductStatus(product);
                });
                
                stockBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showQuickStockDialog(product);
                });
                
                priceBtn.setOnAction(e -> {
                    Product product = getTableView().getItems().get(getIndex());
                    showQuickPriceDialog(product);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Product product = getTableView().getItems().get(getIndex());
                    
                    // Botón toggle dinámico según estado
                    if (product.isActive()) {
                        toggleBtn.setText("Desactivar");
                        toggleBtn.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; " +
                                         "-fx-font-size: 11px; -fx-padding: 5 8; -fx-background-radius: 5; -fx-cursor: hand;");
                        toggleBtn.setTooltip(new Tooltip("Ocultar producto del catálogo"));
                    } else {
                        toggleBtn.setText("Activar");
                        toggleBtn.setStyle("-fx-background-color: #2ECC71; -fx-text-fill: white; " +
                                         "-fx-font-size: 11px; -fx-padding: 5 8; -fx-background-radius: 5; -fx-cursor: hand;");
                        toggleBtn.setTooltip(new Tooltip("Mostrar producto en el catálogo"));
                    }
                    
                    HBox buttons = new HBox(4, editBtn, priceBtn, stockBtn, toggleBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
        
        productsTable.getColumns().addAll(idCol, imageCol, nameCol, priceCol, 
                                         stockCol, categoryCol, activeCol, actionsCol);
        
        container.getChildren().addAll(statsPanel, toolbar, productsTable);
        
        // Cargar productos
        loadProducts();
        
        return container;
    }
    
    private HBox createStatsPanel() {
        HBox statsPanel = new HBox(30);
        statsPanel.setAlignment(Pos.CENTER_LEFT);
        statsPanel.setPadding(new Insets(15));
        statsPanel.setStyle("-fx-background-color: linear-gradient(to right, #FFFFFF, #F8F9FA); " +
                          "-fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Total productos
        VBox totalBox = createStatBox("📦 Total Productos", "0", "#3498DB");
        totalProductsLabel = (Label) ((VBox) totalBox.getChildren().get(0)).getChildren().get(1);
        
        // Productos activos
        VBox activeBox = createStatBox("✓ Activos", "0", "#27AE60");
        activeProductsLabel = (Label) ((VBox) activeBox.getChildren().get(0)).getChildren().get(1);
        
        // Stock bajo
        VBox lowStockBox = createStatBox("⚠️ Stock Bajo", "0", "#E67E22");
        lowStockLabel = (Label) ((VBox) lowStockBox.getChildren().get(0)).getChildren().get(1);
        
        statsPanel.getChildren().addAll(totalBox, activeBox, lowStockBox);
        
        return statsPanel;
    }
    
    private VBox createStatBox(String title, String value, String color) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(10, 25, 10, 25));
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
        
        box.getChildren().addAll(titleLabel, valueLabel);
        
        VBox wrapper = new VBox(box);
        return wrapper;
    }
    
    private void updateStats() {
        List<Product> allProducts = productController.getAllProductsAdmin();
        
        int total = allProducts.size();
        int active = (int) allProducts.stream().filter(Product::isActive).count();
        int lowStock = (int) allProducts.stream().filter(p -> p.getStock() > 0 && p.getStock() <= 5).count();
        
        totalProductsLabel.setText(String.valueOf(total));
        activeProductsLabel.setText(String.valueOf(active));
        lowStockLabel.setText(String.valueOf(lowStock));
    }
    
    private void filterProducts(String searchTerm) {
        if (filteredProducts != null) {
            filteredProducts.setPredicate(product -> {
                if (searchTerm == null || searchTerm.isEmpty()) {
                    return true;
                }
                String lowerSearch = searchTerm.toLowerCase();
                return product.getName().toLowerCase().contains(lowerSearch) ||
                       product.getId().toLowerCase().contains(lowerSearch) ||
                       product.getCategory().toLowerCase().contains(lowerSearch) ||
                       product.getBrand().toLowerCase().contains(lowerSearch);
            });
        }
    }
    
    private void applyStatusFilter(String filter) {
        if (filteredProducts == null) return;
        
        filteredProducts.setPredicate(product -> {
            String searchText = searchProductField.getText();
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    product.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                    product.getId().toLowerCase().contains(searchText.toLowerCase());
            
            if (!matchesSearch) return false;
            
            return switch (filter) {
                case "Activos" -> product.isActive();
                case "Inactivos" -> !product.isActive();
                case "Sin Stock" -> product.getStock() == 0;
                case "Stock Bajo" -> product.getStock() > 0 && product.getStock() <= 5;
                default -> true; // "Todos"
            };
        });
    }
    
    private VBox createOrdersTab() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(15));
        
        // Tabla de órdenes
        ordersTable = new TableView<>();
        ordersTable.getStyleClass().add("table-view");
        VBox.setVgrow(ordersTable, Priority.ALWAYS);
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<Order, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(150);
        
        TableColumn<Order, String> userCol = new TableColumn<>("Usuario");
        userCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        userCol.setPrefWidth(120);
        
        TableColumn<Order, String> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(data -> 
            new SimpleStringProperty(data.getValue().getFormattedDate()));
        dateCol.setPrefWidth(150);
        
        TableColumn<Order, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(data -> 
            new SimpleStringProperty(CurrencyFormatter.formatPriceShort(data.getValue().getTotal())));
        totalCol.setPrefWidth(120);
        
        TableColumn<Order, Void> statusCol = new TableColumn<>("Estado");
        statusCol.setPrefWidth(120);
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    Label badge = new Label(order.getStatus().toString());
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    
                    String style = switch (order.getStatus()) {
                        case PENDING -> "-fx-background-color: #FCF3CF; -fx-text-fill: #D68910;";
                        case PROCESSING -> "-fx-background-color: #D4EFDF; -fx-text-fill: #27AE60;";
                        case SHIPPED -> "-fx-background-color: #D6EAF8; -fx-text-fill: #2980B9;";
                        case DELIVERED -> "-fx-background-color: #D5F4E6; -fx-text-fill: #1D8348;";
                        case CANCELLED -> "-fx-background-color: #F2D7D5; -fx-text-fill: #E74C3C;";
                    };
                    badge.setStyle(style + " -fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 10px;");
                    
                    HBox container = new HBox(badge);
                    container.setAlignment(Pos.CENTER);
                    setGraphic(container);
                }
            }
        });
        
        TableColumn<Order, Void> actionsCol = new TableColumn<>("Cambiar Estado");
        actionsCol.setPrefWidth(220);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<OrderStatus> statusCombo = new ComboBox<>();
            private final Button updateBtn = new Button("Aplicar");
            
            {
                statusCombo.getItems().addAll(OrderStatus.values());
                statusCombo.setPrefWidth(120);
                statusCombo.setTooltip(new Tooltip("Seleccionar nuevo estado del pedido"));
                
                updateBtn.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; " +
                                 "-fx-font-size: 11px; -fx-padding: 5 12; -fx-background-radius: 5; -fx-cursor: hand;");
                updateBtn.setTooltip(new Tooltip("Guardar el nuevo estado"));
                updateBtn.setOnAction(e -> {
                    Order order = getTableView().getItems().get(getIndex());
                    OrderStatus newStatus = statusCombo.getValue();
                    if (newStatus != null) {
                        orderController.updateOrderStatus(order.getId(), newStatus);
                        refresh();
                    }
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Order order = getTableView().getItems().get(getIndex());
                    statusCombo.setValue(order.getStatus());
                    HBox box = new HBox(8, statusCombo, updateBtn);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });
        
        ordersTable.getColumns().addAll(idCol, userCol, dateCol, totalCol, statusCol, actionsCol);
        
        container.getChildren().add(ordersTable);
        return container;
    }
    
    private VBox createUsersTab() {
        VBox container = new VBox(15);
        container.setPadding(new Insets(15));
        
        // Tabla de usuarios
        usersTable = new TableView<>();
        usersTable.getStyleClass().add("table-view");
        VBox.setVgrow(usersTable, Priority.ALWAYS);
        usersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        TableColumn<User, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setPrefWidth(150);
        
        TableColumn<User, String> usernameCol = new TableColumn<>("Usuario");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);
        
        TableColumn<User, String> nameCol = new TableColumn<>("Nombre");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        nameCol.setPrefWidth(200);
        
        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(200);
        
        TableColumn<User, Void> roleCol = new TableColumn<>("Rol");
        roleCol.setPrefWidth(100);
        roleCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    Label badge = new Label(user.getRole().toString());
                    badge.setPadding(new Insets(4, 10, 4, 10));
                    
                    if (user.getRole() == UserRole.ADMIN) {
                        badge.setStyle("-fx-background-color: #E8DAEF; -fx-text-fill: #8E44AD; " +
                                     "-fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 10px;");
                    } else {
                        badge.setStyle("-fx-background-color: #D6EAF8; -fx-text-fill: #2980B9; " +
                                     "-fx-background-radius: 12; -fx-font-weight: bold; -fx-font-size: 10px;");
                    }
                    
                    HBox container = new HBox(badge);
                    container.setAlignment(Pos.CENTER);
                    setGraphic(container);
                }
            }
        });
        
        usersTable.getColumns().addAll(idCol, usernameCol, nameCol, emailCol, roleCol);
        
        container.getChildren().add(usersTable);
        return container;
    }
    
    public void refresh() {
        loadProducts();
        loadOrders();
        loadUsers();
        updateStats();
    }
    
    private void loadProducts() {
        List<Product> products = productController.getAllProductsAdmin();
        filteredProducts = new FilteredList<>(FXCollections.observableArrayList(products), p -> true);
        productsTable.setItems(null); // Limpiar primero para forzar actualización
        productsTable.setItems(filteredProducts);
        productsTable.refresh(); // Forzar refresh de todas las celdas
        updateStats();
    }
    
    private void loadOrders() {
        List<Order> orders = orderController.getAllOrders();
        ordersTable.setItems(FXCollections.observableArrayList(orders));
    }
    
    private void loadUsers() {
        List<User> users = userController.getAllUsers();
        usersTable.setItems(FXCollections.observableArrayList(users));
    }
    
    private void showAddProductDialog() {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Nuevo Producto");
        dialog.setHeaderText("➕ Agregar un nuevo producto al catálogo");
        
        ButtonType addButtonType = new ButtonType("Agregar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
        
        GridPane grid = createProductForm(null);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(500);
        
        // Validación
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    return getProductFromForm(grid, null);
                } catch (Exception e) {
                    showError("Error de validación", "Por favor verifica los datos ingresados.\n" + e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(product -> {
            if (product != null) {
                productController.addProduct(product);
                refresh();
                showSuccess("¡Producto agregado!", "El producto se ha añadido correctamente.");
            }
        });
    }
    
    private void showEditProductDialog(Product product) {
        Dialog<Product> dialog = new Dialog<>();
        dialog.setTitle("Editar Producto");
        dialog.setHeaderText("✏️ Modificar producto: " + product.getName());
        
        ButtonType saveButtonType = new ButtonType("Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = createProductForm(product);
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(500);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    return getProductFromForm(grid, product);
                } catch (Exception e) {
                    showError("Error de validación", "Por favor verifica los datos ingresados.\n" + e.getMessage());
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(updatedProduct -> {
            if (updatedProduct != null) {
                productController.updateProduct(updatedProduct);
                refresh();
                showSuccess("¡Producto actualizado!", "Los cambios se han guardado correctamente.");
            }
        });
    }
    
    private void showQuickStockDialog(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getStock()));
        dialog.setTitle("Cambiar Stock");
        dialog.setHeaderText("📦 " + product.getName());
        dialog.setContentText("Nuevo stock:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(stockStr -> {
            try {
                int newStock = Integer.parseInt(stockStr.trim());
                if (newStock < 0) {
                    showError("Error", "El stock no puede ser negativo.");
                    return;
                }
                
                boolean wasActive = product.isActive();
                product.setStock(newStock);
                productController.updateProduct(product);
                refresh();
                
                // Mensaje especial si se desactivó automáticamente
                if (newStock == 0 && wasActive) {
                    showSuccess("Stock actualizado", 
                        "El producto \"" + product.getName() + "\" ahora tiene stock 0.\n" +
                        "⚠️ Se ha DESACTIVADO automáticamente y aparecerá como 'Sin Stock' en el catálogo.");
                } else {
                    showSuccess("¡Stock actualizado!", "Stock de " + product.getName() + ": " + newStock);
                }
            } catch (NumberFormatException e) {
                showError("Error", "Por favor ingresa un número válido.");
            }
        });
    }
    
    private void showQuickPriceDialog(Product product) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(product.getPrice()));
        dialog.setTitle("Cambiar Precio");
        dialog.setHeaderText("💰 " + product.getName());
        dialog.setContentText("Nuevo precio (COP):");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(priceStr -> {
            try {
                double newPrice = Double.parseDouble(priceStr.trim().replace(",", "."));
                if (newPrice < 0) {
                    showError("Error", "El precio no puede ser negativo.");
                    return;
                }
                product.setPrice(newPrice);
                productController.updateProduct(product);
                refresh();
                showSuccess("¡Precio actualizado!", 
                    product.getName() + ": " + CurrencyFormatter.formatPrice(newPrice));
            } catch (NumberFormatException e) {
                showError("Error", "Por favor ingresa un precio válido.");
            }
        });
    }
    
    private void toggleProductStatus(Product product) {
        boolean newStatus = !product.isActive();
        
        // Si quiere activar pero no tiene stock, mostrar error
        if (newStatus && product.getStock() == 0) {
            showError("No se puede activar", 
                "El producto \"" + product.getName() + "\" no tiene stock disponible.\n\n" +
                "Primero debes agregar stock para poder activarlo.");
            return;
        }
        
        String action = product.isActive() ? "desactivar" : "activar";
        String message = "¿Deseas " + action + " el producto \"" + product.getName() + "\"?";
        
        if (newStatus) {
            message += "\n\nEl producto volverá a aparecer disponible en el catálogo.";
        } else {
            message += "\n\nEl producto aparecerá como 'Sin Stock' en el catálogo.";
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cambiar Estado");
        alert.setHeaderText(product.isActive() ? "🔴 Desactivar Producto" : "🟢 Activar Producto");
        alert.setContentText(message);
        
        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Usar método específico que no aplica lógica automática
            if (productController.toggleProductStatus(product.getId(), newStatus)) {
                refresh();
                String statusMsg = newStatus ? "activado" : "desactivado";
                showSuccess("Estado cambiado", "El producto ha sido " + statusMsg + ".");
            } else {
                showError("Error", "No se pudo cambiar el estado del producto.");
            }
        }
    }
    
    private GridPane createProductForm(Product product) {
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);
        grid.setPadding(new Insets(25));
        
        // Campos del formulario
        TextField nameField = new TextField(product != null ? product.getName() : "");
        nameField.setPromptText("Nombre del producto");
        nameField.getStyleClass().add("text-field");
        
        TextField priceField = new TextField(product != null ? String.valueOf(product.getPrice()) : "");
        priceField.setPromptText("Precio en COP");
        priceField.getStyleClass().add("text-field");
        
        Spinner<Integer> stockSpinner = new Spinner<>(0, 9999, product != null ? product.getStock() : 0);
        stockSpinner.setEditable(true);
        stockSpinner.getStyleClass().add("spinner");
        stockSpinner.setPrefWidth(150);
        
        ComboBox<String> categoryCombo = new ComboBox<>();
        categoryCombo.getItems().addAll("Deportivos", "Casuales", "Formales", "Running", "Basket");
        categoryCombo.setValue(product != null ? product.getCategory() : "Deportivos");
        categoryCombo.setPrefWidth(200);
        
        TextField brandField = new TextField(product != null ? product.getBrand() : "");
        brandField.setPromptText("Marca del producto");
        brandField.getStyleClass().add("text-field");
        
        TextArea descField = new TextArea(product != null ? product.getDescription() : "");
        descField.setPromptText("Descripción del producto...");
        descField.setPrefRowCount(3);
        descField.setWrapText(true);
        
        TextField imageField = new TextField(product != null ? product.getImagePath() : "");
        imageField.setPromptText("Ruta de la imagen");
        imageField.getStyleClass().add("text-field");
        
        Button browseBtn = new Button("Buscar...");
        browseBtn.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; " +
                         "-fx-font-size: 11px; -fx-padding: 8 12; -fx-background-radius: 5; -fx-cursor: hand;");
        browseBtn.setTooltip(new Tooltip("Seleccionar imagen desde el computador"));
        browseBtn.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar Imagen");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                imageField.setText(selectedFile.getAbsolutePath());
            }
        });
        
        HBox imageBox = new HBox(10, imageField, browseBtn);
        HBox.setHgrow(imageField, Priority.ALWAYS);
        
        CheckBox activeCheck = new CheckBox("Producto activo");
        activeCheck.setSelected(product == null || product.isActive());
        
        // Labels con estilo
        String labelStyle = "-fx-font-weight: bold; -fx-text-fill: #2C3E50;";
        
        Label nameLabel = new Label("Nombre *");
        nameLabel.setStyle(labelStyle);
        Label priceLabel = new Label("Precio (COP) *");
        priceLabel.setStyle(labelStyle);
        Label stockLabel = new Label("Stock *");
        stockLabel.setStyle(labelStyle);
        Label categoryLabel = new Label("Categoría");
        categoryLabel.setStyle(labelStyle);
        Label brandLabel = new Label("Marca");
        brandLabel.setStyle(labelStyle);
        Label descLabel = new Label("Descripción");
        descLabel.setStyle(labelStyle);
        Label imageLabel = new Label("Imagen");
        imageLabel.setStyle(labelStyle);
        
        grid.add(nameLabel, 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(priceLabel, 0, 1);
        grid.add(priceField, 1, 1);
        grid.add(stockLabel, 0, 2);
        grid.add(stockSpinner, 1, 2);
        grid.add(categoryLabel, 0, 3);
        grid.add(categoryCombo, 1, 3);
        grid.add(brandLabel, 0, 4);
        grid.add(brandField, 1, 4);
        grid.add(descLabel, 0, 5);
        grid.add(descField, 1, 5);
        grid.add(imageLabel, 0, 6);
        grid.add(imageBox, 1, 6);
        grid.add(activeCheck, 1, 7);
        
        GridPane.setHgrow(nameField, Priority.ALWAYS);
        GridPane.setHgrow(priceField, Priority.ALWAYS);
        GridPane.setHgrow(descField, Priority.ALWAYS);
        GridPane.setHgrow(imageBox, Priority.ALWAYS);
        
        // Store fields in grid properties for later retrieval
        grid.setUserData(new Object[]{nameField, priceField, stockSpinner, categoryCombo, 
                                      brandField, descField, imageField, activeCheck});
        
        return grid;
    }
    
    private Product getProductFromForm(GridPane grid, Product existingProduct) {
        Object[] fields = (Object[]) grid.getUserData();
        TextField nameField = (TextField) fields[0];
        TextField priceField = (TextField) fields[1];
        @SuppressWarnings("unchecked")
        Spinner<Integer> stockSpinner = (Spinner<Integer>) fields[2];
        @SuppressWarnings("unchecked")
        ComboBox<String> categoryCombo = (ComboBox<String>) fields[3];
        TextField brandField = (TextField) fields[4];
        TextArea descField = (TextArea) fields[5];
        TextField imageField = (TextField) fields[6];
        CheckBox activeCheck = (CheckBox) fields[7];
        
        // Validaciones
        if (nameField.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        
        double price;
        try {
            price = Double.parseDouble(priceField.getText().trim().replace(",", "."));
            if (price < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El precio debe ser un número válido y positivo.");
        }
        
        Product product = existingProduct != null ? existingProduct : new Product();
        
        product.setName(nameField.getText().trim());
        product.setPrice(price);
        product.setStock(stockSpinner.getValue());
        product.setCategory(categoryCombo.getValue());
        product.setBrand(brandField.getText().trim());
        product.setDescription(descField.getText().trim());
        product.setImagePath(imageField.getText().trim());
        product.setActive(activeCheck.isSelected());
        
        return product;
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
