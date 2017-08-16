package com.taitsmith.daybaker.data;

import io.realm.RealmObject;

/**
 * Class to hold all relevant recipe info. Name, steps, ingredients, etc.
 */

public class Recipe extends RealmObject{
    private String name, ingredients, steps;
    private int servings;

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }
}
