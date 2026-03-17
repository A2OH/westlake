package com.example.mockdonalds;

/** POJO: menu item with id, name, description, price, category. */
public class MenuItem {
    public final int id;
    public final String name;
    public final String description;
    public final double price;
    public final String category;

    public MenuItem(int id, String name, String description, double price, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
    }

    public String getPriceString() {
        // Avoid String.format (ICU) and Math.round/floor (native) for KitKat Dalvik
        int cents = (int)(price * 100 + 0.5);
        int dollars = cents / 100;
        int remainder = cents % 100;
        return "$" + dollars + "." + (remainder < 10 ? "0" : "") + remainder;
    }

    @Override
    public String toString() {
        return name + " " + getPriceString();
    }
}
