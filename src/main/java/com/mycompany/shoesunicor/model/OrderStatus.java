package com.mycompany.shoesunicor.model;

/**
 * Estados de una orden
 * @author Victor Negrete
 */
public enum OrderStatus {
    PENDING("Pendiente"),
    PROCESSING("Procesando"),
    SHIPPED("Enviado"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado");
    
    private final String displayName;
    
    OrderStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}

