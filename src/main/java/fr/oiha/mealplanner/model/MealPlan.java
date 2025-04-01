package fr.oiha.mealplanner.model;

import java.util.Date;
import java.util.List;

/**
 * Represents a meal plan.
 * Contains a name and a list of meals.
 * The meal plan has a unique ID.
 * It is used to organize meals for a specific date.
 * It is used to manage meal plans for users.
 * The meal plan can be saved and loaded from a file.
 */
public class MealPlan {
    private String name;
    private List<Meal> meals;

    public MealPlan(int id, String name, Date date, List<Meal> meals) {
        this.name = name;
        this.meals = meals;
    }

    public String getName() {
        return name;
    }

    public List<Meal> getMeals() {
        return meals;
    }

}
