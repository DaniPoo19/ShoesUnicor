package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.AuthController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/**
 * Vista de Registro
 * @author Victor Negrete
 */
public class RegisterView extends VBox {
    private TextField usernameField;
    private TextField emailField;
    private TextField fullNameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private Label messageLabel;
    private AuthController authController;
    private Runnable onRegisterSuccess;
    private Runnable onBackToLogin;
    
    public RegisterView(Runnable onRegisterSuccess, Runnable onBackToLogin) {
        this.authController = new AuthController();
        this.onRegisterSuccess = onRegisterSuccess;
        this.onBackToLogin = onBackToLogin;
        
        setupUI();
    }
    
    private void setupUI() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(30));
        setSpacing(20);
        getStyleClass().add("root");
        
        // Container principal
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.getStyleClass().add("main-container");
        mainContainer.setMaxWidth(500);
        mainContainer.setPadding(new Insets(40));
        
        // Título
        Text title = new Text("🟢 CREAR CUENTA");
        title.getStyleClass().add("title");
        
        Text subtitle = new Text("Únete a Unicor Shoes");
        subtitle.getStyleClass().add("subtitle");
        
        // Nombre completo
        Label fullNameLabel = new Label("Nombre Completo");
        fullNameLabel.getStyleClass().add("label-field");
        fullNameField = new TextField();
        fullNameField.setPromptText("Ej: Juan Pérez");
        fullNameField.getStyleClass().add("text-field");
        
        // Usuario
        Label usernameLabel = new Label("Usuario");
        usernameLabel.getStyleClass().add("label-field");
        usernameField = new TextField();
        usernameField.setPromptText("Elige un nombre de usuario");
        usernameField.getStyleClass().add("text-field");
        
        // Email
        Label emailLabel = new Label("Email");
        emailLabel.getStyleClass().add("label-field");
        emailField = new TextField();
        emailField.setPromptText("correo@ejemplo.com");
        emailField.getStyleClass().add("text-field");
        
        // Contraseña
        Label passwordLabel = new Label("Contraseña");
        passwordLabel.getStyleClass().add("label-field");
        passwordField = new PasswordField();
        passwordField.setPromptText("Mínimo 6 caracteres");
        passwordField.getStyleClass().add("password-field");
        
        // Confirmar contraseña
        Label confirmPasswordLabel = new Label("Confirmar Contraseña");
        confirmPasswordLabel.getStyleClass().add("label-field");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Repite tu contraseña");
        confirmPasswordField.getStyleClass().add("password-field");
        
        // Label de mensaje
        messageLabel = new Label();
        messageLabel.setVisible(false);
        messageLabel.setWrapText(true);
        
        // Botón de registro
        Button registerButton = new Button("Registrarse");
        registerButton.getStyleClass().add("btn-primary");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setOnAction(e -> handleRegister());
        
        // Botón de volver
        Button backButton = new Button("Volver al Login");
        backButton.getStyleClass().add("btn-secondary");
        backButton.setMaxWidth(Double.MAX_VALUE);
        backButton.setOnAction(e -> onBackToLogin.run());
        
        // Agregar elementos
        mainContainer.getChildren().addAll(
            title,
            subtitle,
            new VBox(5, fullNameLabel, fullNameField),
            new VBox(5, usernameLabel, usernameField),
            new VBox(5, emailLabel, emailField),
            new VBox(5, passwordLabel, passwordField),
            new VBox(5, confirmPasswordLabel, confirmPasswordField),
            messageLabel,
            registerButton,
            backButton
        );
        
        getChildren().add(mainContainer);
    }
    
    private void handleRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validaciones
        if (fullName.isEmpty() || username.isEmpty() || email.isEmpty() || 
            password.isEmpty() || confirmPassword.isEmpty()) {
            showError("Por favor completa todos los campos");
            return;
        }
        
        if (!authController.isValidEmail(email)) {
            showError("Email inválido");
            return;
        }
        
        if (!authController.isValidPassword(password)) {
            showError("La contraseña debe tener al menos 6 caracteres");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden");
            return;
        }
        
        // Intentar registro
        if (authController.register(username, password, email, fullName)) {
            showSuccess("¡Cuenta creada exitosamente!");
            // Esperar un momento y volver al login
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    javafx.application.Platform.runLater(onRegisterSuccess);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showError("El usuario o email ya están registrados");
        }
    }
    
    private void showError(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("error-label");
        messageLabel.setVisible(true);
    }
    
    private void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.getStyleClass().clear();
        messageLabel.getStyleClass().add("success-label");
        messageLabel.setVisible(true);
    }
}

