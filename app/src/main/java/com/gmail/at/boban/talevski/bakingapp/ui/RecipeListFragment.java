package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.adapter.RecipeAdapter;
import com.gmail.at.boban.talevski.bakingapp.api.UdacityRecipeApi;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.network.RetrofitClientInstance;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListFragment extends Fragment {
    public static final String TAG = RecipeListFragment.class.getSimpleName();

    private RecipeListViewModel mViewModel;

    // Mandatory empty constructor
    public RecipeListFragment(){}

    public static RecipeListFragment newInstance() {
        return new RecipeListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.recipe_list_fragment, container, false);

        RetrofitClientInstance.getRetrofitInstance().create(UdacityRecipeApi.class).getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Log.d(TAG, "response received");

                RecyclerView recipeRecyclerView = rootView.findViewById(R.id.recipe_recycler_view);
                RecipeAdapter recipeAdapter = new RecipeAdapter(response.body(), getContext());
                recipeRecyclerView.setAdapter(recipeAdapter);
                recipeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recipeRecyclerView.setHasFixedSize(true);
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                Log.d(TAG, "response failure");
                if (t instanceof IOException) {
                    Log.d(TAG, "this is an actual network failure :( inform the user and possibly retry");
                }
                else {
                    Log.d(TAG, "Probably conversion error - message: " + t.getMessage() );
                }
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(RecipeListViewModel.class);
        // TODO: Use the ViewModel


    }

}
