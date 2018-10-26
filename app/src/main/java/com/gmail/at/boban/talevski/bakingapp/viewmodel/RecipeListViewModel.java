package com.gmail.at.boban.talevski.bakingapp.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.gmail.at.boban.talevski.bakingapp.api.UdacityRecipeApi;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.network.RetrofitClientInstance;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListViewModel extends ViewModel {
    private static final String TAG = RecipeListViewModel.class.getSimpleName();

    private MutableLiveData<List<Recipe>> recipeList;


    public LiveData<List<Recipe>> getRecipes() {
        if (recipeList == null) {
            recipeList = new MutableLiveData<>();
            loadRecipes();
        }
        return recipeList;
    }

    private void loadRecipes() {
        final MutableLiveData<List<Recipe>> results = new MutableLiveData<>();

        RetrofitClientInstance.getRetrofitInstance().create(UdacityRecipeApi.class).getRecipes()
                .enqueue(new Callback<List<Recipe>>() {
                    @Override
                    public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                        Log.d(TAG, "response received");
                        if (response.body() != null) {
                            results.setValue(response.body());
                            recipeList.setValue(results.getValue());
                        }
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

    }
}
