package com.gmail.at.boban.talevski.bakingapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.gmail.at.boban.talevski.bakingapp.api.UdacityRecipeApi;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.network.RetrofitClientInstance;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RetrofitClientInstance.getRetrofitInstance().create(UdacityRecipeApi.class).getRecipes().enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                Log.d(TAG, "response received");
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
