package com.gmail.at.boban.talevski.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Ingredient;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.model.Step;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeDetailsViewModel;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeStepDetailsViewModel;
import com.gmail.at.boban.talevski.bakingapp.widget.BakingWidgetProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeDetailsActivity extends AppCompatActivity {

    public static final String PREFS_FILE = "com.gmail.at.boban.talevski.bakingapp.ui.preferences";
    public static final String KEY_RECIPE_NAME = "KEY_RECIPE_NAME";
    public static final String KEY_INGREDIENTS_SET = "KEY_INGREDIENTS_SET";

    RecipeDetailsViewModel masterViewModel;
    private boolean twoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        FrameLayout recipeStepDetailsContainer = findViewById(R.id.recipe_step_details_container);
        if (recipeStepDetailsContainer != null) {
            // we are in two pane mode
            twoPane = true;
        }

        boolean isFreshStart = savedInstanceState == null;
        setupViewModel(isFreshStart);

        // set title
        getSupportActionBar().setTitle(masterViewModel.getRecipeName());
    }

    private void setupViewModel(boolean isFreshStart) {
        // registers the viewmodel in any case
        masterViewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);

        // if it's a fresh activity start, acquire data from the intent to set it up in the viewmodel
        // and add the fragment if it's in twoPane mode
        // otherwise do nothing, as the viewmodel is already filled with data
        // which might have already changed.
        // Step position mostly, it could've been changed by clicking on other steps in the master
        // pane and the update is already recorded in the (step) details viewmodel, we don't want
        // it to default to zero on rotation
        if (isFreshStart) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(RecipeListFragment.EXTRA_RECIPE)) {
                Recipe recipe = intent.getParcelableExtra(RecipeListFragment.EXTRA_RECIPE);
                masterViewModel.setIngredientList(recipe.getIngredients());
                masterViewModel.setStepList(recipe.getSteps());
                masterViewModel.setRecipeName(recipe.getName());
                masterViewModel.setTwoPane(twoPane);

                putRecipeDetailsInSharedPref();

                if (twoPane) {
                    // set up another viewmodel to be used by the (step)details fragment
                    RecipeStepDetailsViewModel detailsViewModel =
                            ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);

                    // On initial activity start use the information in the master viewmodel to populate
                    // the data in the (step) details view model
                    List<Step> steps = masterViewModel.getStepList();
                    int stepPosition = 0; // default at the first step of the recipe
                    String recipeName = masterViewModel.getRecipeName();
                    detailsViewModel.setStepList(steps);
                    detailsViewModel.setStepPosition(stepPosition);
                    detailsViewModel.setRecipeName(recipeName);

                    // add the (step) details fragment if it's not already added
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    RecipeStepDetailsFragment savedFragment = (RecipeStepDetailsFragment) fragmentManager
                            .findFragmentById(R.id.recipe_step_details_container);

                    // This check is likely not needed since we check if it's a fresh start
                    // of the activity and the fragment shouldn't be present if we get to this part
                    // of the code. Still, better safe than sorry.
                    if (savedFragment == null) {
                        getSupportFragmentManager().beginTransaction()
                                .add(R.id.recipe_step_details_container, new RecipeStepDetailsFragment())
                                .commit();
                    }
                }
            }
        }
    }

    // puts the last seen recipe details in shared preferences to be accessed by the widget
    // and update the widget
    private void putRecipeDetailsInSharedPref() {
        Set<String> ingredientSet = new HashSet<>();
        List<Ingredient> ingredientList = masterViewModel.getIngredientList();
        // adds string interpretation of each ingredient to a set of String ingredients
        ingredientList.forEach(ingredient -> ingredientSet.add(ingredient.toString(this)));

        // Store the
        SharedPreferences preferences = getSharedPreferences(PREFS_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(KEY_RECIPE_NAME, masterViewModel.getRecipeName());
        editor.putStringSet(KEY_INGREDIENTS_SET, ingredientSet);
        editor.apply();

        // Update the widget to show the ingredients of the last viewed recipe
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, BakingWidgetProvider.class));
        BakingWidgetProvider.updateBakingWidgets(
                this, appWidgetManager, appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
    }
}
