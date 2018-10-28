package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeDetailsViewModel;

public class RecipeDetailsActivity extends AppCompatActivity {

    RecipeDetailsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        setupViewModel();
    }

    private void setupViewModel() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RecipeListFragment.EXTRA_RECIPE)) {
            Recipe recipe = intent.getParcelableExtra(RecipeListFragment.EXTRA_RECIPE);
            viewModel = ViewModelProviders.of(this).get(RecipeDetailsViewModel.class);
            viewModel.setIngredientList(recipe.getIngredients());
            viewModel.setStepList(recipe.getSteps());

            // set title
            getSupportActionBar().setTitle(recipe.getName());
        }
    }
}
