package com.mycompany.shoesunicor.util;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Utilidad para animaciones modernas en JavaFX
 * @author Victor Negrete
 */
public class AnimationUtil {
    
    /**
     * Animación de entrada con fade
     */
    public static void fadeIn(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }
    
    /**
     * Animación de entrada con fade (duración por defecto)
     */
    public static void fadeIn(Node node) {
        fadeIn(node, Duration.millis(500));
    }
    
    /**
     * Animación de salida con fade
     */
    public static void fadeOut(Node node, Duration duration) {
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
    }
    
    /**
     * Animación de escala al hacer hover
     */
    public static void scaleOnHover(Node node, double scaleFactor) {
        node.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(scaleFactor);
            scale.setToY(scaleFactor);
            scale.play();
        });
        
        node.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
        });
    }
    
    /**
     * Animación de rebote
     */
    public static void bounce(Node node) {
        ScaleTransition scale1 = new ScaleTransition(Duration.millis(100), node);
        scale1.setToX(1.1);
        scale1.setToY(1.1);
        
        ScaleTransition scale2 = new ScaleTransition(Duration.millis(100), node);
        scale2.setToX(0.9);
        scale2.setToY(0.9);
        
        ScaleTransition scale3 = new ScaleTransition(Duration.millis(100), node);
        scale3.setToX(1.0);
        scale3.setToY(1.0);
        
        SequentialTransition seq = new SequentialTransition(scale1, scale2, scale3);
        seq.play();
    }
    
    /**
     * Animación de deslizamiento desde abajo
     */
    public static void slideUp(Node node, Duration duration) {
        TranslateTransition translate = new TranslateTransition(duration, node);
        translate.setFromY(50);
        translate.setToY(0);
        
        FadeTransition fade = new FadeTransition(duration, node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        
        ParallelTransition parallel = new ParallelTransition(translate, fade);
        parallel.play();
    }
    
    /**
     * Animación de deslizamiento desde abajo (duración por defecto)
     */
    public static void slideUp(Node node) {
        slideUp(node, Duration.millis(400));
    }
    
    /**
     * Animación de rotación suave
     */
    public static void rotate(Node node, double angle, Duration duration) {
        RotateTransition rotate = new RotateTransition(duration, node);
        rotate.setByAngle(angle);
        rotate.play();
    }
    
    /**
     * Animación de pulso (repetitiva)
     */
    public static Timeline createPulse(Node node) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(node.scaleXProperty(), 1.0),
                new KeyValue(node.scaleYProperty(), 1.0)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(node.scaleXProperty(), 1.05),
                new KeyValue(node.scaleYProperty(), 1.05)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(node.scaleXProperty(), 1.0),
                new KeyValue(node.scaleYProperty(), 1.0)
            )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        return timeline;
    }
    
    /**
     * Animación de shake (sacudida)
     */
    public static void shake(Node node) {
        TranslateTransition translate1 = new TranslateTransition(Duration.millis(50), node);
        translate1.setToX(10);
        
        TranslateTransition translate2 = new TranslateTransition(Duration.millis(50), node);
        translate2.setToX(-10);
        
        TranslateTransition translate3 = new TranslateTransition(Duration.millis(50), node);
        translate3.setToX(10);
        
        TranslateTransition translate4 = new TranslateTransition(Duration.millis(50), node);
        translate4.setToX(0);
        
        SequentialTransition seq = new SequentialTransition(translate1, translate2, translate3, translate4);
        seq.play();
    }
}




