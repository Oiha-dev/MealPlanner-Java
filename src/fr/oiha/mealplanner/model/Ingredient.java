package fr.oiha.mealplanner.model;

public class Ingredient {
    private Product product;
    private int productId; // For JSON serialization
    private double quantity;

    // Constructor for use in the application
    public Ingredient(Product product, double quantity) {
        this.product = product;
        this.productId = product.getId();
        this.quantity = quantity;
    }

    // Constructor for deserialization from JSON
    public Ingredient(int productId, double quantity) {
        this.productId = productId;
        this.quantity = quantity;
        // Product will be set later when loaded from the service
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
        this.productId = product.getId();
    }

    public int getProductId() {
        return productId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
