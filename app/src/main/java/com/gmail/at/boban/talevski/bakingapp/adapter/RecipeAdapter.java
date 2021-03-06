package com.gmail.at.boban.talevski.bakingapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private List<Recipe> recipeList;
    private Context context;

    private OnListItemClick clickHandler;

    public interface OnListItemClick {
        void onListItemClick(Recipe recipe);
    }

    public RecipeAdapter(Context context, OnListItemClick clickHandler, List<Recipe> recipeList) {
        this.context = context;
        this.clickHandler = clickHandler;
        this.recipeList = recipeList;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater
                .from(context)
                .inflate(R.layout.recipe_card, viewGroup, false);
        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int position) {
        recipeViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    public class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView cardRecipeImage;
        TextView cardRecipeTextView;
        TextView cardRecipeServingsTextView;

        public RecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRecipeImage = itemView.findViewById(R.id.card_recipe_image);
            cardRecipeTextView = itemView.findViewById(R.id.card_recipe_name);
            cardRecipeServingsTextView = itemView.findViewById(R.id.card_recipe_servings);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            Recipe recipe = recipeList.get(position);
            cardRecipeTextView.setText(recipe.getName());
            cardRecipeServingsTextView.setText(
                    context.getString(R.string.servings, recipe.getServings()));
            if (recipe.getImageUrl().isEmpty()) {
                // add the noimage drawable to the ImageView, but remove it
                // from the View hierarchy so it doesn't take up screen real estate for no reason
                cardRecipeImage.setImageResource(R.drawable.no_image);
                cardRecipeImage.setVisibility(View.GONE);
            } else {
                Picasso.get()
                        .load(recipe.getImageUrl())
                        .error(R.drawable.no_image)
                        .into(cardRecipeImage);
            }
        }

        @Override
        public void onClick(View view) {
            clickHandler.onListItemClick(recipeList.get(getAdapterPosition()));
        }
    }
}
