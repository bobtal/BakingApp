package com.gmail.at.boban.talevski.bakingapp.viewmodel;

import android.arch.lifecycle.ViewModel;

import com.gmail.at.boban.talevski.bakingapp.model.Ingredient;
import com.gmail.at.boban.talevski.bakingapp.model.Step;

import java.util.List;

public class RecipeDetailsViewModel extends ViewModel {
    private List<Ingredient> ingredientList;
    private List<Step> stepList;
    private String recipeName;

    public List<Ingredient> getIngredientList() {
        return ingredientList;
    }

    public void setIngredientList(List<Ingredient> ingredientList) {
        this.ingredientList = ingredientList;
    }

    public List<Step> getStepList() {
        return stepList;
    }

    public void setStepList(List<Step> stepList) {
        this.stepList = stepList;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
}
