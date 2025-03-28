package fr.oiha.mealplanner.service;

import main.java.fr.oiha.mealplanner.model.Meal;
import main.java.fr.oiha.mealplanner.model.MealPlan;
import main.java.fr.oiha.mealplanner.model.Product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class DataStorageService {

    public void saveMealPlan(MealPlan plan) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(plan);

        try (FileWriter writer = new FileWriter("mealplan.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public MealPlan loadMealPlan() {
        try (BufferedReader reader = new BufferedReader(new FileReader("mealplan.json"))) {
            Gson gson = new Gson();
            return gson.fromJson(reader, MealPlan.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

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

    public void saveShoppingList(Map<Product, Double> shoppingList) {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(shoppingList);

        try (FileWriter writer = new FileWriter("shoppinglist.json")) {
            writer.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean exportMealPlanToMarkdown(MealPlan mealPlan, String filePath) {
        if (mealPlan == null || mealPlan.getMeals() == null || mealPlan.getMeals().isEmpty()) {
            return false;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Écrire le titre du plan de repas
            writer.println("# " + mealPlan.getName());
            writer.println();

            // Écrire la section des repas
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

            // Écrire la liste de courses
            writer.println("## Liste de courses");
            writer.println();

            Map<Product, Double> shoppingList = service.generateShoppingList(mealPlan);

            // Compter combien de fois chaque produit apparaît avec la même quantité
            Map<String, Integer> productOccurrences = new HashMap<>();
            Map<String, Product> productByIdentifier = new HashMap<>();
            Map<String, Double> quantityByIdentifier = new HashMap<>();

            for (Map.Entry<Product, Double> entry : shoppingList.entrySet()) {
                Product product = entry.getKey();
                Double quantity = entry.getValue();

                // Créer un identifiant unique pour le produit et sa quantité
                String identifier = product.getId() + "-" + quantity;

                // Stocker le produit et sa quantité
                productByIdentifier.put(identifier, product);
                quantityByIdentifier.put(identifier, quantity);

                // Incrémenter le compteur d'occurrences
                productOccurrences.put(identifier, productOccurrences.getOrDefault(identifier, 0) + 1);
            }

            // Afficher chaque produit avec son nombre d'occurrences
            for (String identifier : productOccurrences.keySet()) {
                Product product = productByIdentifier.get(identifier);
                Double quantity = quantityByIdentifier.get(identifier);
                int occurrences = productOccurrences.get(identifier);

                // Calculer le nombre de paquets nécessaires
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

            // Écrire les recettes
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

    public Map<Product, Double> loadShoppingList() {
        File file = new File("shoppinglist.json");
        if (!file.exists()) {
            return new HashMap<>();
        }

        try (Reader reader = new FileReader(file)) {
            Gson gson = new GsonBuilder().create();
            Map<Product, Double> shoppingList = gson.fromJson(reader, Map.class);

            if (shoppingList == null) {
                return new HashMap<>();
            }

            return shoppingList;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
}

