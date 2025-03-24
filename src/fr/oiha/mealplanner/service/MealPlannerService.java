package fr.oiha.mealplanner.service;

import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.MealPlan;
import fr.oiha.mealplanner.model.Product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MealPlannerService {
    private static MealPlannerService instance;
    private Set<Product> products;
    private Set<Meal> meals;
    private int productCounter = 1;
    private int mealCounter = 1;

    private MealPlannerService() {
        DataStorageService storageService = new DataStorageService();
        List<Product> loadedProducts = storageService.loadProducts();
        if (loadedProducts != null) {
            products = new HashSet<>(loadedProducts);
            for (Product p : loadedProducts) {
                if (p.getId() >= productCounter) {
                    productCounter = p.getId() + 1;
                }
            }
        } else {
            products = new HashSet<>();
        }
        meals = new HashSet<>();
    }

    public static MealPlannerService getInstance() {
        if (instance == null) {
            instance = new MealPlannerService();
        }
        return instance;
    }

    public void addProduct(String name, double pricePerPack, double weightPerPack, String unit) {
        Product product = new Product(productCounter++, name, pricePerPack, weightPerPack, unit);
        products.add(product);
    }

    public void removeProduct(String name) {
        products.removeIf(product -> product.getName().equalsIgnoreCase(name));
    }

    public void addMeal(String name, List<Ingredient> ingredients, String recipe) {
        Meal meal = new Meal(mealCounter++, name, recipe, ingredients);
        meals.add(meal);
    }

    public void removeMeal(String name) {
        meals.removeIf(meal -> meal.getName().equalsIgnoreCase(name));
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