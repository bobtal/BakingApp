package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.adapter.IngredientAdapter;
import com.gmail.at.boban.talevski.bakingapp.adapter.StepAdapter;
import com.gmail.at.boban.talevski.bakingapp.model.Step;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeDetailsViewModel;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailsFragment extends Fragment implements StepAdapter.OnClickHandler {
    private static final String TAG = RecipeDetailsFragment.class.getSimpleName();
    public static final String EXTRA_STEP_LIST =
            "com.gmail.at.boban.talevski.bakingapp.ui.EXTRA_STEP_LIST";
    public static final String EXTRA_STEP_POSITION =
            "com.gmail.at.boban.talevski.bakingapp.ui.EXTRA_STEP_POSITION";
    private static final String EXTRA_RECIPE_NAME =
            "com.gmail.at.boban.talevski.bakingapp.ui.EXTRA_RECIPE_NAME";

    private RecipeDetailsViewModel viewModel;
    private RecyclerView ingredientsRecyclerView;
    private RecyclerView stepsRecyclerView;

    public static RecipeDetailsFragment newInstance() {
        return new RecipeDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_details_fragment, container, false);
        ingredientsRecyclerView = view.findViewById(R.id.recipe_ingredients_recycler_view);
        stepsRecyclerView = view.findViewById(R.id.recipe_steps_recycler_view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailsViewModel.class);

        IngredientAdapter ingredientAdapter =
                new IngredientAdapter(getActivity(), viewModel.getIngredientList());
        ingredientsRecyclerView.setAdapter(ingredientAdapter);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientsRecyclerView.setHasFixedSize(true);

        StepAdapter stepAdapter = new StepAdapter(getActivity(), this, viewModel.getStepList());
        stepsRecyclerView.setAdapter(stepAdapter);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stepsRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onListItemClick(List<Step> stepsList, int stepPosition) {
        Intent intent = new Intent(getActivity(), RecipeStepDetailsActivity.class);
        intent.putParcelableArrayListExtra(EXTRA_STEP_LIST, (ArrayList<? extends Parcelable>) stepsList);
        intent.putExtra(EXTRA_STEP_POSITION, stepPosition);
        intent.putExtra(EXTRA_RECIPE_NAME, viewModel.getRecipeName());
        startActivity(intent);
    }
}
