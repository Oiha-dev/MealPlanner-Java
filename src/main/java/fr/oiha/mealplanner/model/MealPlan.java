package fr.oiha.mealplanner.model;

import java.util.Date;
import java.util.List;

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
