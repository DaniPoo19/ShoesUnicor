/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
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
 * @author Victor Negrete
 */
public class Main extends Application {
    private Stage primaryStage;
    private Scene scene;

    @Override
    public void start(Stage primaryStage) {
        // Inicializar datos de la aplicación
        DataInitializer.initializeData();

        this.primaryStage = primaryStage;

        // Configuración de la ventana
        primaryStage.setTitle("Unicor Shoes - Tienda Virtual");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(800);
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(600);

        // Mostrar login
        showLogin();

        primaryStage.show();
    }

    private void showLogin() {
        LoginView loginView = new LoginView(
                this::showMainView,
                this::showRegister
        );

        scene = new Scene(loginView);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showRegister() {
        RegisterView registerView = new RegisterView(
                this::showLogin,
                this::showLogin
        );

        scene = new Scene(registerView);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }

    private void showMainView() {
        MainView mainView = new MainView(this::showLogin);

        scene = new Scene(mainView);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        primaryStage.setScene(scene);

        // Actualizar título con nombre de usuario
        if (Session.getInstance().isLoggedIn()) {
            String userName = Session.getInstance().getCurrentUser().getFullName();
            primaryStage.setTitle("Unicor Shoes - " + userName);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
