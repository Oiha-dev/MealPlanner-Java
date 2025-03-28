package main.java.fr.oiha.mealplanner.service;

import main.java.fr.oiha.mealplanner.model.Ingredient;
import main.java.fr.oiha.mealplanner.model.Meal;
import main.java.fr.oiha.mealplanner.model.MealPlan;
import main.java.fr.oiha.mealplanner.model.Product;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MealPlannerService {
    private static MealPlannerService instance;
    private Set<Product> products;
    private Set<Meal> meals;
    private int productCounter = 0;
    private int mealCounter = 0;
    private DataStorageService storageService;

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
        List<Meal> loadedMeals = storageService.loadMeals();
        if (loadedMeals != null) {
            meals = new HashSet<>(loadedMeals);
            for (Meal m : loadedMeals) {
                if (m.getId() >= mealCounter) {
                    mealCounter = m.getId() + 1;
                }
            }
        } else {
            meals = new HashSet<>();
        }
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

    public void modifyProduct(int id, String name, double pricePerPack, double weightPerPack, String unit) {
        for (Product product : products) {
            if (product.getId() == id) {
                product.setName(name);
                product.setPricePerPack(pricePerPack);
                product.setWeightPerPack(weightPerPack);
                product.setUnit(unit);
                DataStorageService.saveProducts(MealPlannerService.getInstance().getProducts());
                return;
            }
        }
    }

    public void removeProduct(int id) {
        products.removeIf(product -> product.getId() == id);
        DataStorageService.saveProducts(MealPlannerService.getInstance().getProducts());
    }

    public void addMeal(String name, List<Ingredient> ingredients, String recipe) {
        Meal meal = new Meal(mealCounter++, name, recipe, ingredients);
        meals.add(meal);
        DataStorageService.saveMeals(getMeals());
    }

    public void removeMeal(int id) {
        meals.removeIf(meal -> meal.getId() == id);
        DataStorageService.saveMeals(getMeals());
    }

    public void modifyMeal(int id, String name, List<Ingredient> ingredients, String recipe) {
        for (Meal meal : meals) {
            if (meal.getId() == id) {
                meal.setName(name);
                meal.setRecipe(recipe);
                meal.setIngredients(ingredients);
                DataStorageService.saveMeals(getMeals());
                return;
            }
        }
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
