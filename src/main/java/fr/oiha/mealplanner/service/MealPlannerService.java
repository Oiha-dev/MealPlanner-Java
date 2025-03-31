package fr.oiha.mealplanner.service;

import fr.oiha.mealplanner.exception.MealNotFoundException;
import fr.oiha.mealplanner.exception.ProductNotFoundException;
import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.MealPlan;
import fr.oiha.mealplanner.model.Product;

import java.util.*;

public class MealPlannerService {
    private static MealPlannerService instance;
    private Set<Product> products;
    private Set<Meal> meals;
    private int productCounter = 0;
    private int mealCounter = 0;
    private int mealPlanCounter = 0;
    private DataStorageService storageService;
    private Map<Product, Double> shoppingList;

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
