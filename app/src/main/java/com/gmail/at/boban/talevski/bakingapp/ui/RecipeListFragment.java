package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.adapter.RecipeAdapter;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeListViewModel;

import java.util.List;

public class RecipeListFragment extends Fragment implements RecipeAdapter.OnListItemClick {
    private static final String TAG = RecipeListFragment.class.getSimpleName();
    public static final String EXTRA_RECIPE = "com.gmail.at.boban.talevski.bakingapp.ui.EXTRA_RECIPE";

    private RecipeListViewModel viewModel;
    private ProgressBar progressBar;
    private RecyclerView recipeRecyclerView;

    // Mandatory empty constructor
    public RecipeListFragment(){}

    public static RecipeListFragment newInstance() {
        return new RecipeListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.recipe_list_fragment, container, false);

        progressBar = rootView.findViewById(R.id.loading_progress);
        recipeRecyclerView = rootView.findViewById(R.id.recipe_recycler_view);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(RecipeListViewModel.class);
        setupViewModel();
    }

    private void setupViewModel() {
        showProgressBar();
        viewModel.getRecipes().observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipes) {
                Log.d(TAG, "onChanged");
                populateUIWithRecipes(recipes);
            }
        });
    }

    private void populateUIWithRecipes(@Nullable List<Recipe> recipes) {
        int numberOfColumns = getActivity().getResources().getInteger(R.integer.columns);
        RecipeAdapter recipeAdapter = new RecipeAdapter(getActivity(), this, recipes);
        recipeRecyclerView.setAdapter(recipeAdapter);
        recipeRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        recipeRecyclerView.setHasFixedSize(true);
        hideProgressBar();
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.INVISIBLE);
        recipeRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        recipeRecyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onListItemClick(Recipe recipe) {
        Intent intent = new Intent(getActivity(), RecipeDetailsActivity.class);
        intent.putExtra(EXTRA_RECIPE, recipe);
        startActivity(intent);
    }
}
