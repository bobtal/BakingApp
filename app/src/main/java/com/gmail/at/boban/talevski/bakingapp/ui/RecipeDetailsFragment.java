package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
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
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeDetailsViewModel;

public class RecipeDetailsFragment extends Fragment {

    private RecipeDetailsViewModel mViewModel;
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
        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeDetailsViewModel.class);
        // TODO: Use the ViewModel

        IngredientAdapter ingredientAdapter =
                new IngredientAdapter(getActivity(), mViewModel.getIngredientList());
        ingredientsRecyclerView.setAdapter(ingredientAdapter);
        ingredientsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        ingredientsRecyclerView.setHasFixedSize(true);

        StepAdapter stepAdapter = new StepAdapter(getActivity(), mViewModel.getStepList());
        stepsRecyclerView.setAdapter(stepAdapter);
        stepsRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        stepsRecyclerView.setHasFixedSize(true);
    }

}
