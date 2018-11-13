package com.gmail.at.boban.talevski.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.model.Step;
import com.gmail.at.boban.talevski.bakingapp.utils.SharedPreferencesUtils;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeDetailsViewModel;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeStepDetailsViewModel;
import com.gmail.at.boban.talevski.bakingapp.widget.BakingWidgetProvider;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {

    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();

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

        // Declare a recipe object which will be used to fill in the UI.
        Recipe recipe;

        // if it's a fresh activity start, acquire the recipe object from the intent
        // and use it to populate the viewmodel
        if (isFreshStart) {
            Intent intent = getIntent();
            recipe = intent.getParcelableExtra(RecipeListFragment.EXTRA_RECIPE);
            populateViewModelWithData(recipe);

            // add/update the data in shared preferences on fresh activity start to store
            // both the data needed for the widget and the recipe object needed for an app restart
            SharedPreferencesUtils.putRecipeObjectInSharedPrefAsJson(this, recipe);
            SharedPreferencesUtils.putRecipeDetailsInSharedPrefForWidget(
                    this, masterViewModel.getIngredientList(), masterViewModel.getRecipeName());
            updateWidget();
        } else {
            // not a fresh start, so check whether the viewmodel is populated with data - it was a rotation,
            // or it was a restart after process was in the background and being killed by the system
            if (masterViewModel.getRecipeName() == null || masterViewModel.getRecipeName().isEmpty()) {
                // viewmodel doesn't have any data, so need to populate it with the
                // recipe object stored as JSON in shared preferences
                recipe = SharedPreferencesUtils.getRecipeFromSharedPreferences(this);
                populateViewModelWithData(recipe);
            } // it's a configuration change and the viewmodel is alive and well so do nothing
        }

        // add the fragment if it's in twoPane mode
        if (twoPane) {
            // set up another viewmodel to be used by the (step)details fragment
            RecipeStepDetailsViewModel detailsViewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);

            // use the information in the master viewmodel to populate
            // the data in the (step) details view model
            List<Step> steps = masterViewModel.getStepList();
            String recipeName = masterViewModel.getRecipeName();
            detailsViewModel.setStepList(steps);
            detailsViewModel.setRecipeName(recipeName);

            // set step position to 0 if it's a fresh start, otherwise, get it from shared preferences
            int stepPosition = isFreshStart ? 0 :
                    SharedPreferencesUtils.getStepPositionFromSharedPreferences(this);
            detailsViewModel.setStepPosition(stepPosition);

            // add the (step) details fragment if it's not already added
            FragmentManager fragmentManager = getSupportFragmentManager();
            RecipeStepDetailsFragment savedFragment = (RecipeStepDetailsFragment) fragmentManager
                    .findFragmentById(R.id.recipe_step_details_container);
            if (savedFragment == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recipe_step_details_container, new RecipeStepDetailsFragment())
                        .commit();

            }
        }
    }

    private void populateViewModelWithData(Recipe recipe) {
        masterViewModel.setIngredientList(recipe.getIngredients());
        masterViewModel.setStepList(recipe.getSteps());
        masterViewModel.setRecipeName(recipe.getName());
        masterViewModel.setTwoPane(twoPane);
    }

    private void updateWidget() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(this, BakingWidgetProvider.class));
        BakingWidgetProvider.updateBakingWidgets(
                this, appWidgetManager, appWidgetIds);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
    }

}
