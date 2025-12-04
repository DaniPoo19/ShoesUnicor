module com.mycompany.shoesunicor {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive com.google.gson;
    requires transitive javafx.graphics;
    requires transitive javafx.base;
    
    opens com.mycompany.shoesunicor to javafx.fxml;
    opens com.mycompany.shoesunicor.view to javafx.fxml;
    opens com.mycompany.shoesunicor.model to com.google.gson, javafx.base;
    
    exports com.mycompany.shoesunicor;
    exports com.mycompany.shoesunicor.view;
    exports com.mycompany.shoesunicor.model;
    exports com.mycompany.shoesunicor.controller;
    exports com.mycompany.shoesunicor.util;
}
