package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Step;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeStepDetailsViewModel;

import java.util.List;

public class RecipeStepDetailsActivity extends AppCompatActivity {

    private RecipeStepDetailsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        setupViewModel();
    }

    private void setupViewModel() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(RecipeDetailsFragment.EXTRA_STEP_LIST) &&
                intent.hasExtra(RecipeDetailsFragment.EXTRA_STEP_POSITION)) {
            List<Step> steps = intent.getParcelableArrayListExtra(RecipeDetailsFragment.EXTRA_STEP_LIST);
            int stepPosition = intent.getIntExtra(RecipeDetailsFragment.EXTRA_STEP_POSITION, -1);
            String recipeName = intent.getStringExtra(RecipeDetailsFragment.EXTRA_RECIPE_NAME);
            viewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);
            viewModel.setStepList(steps);
            viewModel.setStepPosition(stepPosition);
            viewModel.setRecipeName(recipeName);

            // set title
            getSupportActionBar().setTitle(recipeName);
        }
    }
}
