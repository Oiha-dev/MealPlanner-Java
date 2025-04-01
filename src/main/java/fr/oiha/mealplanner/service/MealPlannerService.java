package fr.oiha.mealplanner.service;

import fr.oiha.mealplanner.exception.MealNotFoundException;
import fr.oiha.mealplanner.exception.ProductNotFoundException;
import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.MealPlan;
import fr.oiha.mealplanner.model.Product;

import java.util.*;

/**
 * MealPlannerService is a singleton class that manages the meal planning process.
 * It handles the addition, modification, and removal of products and meals.
 * It also generates meal plans and shopping lists based on user preferences.
 * It uses a DataStorageService to save and load data.
 */
public class MealPlannerService {
    private static MealPlannerService instance;
    private Set<Product> products;
    private Set<Meal> meals;
    private int productCounter = 0;
    private int mealCounter = 0;
    private int mealPlanCounter = 0;
    private DataStorageService storageService;
    private Map<Product, Double> shoppingList;

    /**
     * Private constructor for the MealPlannerService.
     * Initializes the product and meal sets.
     * Loads products and meals from the storage service.
     * Sets the product and meal counters based on loaded data.
     */
    private MealPlannerService() {
        storageService = new DataStorageService();
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
        shoppingList = new HashMap<>();
    }

    /**
     * Singleton instance getter for MealPlannerService.
     * @return the singleton instance of MealPlannerService
     */
    public static MealPlannerService getInstance() {
        if (instance == null) {
            instance = new MealPlannerService();
        }
        return instance;
    }

    /**
     * Adds a new product to the product set.
     * @param name the name of the product
     * @param pricePerPack the price per pack of the product
     * @param weightPerPack the weight per pack of the product
     * @param unit the unit of measurement for the product
     */
    public void addProduct(String name, double pricePerPack, double weightPerPack, String unit) {
        Product product = new Product(productCounter++, name, pricePerPack, weightPerPack, unit);
        products.add(product);
    }

    /**
     * Modifies an existing product in the product set.
     * @param id the ID of the product to modify
     * @param name the new name of the product
     * @param pricePerPack the new price per pack of the product
     * @param weightPerPack the new weight per pack of the product
     * @param unit the new unit of measurement for the product
     * @throws ProductNotFoundException if the product with the given ID is not found
     */
    public void modifyProduct(int id, String name, double pricePerPack, double weightPerPack, String unit) throws ProductNotFoundException {
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
        throw new ProductNotFoundException(id);
    }

    /**
     * Removes a product from the product set.
     * @param id the ID of the product to remove
     */
    public void removeProduct(int id) {
        products.removeIf(product -> product.getId() == id);
        DataStorageService.saveProducts(MealPlannerService.getInstance().getProducts());
    }

    /**
     * Adds a new meal for the meal set.
     * @param name the name of the meal
     * @param ingredients the list of ingredients for the meal
     * @param recipe the recipe for the meal
     */
    public void addMeal(String name, List<Ingredient> ingredients, String recipe) {
        Meal meal = new Meal(mealCounter++, name, recipe, ingredients);
        meals.add(meal);
        DataStorageService.saveMeals(getMeals());
    }

    /**
     * Removes a meal from the meal set.
     * @param id the ID of the meal to remove
     */
    public void removeMeal(int id) {
        meals.removeIf(meal -> meal.getId() == id);
        DataStorageService.saveMeals(getMeals());
    }

    /**
     * Modifies an existing meal in the meal set.
     * This method updates the meal's name, ingredients, and recipe.
     * It also saves the updated meals to the storage service.
     * @param id the ID of the meal to modify
     * @param name the new name of the meal
     * @param ingredients the new list of ingredients for the meal
     * @param recipe the new recipe for the meal
     * @throws MealNotFoundException if the meal with the given ID is not found
     */
    public void modifyMeal(int id, String name, List<Ingredient> ingredients, String recipe) throws MealNotFoundException {
        for (Meal meal : meals) {
            if (meal.getId() == id) {
                meal.setName(name);
                meal.setRecipe(recipe);
                meal.setIngredients(ingredients);
                DataStorageService.saveMeals(getMeals());
                return;
            }
        }
        throw new MealNotFoundException(id);
    }

    /**
     * Generates a meal plan based on the maximum budget and number of meals.
     * This method randomly selects meals from the meal set
     * and ensures that the total cost does not exceed the budget.
     * @param maxBudget the maximum budget for the meal plan
     * @param mealCount the number of meals to include in the meal plan
     * @return a MealPlan object containing the selected meals
     */
    public MealPlan generateMealPlan(double maxBudget, int mealCount) {
        if (meals.isEmpty()) {
            return null;
        }

        List<Meal> allMeals = new ArrayList<>(meals);
        Collections.shuffle(allMeals); 

        List<Meal> selectedMeals = new ArrayList<>();
        double totalCost = 0.0;

        
        for (Meal meal : allMeals) {
            if (selectedMeals.size() >= mealCount) {
                break;
            }

            double mealCost = calculateMealCost(meal);

            if (totalCost + mealCost <= maxBudget) {
                selectedMeals.add(meal);
                totalCost += mealCost;
            }
        }

        return new MealPlan(mealPlanCounter++, "Plan de repas du " + new Date(), new Date(), selectedMeals);
    }

    /**
     * Generates a shopping list based on the provided meal plan.
     * This method iterates through the meals and their ingredients,
     * aggregating the quantities of each product.
     * @param plan the meal plan to generate the shopping list from
     * @return a map of products and their total quantities
     */
    public Map<Product, Double> generateShoppingList(MealPlan plan) {
        Map<Product, Double> shoppingList = new HashMap<>();

        if (plan == null || plan.getMeals() == null || plan.getMeals().isEmpty()) {
            return shoppingList;
        }

        for (Meal meal : plan.getMeals()) {
            for (Ingredient ingredient : meal.getIngredients()) {
                Product product = ingredient.getProduct();
                double currentQuantity = shoppingList.getOrDefault(product, 0.0);
                shoppingList.put(product, currentQuantity + ingredient.getQuantity());
            }
        }

        this.shoppingList = shoppingList;
        return shoppingList;
    }

    public boolean exportMealPlanToMarkdown(MealPlan mealPlan, String filePath) {
        return storageService.exportMealPlanToMarkdown(mealPlan, filePath);
    }

    /**
     * Calculates the total cost of a meal based on its ingredients.
     * This method iterates through the ingredients of the meal,
     * calculating the cost based on the product's price and quantity.
     * @param meal the meal to calculate the cost for
     * @return the total cost of the meal
     */
    public double calculateMealCost(Meal meal) {
        double cost = 0.0;
        for (Ingredient ingredient : meal.getIngredients()) {
            Product product = ingredient.getProduct();
            cost += (ingredient.getQuantity() / product.getWeightPerPack()) * product.getPricePerPack();
        }
        return cost;
    }

    public Set<Meal> getMeals() {
        return meals;
    }

    public Set<Product> getProducts() {
        return products;
    }
}
