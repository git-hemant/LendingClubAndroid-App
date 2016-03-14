package com.ravneetg.lcautomatique.utils;

/**
 * This class is wrapper class which represent individual item inside the
 * drop down. This wrapper class provides separate display value then
 * actual value which is stored.
 * Created by HemantSingh on 28/02/2015.
 */
public class DropdownValue {
    private String displayValue;
    private String saveValue;
    // We can cache the hash code as this class is immutable.
    private int hashCode = -1;

    public DropdownValue(String displayValue, String savedValue) {
        this.displayValue = displayValue;
        this.saveValue = savedValue;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public String getSaveValue() {
        return saveValue;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof  DropdownValue) {
            DropdownValue other = (DropdownValue) o;
            return (other.displayValue.equals(displayValue) && other.saveValue.equals(saveValue));
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (hashCode == -1) {
            // Best way to generate unique hash code is to use native implementation.
            hashCode = (displayValue + saveValue).hashCode();
        }
        return hashCode;
    }

    @Override
    public String toString() {
        return displayValue;
    }

    // static utility methods
    public static String[] getDisplayValues(DropdownValue[] values) {
        String[] displayValues = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            displayValues[i] = values[i].getDisplayValue();
        }
        return displayValues;
    }
}
