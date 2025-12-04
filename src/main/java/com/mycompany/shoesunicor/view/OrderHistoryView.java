package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.OrderController;
import com.mycompany.shoesunicor.model.CartItem;
import com.mycompany.shoesunicor.model.Order;
import com.mycompany.shoesunicor.model.OrderStatus;
import com.mycompany.shoesunicor.util.AnimationUtil;
import com.mycompany.shoesunicor.util.CurrencyFormatter;
import com.mycompany.shoesunicor.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Vista de historial de pedidos con filtros y detalles
 * @author Victor Negrete
 */
public class OrderHistoryView extends VBox {
    private OrderController orderController;
    private Session session;
    private VBox ordersContainer;
    
    // Filtros
    private TextField searchField;
    private ComboBox<String> statusFilter;
    private DatePicker fromDatePicker;
    private DatePicker toDatePicker;
    
    // Panel de detalles
    private VBox detailsPanel;
    private Order selectedOrder;
    
    public OrderHistoryView() {
        this.orderController = new OrderController();
        this.session = Session.getInstance();
        
        setupUI();
        AnimationUtil.fadeIn(this);
    }
    
    private void setupUI() {
        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("root");
        
        // Header
        Text title = new Text("📦 Historial de Pedidos");
        title.getStyleClass().add("subtitle");
        
        // Panel de filtros
        VBox filtersPanel = createFiltersPanel();
        
        // Contenedor principal con dos paneles
        HBox mainContainer = new HBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(mainContainer, Priority.ALWAYS);
        
        // Panel izquierdo - Lista de pedidos
        VBox leftPanel = new VBox(15);
        leftPanel.setPrefWidth(450);
        leftPanel.setMinWidth(400);
        
        Label ordersLabel = new Label("📋 Mis Pedidos");
        ordersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        
        ordersContainer = new VBox(10);
        ordersContainer.setPadding(new Insets(10));
        scrollPane.setContent(ordersContainer);
        
        leftPanel.getChildren().addAll(ordersLabel, scrollPane);
        
        // Panel derecho - Detalles del pedido
        detailsPanel = createDetailsPanel();
        HBox.setHgrow(detailsPanel, Priority.ALWAYS);
        
        mainContainer.getChildren().addAll(leftPanel, detailsPanel);
        
        getChildren().addAll(title, filtersPanel, mainContainer);
    }
    
