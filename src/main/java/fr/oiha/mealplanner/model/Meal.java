package fr.oiha.mealplanner.model;

import java.util.List;

/**
 * Represents a meal in the meal planner.
 * Contains a name, recipe, and a list of ingredients.
 * The meal has a unique ID.
 */
public class Meal {
    private final int id;
    private String name;
    private String recipe;
    private List<Ingredient> ingredients;

    public Meal(int id, String name, String recipe, List<Ingredient> ingredients) {
        this.id = id;
        this.name = name;
        this.recipe = recipe;
        this.ingredients = ingredients;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecipe() {
        return recipe;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }
}
