package fr.oiha.mealplanner.service;

import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.MealPlan;
import fr.oiha.mealplanner.model.Product;

import java.util.List;
import java.util.Set;

public class MealPlannerService {
    private Set<Product> products;
    private Set<Meal> meals;

    public void addProduct(String name, double pricePerPack, double weightPerPack, String unit) {

    }

    public void removeProduct(String name) {

    }


    public void addMeal(String name, List<Ingredient> ingredients, String recipe) {

    }

    public void removeMeal(String name) {

    }

    public MealPlan generateMealPlan(double maxBudget) {
        return null;
    }

    public void generateShoppingList(MealPlan plan) {

    }


    public Set<Meal> getMeals() {
        return meals;
    }

    public Set<Product> getProducts() {
        return products;
    }

}
