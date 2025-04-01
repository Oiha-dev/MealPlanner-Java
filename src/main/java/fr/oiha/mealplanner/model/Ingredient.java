package fr.oiha.mealplanner.model;

/**
 * Represents an ingredient in a meal.
 * Contains a product and its quantity.
 */
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
