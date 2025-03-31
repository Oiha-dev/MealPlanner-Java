package fr.oiha.mealplanner.model;

public class Ingredient {
    private Product product;
    private double quantity;

    public Ingredient(Product product, double quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public double getQuantity() {
        return quantity;
    }

}
