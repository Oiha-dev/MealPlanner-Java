package fr.oiha.mealplanner.service;

import fr.oiha.mealplanner.model.Meal;
import fr.oiha.mealplanner.model.MealPlan;
import fr.oiha.mealplanner.model.Product;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.util.List;
import java.util.Set;

public class DataStorageService {

    public void saveMealPlan(MealPlan plan) {

    }

    public MealPlan loadMealPlan() {
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
        try (BufferedReader reader = new BufferedReader(new FileReader("products.json"))) {
            Gson gson = new Gson();
            Product[] products = gson.fromJson(reader, Product[].class);
            return List.of(products);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
        try (BufferedReader reader = new BufferedReader(new FileReader("meals.json"))) {
            Gson gson = new Gson();
            Meal[] meals = gson.fromJson(reader, Meal[].class);
            return List.of(meals);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveShoppingList() {

    }
    public void loadShoppingList() {

    }
}
