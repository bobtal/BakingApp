package com.gmail.at.boban.talevski.bakingapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
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

    private Context context;
    private List<Step> stepsList;
    private int selectedPosition;

    private OnClickHandler clickHandler;

    // defines adapter behaviour depending on whether it's showing in twoPane mode
    private boolean twoPane;

    public interface OnClickHandler {
        void onListItemClick(List<Step> stepsList, int stepPosition);
    }

    public StepAdapter(Context context, OnClickHandler clickHandler, List<Step> stepsList, boolean twoPane, int stepPosition) {
        this.context = context;
        this.stepsList = stepsList;
        this.clickHandler = clickHandler;
        this.twoPane = twoPane;
        this.selectedPosition = stepPosition;
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

    public class StepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView recipeStepThumbnail;
        TextView recipeStepShortDescText;
        CardView recipeStepCard;

        public StepViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeStepThumbnail = itemView.findViewById(R.id.card_recipe_step_thumbnail);
            recipeStepShortDescText = itemView.findViewById(R.id.card_recipe_step_short_desc);
            recipeStepCard = itemView.findViewById(R.id.card_recipe_step);
            itemView.setOnClickListener(this);
        }

        public void bind(int position) {
            if (stepsList.get(position).getThumbnailUrl().isEmpty()
                    || stepsList.get(position).getThumbnailUrl().endsWith("mp4")) {
                // add the noimage drawable to the ImageView, but remove it
                // from the View hierarchy so it doesn't take up screen real estate for no reason
                recipeStepThumbnail.setImageResource(R.drawable.no_image);
                recipeStepThumbnail.setVisibility(View.GONE);
            } else {
                Picasso.get().load(stepsList.get(position).getThumbnailUrl()).into(recipeStepThumbnail);
            }
            recipeStepShortDescText.setText(stepsList.get(position).getShortDescription());

            // handle coloring/highlighting the selected step if in two pane mode
            if (twoPane) {
                if (position == selectedPosition) {
                    recipeStepCard.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                    recipeStepShortDescText.setTextColor(Color.WHITE);
                } else {
                    recipeStepCard.setBackgroundColor(Color.WHITE);
                    recipeStepShortDescText.setTextColor(Color.BLACK);
                }
            }
        }

        @Override
        public void onClick(View view) {
            clickHandler.onListItemClick(stepsList, getAdapterPosition());

            // set the selected position and call notifyDataSetChanged() to trigger
            // the appropriate recoloring of the views to keep the highlighting consistent
            // should only be done in two pane mode
            if (twoPane) {
                selectedPosition = getAdapterPosition();
                notifyDataSetChanged();
            }
        }
    }
}
