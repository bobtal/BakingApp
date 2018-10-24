package com.gmail.at.boban.talevski.bakingapp.api;

import com.gmail.at.boban.talevski.bakingapp.model.Recipe;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UdacityRecipeApi {

    @GET("android-baking-app-json")
    Call<List<Recipe>> getRecipes();
}
