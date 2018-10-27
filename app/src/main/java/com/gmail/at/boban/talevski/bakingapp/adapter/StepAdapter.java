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
import com.gmail.at.boban.talevski.bakingapp.model.Step;
import com.squareup.picasso.Picasso;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

    Context context;
    List<Step> stepsList;

    public StepAdapter(Context context, List<Step> stepsList) {
        this.context = context;
        this.stepsList = stepsList;
    }

    @NonNull
    @Override
    public StepViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater
                .from(context)
                .inflate(R.layout.step_item, viewGroup, false);
        return new StepViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepViewHolder stepViewHolder, int position) {
        stepViewHolder.bind(position);
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }

    public class StepViewHolder extends RecyclerView.ViewHolder{

        ImageView recipeStepThumbnail;
        TextView recipeStepIdText;
        TextView recipeStepShortDescText;
        TextView recipeStepLongDescText;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeStepThumbnail = itemView.findViewById(R.id.card_recipe_step_thumbnail);
            recipeStepIdText = itemView.findViewById(R.id.card_recipe_step_id);
            recipeStepShortDescText = itemView.findViewById(R.id.card_recipe_step_short_desc);
            recipeStepLongDescText = itemView.findViewById(R.id.card_recipe_step_long_desc);
        }

        public void bind(int position) {
            if (stepsList.get(position).getThumbnailURL().isEmpty()) {
                recipeStepThumbnail.setImageResource(R.drawable.no_image);
            } else {
                Picasso.get().load(stepsList.get(position).getThumbnailURL()).into(recipeStepThumbnail);
            }
            recipeStepIdText.setText(stepsList.get(position).getId());
            recipeStepShortDescText.setText(stepsList.get(position).getShortDescription());
            recipeStepLongDescText.setText(stepsList.get(position).getDescription());
        }
    }
}
