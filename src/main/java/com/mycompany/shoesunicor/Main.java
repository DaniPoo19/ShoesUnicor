/*
 * Aplicación Principal - ShoesUnicor
 * Tienda Virtual de Zapatos - Universidad de Córdoba
 */
package com.mycompany.shoesunicor;

import com.mycompany.shoesunicor.util.DataInitializer;
import com.mycompany.shoesunicor.util.Session;
import com.mycompany.shoesunicor.view.LoginView;
import com.mycompany.shoesunicor.view.MainView;
import com.mycompany.shoesunicor.view.RegisterView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Aplicación principal Unicor Shoes
 * Funcionalidades: Login + Registro + Dashboard + Carrito + Logout
 * 
 * @author Victor Negrete
 */
public class Main extends Application {
    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        // Inicializar datos de la aplicación (usuarios y productos)
        DataInitializer.initializeData();

        this.primaryStage = primaryStage;

        // Configuración de la ventana principal
        primaryStage.setTitle("Unicor Shoes - Tienda Virtual");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);

        // Mostrar pantalla de login al iniciar
        showLogin();

        primaryStage.show();
    }

    /**
     * Muestra la vista de Login
     * Permite iniciar sesión o ir a registro
     */
    private void showLogin() {
        LoginView loginView = new LoginView(
                this::showMainView,    // Callback cuando login es exitoso
                this::showRegister     // Callback para ir a registro
        );

        scene = new Scene(loginView);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Unicor Shoes - Iniciar Sesión");
    }

    /**
     * Muestra la vista de Registro
     * Permite crear una nueva cuenta
     */
    private void showRegister() {
        RegisterView registerView = new RegisterView(
                this::showLogin,    // Callback cuando registro es exitoso (vuelve a login)
                this::showLogin     // Callback para volver a login
        );

        scene = new Scene(registerView);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Unicor Shoes - Crear Cuenta");
    }

    /**
     * Muestra la vista principal (Dashboard)
     * Contiene productos, carrito y opción de logout
     */
    private void showMainView() {
        MainView mainView = new MainView(this::showLogin);

        scene = new Scene(mainView);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);

        // Actualizar título con nombre de usuario logueado
        if (Session.getInstance().isLoggedIn()) {
            String userName = Session.getInstance().getCurrentUser().getFullName();
            primaryStage.setTitle("Unicor Shoes - " + userName);
        }
    }

    /**
     * Punto de entrada de la aplicación
     */
    public static void main(String[] args) {
        launch(args);
    }
}
