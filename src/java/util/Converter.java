/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import javax.faces.context.FacesContext;

/**
 *
 * @author george
 */
public class Converter {
    public static Timestamp dateToTimestamp(Date d){
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        cal.set(Calendar.MILLISECOND, 0);
        
        return new Timestamp(cal.getTimeInMillis());
    }
    
    public static BigDecimal parseCoords(String fieldname){
        
        String coord = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(fieldname);
        if(coord == null || coord.equals("")){
            return null;
        }
        
        // Create a DecimalFormat that fits your requirements
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        String pattern = "#,##0.0#";
        DecimalFormat decimalFormat = new DecimalFormat(pattern, symbols);
        decimalFormat.setParseBigDecimal(true);
        
        // parse the string
        try {
            return (BigDecimal) decimalFormat.parse(coord);
        } catch (ParseException ex) {
            System.out.println("Parsing coords Exception --> "+ex.getMessage());
            
            return null;
        }
    }
}
