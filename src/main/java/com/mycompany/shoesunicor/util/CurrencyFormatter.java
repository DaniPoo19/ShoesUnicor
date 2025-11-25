package com.mycompany.shoesunicor.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Utilidad para formatear moneda en pesos colombianos (COP)
 * @author Victor Negrete
 */
public class CurrencyFormatter {
    
    private static final DecimalFormat formatter;
    
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("es", "CO"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        
        formatter = new DecimalFormat("#,###", symbols);
    }
    
    /**
     * Formatea un precio en pesos colombianos
     * Ejemplo: 350000.0 -> "$350.000 COP"
     */
    public static String formatPrice(double price) {
        return "$" + formatter.format(price) + " COP";
    }
    
    /**
     * Formatea un precio sin el sufijo COP
     * Ejemplo: 350000.0 -> "$350.000"
     */
    public static String formatPriceShort(double price) {
        return "$" + formatter.format(price);
    }
}

