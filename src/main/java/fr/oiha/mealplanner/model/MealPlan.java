package main.java.fr.oiha.mealplanner.model;

import java.util.Date;
import java.util.List;

public class MealPlan {
    private final int id;
    private String name;
    private Date date;
    private List<Meal> meals;

    public MealPlan(int id, String name, Date date, List<Meal> meals) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.meals = meals;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Meal> getMeals() {
        return meals;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals;
    }

}
