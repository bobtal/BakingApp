package com.gmail.at.boban.talevski.bakingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Ingredient;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;

public final class SharedPreferencesUtils {

    private static final String PREFS_FILE = "com.gmail.at.boban.talevski.bakingapp.ui.preferences";
    private static final String KEY_RECIPE_NAME = "KEY_RECIPE_NAME";
    private static final String KEY_INGREDIENTS_SET_FOR_WIDGET = "KEY_INGREDIENTS_SET_FOR_WIDGET";
    private static final String KEY_STEP_POSITION = "KEY_STEP_POSITION";
    private static final String KEY_RECIPE_OBJECT_JSON = "KEY_RECIPE_OBJECT_JSON";

    public static Recipe getRecipeFromSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        String jsonRecipeString = preferences.getString(KEY_RECIPE_OBJECT_JSON, "");

        Gson gson = new Gson();
        return gson.fromJson(jsonRecipeString, Recipe.class);
    }

    public static void putStepPositionInSharedPreferences(Context context, int stepPosition) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(KEY_STEP_POSITION, stepPosition);
        editor.apply();
    }

    public static int getStepPositionFromSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return preferences.getInt(KEY_STEP_POSITION, -1);
    }

    public static void putRecipeObjectInSharedPrefAsJson(Context context, Recipe recipe) {
        Gson gson = new Gson();
        String jsonRecipeString = gson.toJson(recipe);

        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_RECIPE_OBJECT_JSON, jsonRecipeString);
        editor.apply();
    }

    public static void putRecipeDetailsInSharedPrefForWidget(
            Context context, List<Ingredient> ingredientList, String recipeName) {
        Set<String> ingredientSet = new HashSet<>();
        // adds string interpretation of each ingredient to a set of String ingredients
        for (Ingredient ingredient : ingredientList) {
            ingredientSet.add(ingredient.toString(context));
        }

        // Store the recipe name and set of ingredients in the shared preferences
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_RECIPE_NAME, recipeName);
        editor.putStringSet(KEY_INGREDIENTS_SET_FOR_WIDGET, ingredientSet);
        editor.apply();
    }

    public static String getRecipeNameFromSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return preferences.getString(KEY_RECIPE_NAME, context.getString(R.string.no_recipe_viewed));
    }

    public static Set<String> getIngredientSetFromSharedPreferences(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        return preferences.getStringSet(KEY_INGREDIENTS_SET_FOR_WIDGET, new HashSet<>());
    }
}
