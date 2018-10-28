package com.gmail.at.boban.talevski.bakingapp.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.api.UdacityRecipeApi;
import com.gmail.at.boban.talevski.bakingapp.model.Ingredient;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.model.Step;
import com.gmail.at.boban.talevski.bakingapp.network.RetrofitClientInstance;
import com.gmail.at.boban.talevski.bakingapp.utils.NetworkUtils;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeListViewModel extends AndroidViewModel {
    private static final String TAG = RecipeListViewModel.class.getSimpleName();

    private MutableLiveData<List<Recipe>> recipeList;

    public RecipeListViewModel(@NonNull Application application) {
        super(application);
    }


    public LiveData<List<Recipe>> getRecipes() {
        if (recipeList == null) {
            recipeList = new MutableLiveData<>();
            loadRecipes();
        }
        return recipeList;
    }

    private void loadRecipes() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
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
                            } else {
                                Log.d(TAG, "Probably conversion error - message: " + t.getMessage());
                            }
                        }
                    });
        } else {
            Toast.makeText(getApplication(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }


}
