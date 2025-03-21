package fr.oiha.mealplanner.model;

public class Ingredient extends Product {
    private double weightPerMeal;

    public Ingredient(int id, String name, double pricePerPack, double weightPerPack, String unit, double weightPerMeal) {
        super(id, name, pricePerPack, weightPerPack, unit);
        this.weightPerMeal = weightPerMeal;
    }

    public double getWeightPerMeal() {
        return weightPerMeal;
    }

    public void setWeightPerMeal(double weightPerMeal) {
        this.weightPerMeal = weightPerMeal;
    }
}