    private VBox createFiltersPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(15));
        panel.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label filterTitle = new Label("🔍 Filtrar y Buscar");
        filterTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        HBox filtersRow = new HBox(20);
        filtersRow.setAlignment(Pos.CENTER_LEFT);
        
        // Búsqueda por número de pedido
        VBox searchBox = new VBox(5);
        Label searchLabel = new Label("Número de Pedido:");
        searchLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        searchField = new TextField();
        searchField.setPromptText("Buscar por # de pedido...");
        searchField.setPrefWidth(200);
        searchField.textProperty().addListener((obs, old, newVal) -> applyFilters());
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        // Filtro por estado
        VBox statusBox = new VBox(5);
        Label statusLabel = new Label("Estado:");
        statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        statusFilter = new ComboBox<>();
        statusFilter.getItems().add("Todos los estados");
        for (OrderStatus status : OrderStatus.values()) {
            statusFilter.getItems().add(status.getDisplayName());
        }
        statusFilter.setValue("Todos los estados");
        statusFilter.setPrefWidth(150);
        statusFilter.setOnAction(e -> applyFilters());
        statusBox.getChildren().addAll(statusLabel, statusFilter);
        
        // Filtro por fecha desde
        VBox fromDateBox = new VBox(5);
        Label fromLabel = new Label("Desde:");
        fromLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        fromDatePicker = new DatePicker();
        fromDatePicker.setPromptText("Fecha inicio");
        fromDatePicker.setPrefWidth(140);
        fromDatePicker.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        fromDateBox.getChildren().addAll(fromLabel, fromDatePicker);
        
        // Filtro por fecha hasta
        VBox toDateBox = new VBox(5);
        Label toLabel = new Label("Hasta:");
        toLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        toDatePicker = new DatePicker();
        toDatePicker.setPromptText("Fecha fin");
        toDatePicker.setPrefWidth(140);
        toDatePicker.valueProperty().addListener((obs, old, newVal) -> applyFilters());
        toDateBox.getChildren().addAll(toLabel, toDatePicker);
        
        // Botón limpiar filtros
        Button clearBtn = new Button("🗑️ Limpiar");
        clearBtn.setStyle(
            "-fx-background-color: #95A5A6; " +
            "-fx-text-fill: white; " +
            "-fx-padding: 8 15; " +
            "-fx-background-radius: 8;"
        );
        clearBtn.setOnAction(e -> clearFilters());
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        filtersRow.getChildren().addAll(searchBox, statusBox, fromDateBox, toDateBox, spacer, clearBtn);
        panel.getChildren().addAll(filterTitle, filtersRow);
        
        return panel;
    }
    
    private VBox createDetailsPanel() {
        VBox panel = new VBox(15);
        panel.setPadding(new Insets(20));
        panel.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 12; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );
        
        Label placeholder = new Label("👈 Selecciona un pedido para ver los detalles");
        placeholder.setStyle("-fx-font-size: 16px; -fx-text-fill: #7F8C8D;");
        placeholder.setWrapText(true);
        
        panel.setAlignment(Pos.CENTER);
        panel.getChildren().add(placeholder);
        
        return panel;
    }
    
    private void showOrderDetails(Order order) {
        selectedOrder = order;
        detailsPanel.getChildren().clear();
        detailsPanel.setAlignment(Pos.TOP_LEFT);
        
        // Título
        Label title = new Label("📋 Detalles del Pedido");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        // ID del pedido
        String shortId = order.getId().length() > 8 ? 
            order.getId().substring(order.getId().length() - 8) : order.getId();
        Label idLabel = new Label("Pedido #" + shortId);
        idLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3498DB;");
        
        // Estado con badge
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusTitle = new Label("Estado:");
        statusTitle.setStyle("-fx-font-weight: bold;");
        Label statusBadge = new Label(order.getStatus().getDisplayName());
        statusBadge.setPadding(new Insets(5, 12, 5, 12));
        String badgeStyle = getStatusBadgeStyle(order.getStatus());
        statusBadge.setStyle(badgeStyle);
        statusBox.getChildren().addAll(statusTitle, statusBadge);
        
        Separator sep1 = new Separator();
        
        // Fecha del pedido
        Label dateTitle = new Label("📅 Fecha del Pedido:");
        dateTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label dateLabel = new Label(order.getFormattedDate());
        dateLabel.setStyle("-fx-font-size: 14px;");
        
        // Fecha estimada de entrega (simulada: +5 días hábiles)
        Label deliveryTitle = new Label("🚚 Fecha Estimada de Entrega:");
        deliveryTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        LocalDateTime estimatedDelivery = order.getOrderDate().plusDays(5);
        Label deliveryLabel = new Label(estimatedDelivery.format(
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        deliveryLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #27AE60;");
        
        Separator sep2 = new Separator();
        
        // Dirección de envío
        Label addressTitle = new Label("📍 Dirección de Envío:");
        addressTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        Label addressLabel = new Label(order.getShippingAddress());
        addressLabel.setWrapText(true);
        addressLabel.setStyle("-fx-font-size: 14px; -fx-background-color: #F8F9FA; -fx-padding: 10; -fx-background-radius: 8;");
        
        Separator sep3 = new Separator();
        
        // Productos incluidos
        Label productsTitle = new Label("🛒 Productos Incluidos:");
        productsTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        
        VBox productsBox = new VBox(8);
        productsBox.setPadding(new Insets(10));
        productsBox.setStyle("-fx-background-color: #F8F9FA; -fx-background-radius: 8;");
        
        for (CartItem item : order.getItems()) {
            HBox itemRow = new HBox(10);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            
            Label itemName = new Label("• " + item.getProductName());
            itemName.setStyle("-fx-font-size: 13px;");
            itemName.setMaxWidth(200);
            
            Label itemQty = new Label("x" + item.getQuantity());
            itemQty.setStyle("-fx-font-size: 13px; -fx-text-fill: #7F8C8D;");
            
            Region itemSpacer = new Region();
            HBox.setHgrow(itemSpacer, Priority.ALWAYS);
            
            Label itemPrice = new Label(CurrencyFormatter.formatPrice(item.getSubtotal()));
            itemPrice.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
            
            itemRow.getChildren().addAll(itemName, itemQty, itemSpacer, itemPrice);
            productsBox.getChildren().add(itemRow);
        }
        
        Separator sep4 = new Separator();
        
        // Total
        HBox totalBox = new HBox(10);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        totalBox.setPadding(new Insets(10, 0, 0, 0));
        
        Label totalTitle = new Label("TOTAL:");
        totalTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Label totalAmount = new Label(CurrencyFormatter.formatPrice(order.getTotal()));
        totalAmount.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #27AE60;");
        
        totalBox.getChildren().addAll(totalTitle, totalAmount);
        
        // Añadir todo al panel
        detailsPanel.getChildren().addAll(
            title, idLabel, statusBox, sep1,
            dateTitle, dateLabel,
            deliveryTitle, deliveryLabel, sep2,
            addressTitle, addressLabel, sep3,
            productsTitle, productsBox, sep4,
            totalBox
        );
        
        AnimationUtil.fadeIn(detailsPanel);
    }
    
    private String getStatusBadgeStyle(OrderStatus status) {
        String baseStyle = "-fx-background-radius: 15; -fx-font-weight: bold; -fx-font-size: 12px;";
        return switch (status) {
            case PENDING -> baseStyle + "-fx-background-color: #FCF3CF; -fx-text-fill: #D68910;";
            case PROCESSING -> baseStyle + "-fx-background-color: #D4EFDF; -fx-text-fill: #27AE60;";
            case SHIPPED -> baseStyle + "-fx-background-color: #D6EAF8; -fx-text-fill: #2980B9;";
            case DELIVERED -> baseStyle + "-fx-background-color: #D5F4E6; -fx-text-fill: #1D8348;";
            case CANCELLED -> baseStyle + "-fx-background-color: #F2D7D5; -fx-text-fill: #E74C3C;";
        };
    }
    
    public void refresh() {
        loadOrders();
    }
    
    private void loadOrders() {
        applyFilters();
    }
    
    private void applyFilters() {
        ordersContainer.getChildren().clear();
        
        List<Order> orders = orderController.getUserOrders(session.getCurrentUser().getId());
        
        // Filtrar por búsqueda de número de pedido
        String searchText = searchField.getText();
        if (searchText != null && !searchText.trim().isEmpty()) {
            orders = orders.stream()
                .filter(o -> o.getId().toLowerCase().contains(searchText.toLowerCase()))
                .collect(Collectors.toList());
        }
        
        // Filtrar por estado
        String selectedStatus = statusFilter.getValue();
        if (selectedStatus != null && !selectedStatus.equals("Todos los estados")) {
            orders = orders.stream()
                .filter(o -> o.getStatus().getDisplayName().equals(selectedStatus))
                .collect(Collectors.toList());
        }
        
        // Filtrar por fecha desde
        LocalDate fromDate = fromDatePicker.getValue();
        if (fromDate != null) {
            orders = orders.stream()
                .filter(o -> !o.getOrderDate().toLocalDate().isBefore(fromDate))
                .collect(Collectors.toList());
        }
        
        // Filtrar por fecha hasta
        LocalDate toDate = toDatePicker.getValue();
        if (toDate != null) {
            orders = orders.stream()
                .filter(o -> !o.getOrderDate().toLocalDate().isAfter(toDate))
                .collect(Collectors.toList());
        }
        
        if (orders.isEmpty()) {
            Label emptyLabel = new Label("No se encontraron pedidos");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7F8C8D;");
            ordersContainer.getChildren().add(emptyLabel);
            return;
        }
        
        for (Order order : orders) {
            ordersContainer.getChildren().add(createOrderCard(order));
        }
    }
    
    private void clearFilters() {
        searchField.clear();
        statusFilter.setValue("Todos los estados");
        fromDatePicker.setValue(null);
        toDatePicker.setValue(null);
        applyFilters();
    }
    
    private VBox createOrderCard(Order order) {
        VBox card = new VBox(8);
        card.setPadding(new Insets(12));
        card.setCursor(javafx.scene.Cursor.HAND);
        
        boolean isSelected = selectedOrder != null && selectedOrder.getId().equals(order.getId());
        String cardStyle = isSelected ? 
            "-fx-background-color: #EBF5FB; -fx-background-radius: 10; -fx-border-color: #3498DB; -fx-border-radius: 10; -fx-border-width: 2;" :
            "-fx-background-color: #F8F9FA; -fx-background-radius: 10; -fx-border-color: transparent; -fx-border-radius: 10;";
        card.setStyle(cardStyle);
        
        // Click para ver detalles
        card.setOnMouseClicked(e -> {
            showOrderDetails(order);
            applyFilters(); // Refrescar para actualizar selección visual
        });
        
        // Hover effect
        card.setOnMouseEntered(e -> {
            if (selectedOrder == null || !selectedOrder.getId().equals(order.getId())) {
                card.setStyle("-fx-background-color: #E8F6F3; -fx-background-radius: 10;");
            }
        });
        card.setOnMouseExited(e -> {
            boolean sel = selectedOrder != null && selectedOrder.getId().equals(order.getId());
            String style = sel ? 
                "-fx-background-color: #EBF5FB; -fx-background-radius: 10; -fx-border-color: #3498DB; -fx-border-radius: 10; -fx-border-width: 2;" :
                "-fx-background-color: #F8F9FA; -fx-background-radius: 10;";
            card.setStyle(style);
        });
        
        // Header de la orden
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        String shortId = order.getId().length() > 8 ? 
            order.getId().substring(order.getId().length() - 8) : order.getId();
        Label orderIdLabel = new Label("#" + shortId);
        orderIdLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2C3E50;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Estado con badge
        Label statusBadge = new Label(order.getStatus().getDisplayName());
        statusBadge.setPadding(new Insets(3, 8, 3, 8));
        statusBadge.setStyle(getStatusBadgeStyle(order.getStatus()));
        
        header.getChildren().addAll(orderIdLabel, spacer, statusBadge);
        
        // Fecha
        Label dateLabel = new Label("📅 " + order.getFormattedDate());
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        
        // Total y cantidad de items
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);
        
        Label itemsLabel = new Label(order.getTotalItems() + " producto(s)");
        itemsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7F8C8D;");
        
        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);
        
        Label totalLabel = new Label(CurrencyFormatter.formatPrice(order.getTotal()));
        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27AE60;");
        
        footer.getChildren().addAll(itemsLabel, footerSpacer, totalLabel);
        
        card.getChildren().addAll(header, dateLabel, footer);
        
        return card;
    }
}
