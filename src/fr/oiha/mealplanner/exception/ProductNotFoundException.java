package fr.oiha.mealplanner.exception;

public class ProductNotFoundException extends Exception {
    public ProductNotFoundException(int id) {
        super("Produit non trouvé avec l'ID : " + id);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
