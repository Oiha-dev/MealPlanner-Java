package fr.oiha.mealplanner.exception;

public class MealNotFoundException extends Exception {
    public MealNotFoundException(int id) {
        super("Repas non trouvé avec l'ID : " + id);
    }
}
