/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

/**
 *
 * @author anacarolina
 */

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {

    private String datePattern = "dd/MM/yyyy"; // pattern for date format
    private SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern); // formatter using the pattern

    @Override
    public Object stringToValue(String text) throws ParseException {
        // convert string input to a date object
        return dateFormatter.parseObject(text);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
        if (value != null) {
            Calendar cal = (Calendar) value; // cast the object to a calendar
            return dateFormatter.format(cal.getTime()); // format the calendar's date to string
        }
        return ""; // return empty string if value is null
    }
}
