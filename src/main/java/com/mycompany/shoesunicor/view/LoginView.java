package com.mycompany.shoesunicor.view;

import com.mycompany.shoesunicor.controller.AuthController;
import com.mycompany.shoesunicor.util.AnimationUtil;
import com.mycompany.shoesunicor.util.Session;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

/**
 * Vista de Login - Versión mejorada
 * @author Victor Negrete
 */
public class LoginView extends VBox {
    private TextField usernameField;
    private PasswordField passwordField;
    private Label errorLabel;
    private AuthController authController;
    private Runnable onLoginSuccess;
    private Runnable onRegisterClick;

    public LoginView(Runnable onLoginSuccess, Runnable onRegisterClick) {
        this.authController = new AuthController();
        this.onLoginSuccess = onLoginSuccess;
        this.onRegisterClick = onRegisterClick;

        setupUI();
        AnimationUtil.fadeIn(this);
    }

    private void setupUI() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(50));
        setSpacing(20);
        getStyleClass().add("root");

        // Container principal
        VBox mainContainer = new VBox(25);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.getStyleClass().add("main-container");
        mainContainer.setMaxWidth(450);
        mainContainer.setPadding(new Insets(40));

        // Logo/Título con emoji funcional
        Text title = new Text("\u2705 UNICOR SHOES");  // Checkmark verde
        title.getStyleClass().add("title");
        title.setStyle("-fx-fill: #2ECC71; -fx-font-size: 38px; -fx-font-weight: bold;");

        Text subtitle = new Text("\uD83D\uDC4B Bienvenido");  // Waving hand
        subtitle.getStyleClass().add("subtitle");

        // Campo de usuario
        Label usernameLabel = new Label("Usuario");
        usernameLabel.getStyleClass().add("label-field");

        usernameField = new TextField();
        usernameField.setPromptText("Ingresa tu usuario");
        usernameField.getStyleClass().add("text-field");
        usernameField.setMaxWidth(Double.MAX_VALUE);

        // Campo de contraseña
        Label passwordLabel = new Label("Contraseña");
        passwordLabel.getStyleClass().add("label-field");

        passwordField = new PasswordField();
        passwordField.setPromptText("Ingresa tu contraseña");
        passwordField.getStyleClass().add("password-field");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        // Label de error
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error-label");
        errorLabel.setVisible(false);

        // Botón de login
        Button loginButton = new Button("Iniciar Sesión");
        loginButton.getStyleClass().add("btn-primary");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin());

        // Enter para login
        passwordField.setOnAction(e -> handleLogin());

        // Link de registro con emoji
        HBox registerBox = new HBox(5);
        registerBox.setAlignment(Pos.CENTER);
        Label noAccountLabel = new Label("\u2753 ¿No tienes cuenta?");  // Question mark
        Hyperlink registerLink = new Hyperlink("\u2192 Regístrate aquí");  // Arrow
        registerLink.setOnAction(e -> onRegisterClick.run());
        registerLink.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        registerBox.getChildren().addAll(noAccountLabel, registerLink);

        // Agregar elementos
        mainContainer.getChildren().addAll(
                title,
                subtitle,
                new VBox(5, usernameLabel, usernameField),
                new VBox(5, passwordLabel, passwordField),
                errorLabel,
                loginButton,
                registerBox
        );

        getChildren().add(mainContainer);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Validaciones
        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor completa todos los campos");
            return;
        }

        // Intentar login
        if (authController.login(username, password)) {
            errorLabel.setVisible(false);
            AnimationUtil.fadeOut(this, javafx.util.Duration.millis(300));
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                onLoginSuccess.run();
            });
        } else {
            showError("\u274C Usuario o contraseña incorrectos");  // X mark
            AnimationUtil.shake(errorLabel);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        AnimationUtil.shake(errorLabel);
    }
}
