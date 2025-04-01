package fr.oiha.mealplanner.service;

import fr.oiha.mealplanner.model.Ingredient;
import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.MealPlan;
import fr.oiha.mealplanner.model.Product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * DataStorageService is responsible for saving and loading data to and from files.
 * It uses Gson for JSON serialization and deserialization.
 * It handles the storage of products, meals, and meal plans.
 */
public class DataStorageService {

    /**
     * Saves a set of products to a JSON file.
     * The file is named "products.json".
     * The products are serialized using Gson.
     * @param products the set of products to save
     */
    public static void saveProducts(Set<Product> products) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(products);

        try (FileWriter writer = new FileWriter("products.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Product> loadProducts() {
        File file = new File("products.json");
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            Gson gson = new Gson();
            Product[] products = gson.fromJson(reader, Product[].class);
            return List.of(products);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void saveMeals(Set<Meal> meals) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(meals);

        try (FileWriter writer = new FileWriter("meals.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads meals from a JSON file.
     * The file is named "meals.json".
     * The meals are deserialized using Gson.
     * @return a list of meals
     */
    public List<Meal> loadMeals() {
        File file = new File("meals.json");
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            Meal[] meals = gson.fromJson(reader, Meal[].class);

            if (meals == null) {
                return new ArrayList<>();
            }

            return List.of(meals);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Saves a meal plan to a JSON file.
     * The file is named "meal_plan.json".
     * The meal plan is serialized using Gson.
     * @param mealPlan the meal plan to save
     */
    public boolean exportMealPlanToMarkdown(MealPlan mealPlan, String filePath) {
        if (mealPlan == null || mealPlan.getMeals() == null || mealPlan.getMeals().isEmpty()) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {

            writer.println("# " + mealPlan.getName());
            writer.println();


            writer.println("## Liste des repas");
            writer.println();

            MealPlannerService service = MealPlannerService.getInstance();
            double totalCost = 0.0;

            for (Meal meal : mealPlan.getMeals()) {
                double mealCost = service.calculateMealCost(meal);
                totalCost += mealCost;
                writer.printf("- %s (%.2f €)%n", meal.getName(), mealCost);
            }

            writer.println();
            writer.printf("**Coût total: %.2f €**%n", totalCost);
            writer.println();


            writer.println("## Liste de courses");
            writer.println();

            Map<Product, Double> shoppingList = service.generateShoppingList(mealPlan);


            Map<String, Integer> productOccurrences = new HashMap<>();
            Map<String, Product> productByIdentifier = new HashMap<>();
            Map<String, Double> quantityByIdentifier = new HashMap<>();

            for (Map.Entry<Product, Double> entry : shoppingList.entrySet()) {
                Product product = entry.getKey();
                Double quantity = entry.getValue();


                String identifier = product.getId() + "-" + quantity;


                productByIdentifier.put(identifier, product);
                quantityByIdentifier.put(identifier, quantity);


                productOccurrences.put(identifier, productOccurrences.getOrDefault(identifier, 0) + 1);
            }


            for (String identifier : productOccurrences.keySet()) {
                Product product = productByIdentifier.get(identifier);
                Double quantity = quantityByIdentifier.get(identifier);
                int occurrences = productOccurrences.get(identifier);


                double totalQuantity = quantity;
                if (occurrences > 1) {
                    totalQuantity = quantity * occurrences;
                }

                double packCount = Math.ceil(totalQuantity / product.getWeightPerPack());
                double totalPrice = packCount * product.getPricePerPack();

                if (occurrences > 1) {
                    writer.printf("- %s: %.2f %s (x%d) (%.0f paquet(s), %.2f €)%n",
                        product.getName(), totalQuantity, product.getUnit(), occurrences, packCount, totalPrice);
                } else {
                    writer.printf("- %s: %.2f %s (%.0f paquet(s), %.2f €)%n",
                        product.getName(), quantity, product.getUnit(), packCount, totalPrice);
                }
            }

            writer.println();


            writer.println("## Recettes");
            writer.println();

            for (Meal meal : mealPlan.getMeals()) {
                writer.println("### " + meal.getName());
                writer.println();

                writer.println("**Ingrédients:**");
                for (Ingredient ingredient : meal.getIngredients()) {
                    writer.printf("- %.2f %s de %s%n",
                        ingredient.getQuantity(),
                        ingredient.getProduct().getUnit(),
                        ingredient.getProduct().getName());
                }

                writer.println();
                writer.println("**Préparation:**");
                writer.println(meal.getRecipe());
                writer.println();
            }

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

