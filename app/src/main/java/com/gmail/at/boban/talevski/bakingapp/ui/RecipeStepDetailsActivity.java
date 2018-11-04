package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Step;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeStepDetailsViewModel;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class RecipeStepDetailsActivity extends AppCompatActivity {

    private PlayerView playerView;
    private TextView stepInstructions;
    private Button nextStepButton, previousStepButton;
    private SimpleExoPlayer player;

    private boolean landscape;

    private RecipeStepDetailsViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        boolean isFreshStart = savedInstanceState == null;
        // true if it's a fresh start of activity, not a configuration change (rotation)
        setupViewModel(isFreshStart);

        // set title
        getSupportActionBar().setTitle(viewModel.getRecipeName());

        playerView = findViewById(R.id.recipe_step_details_player_view);
        stepInstructions = findViewById(R.id.recipe_step_details_instructions_textview);
        nextStepButton = findViewById(R.id.recipe_step_details_button_next);
        previousStepButton = findViewById(R.id.recipe_step_details_button_previous);

        if (stepInstructions == null) {
            // step instructions view is not present in the landscape layout
            // so if that's the case we are in landscape mode
            landscape = true;
        }

        if (!landscape) {
            // set this only if we are not in landscape mode
            setInstructionText();
        }
        initializePlayer();
        setupPlayerMediaSource();
        initializeButtons();
        setButtonVisibility();
    }

    private void setupViewModel(boolean isFreshStart) {
        // registers the viewmodel in any case
        viewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);

        // if it's a fresh activity start, acquire data from the intent to set it up in the viewmodel
        // otherwise do nothing, as the viewmodel is already filled with data
        // which might have already changed
        // step position mostly, it could've been changed by clicking next/prev buttons
        // and the update is already recorded in the viewmodel
        if (isFreshStart) {
            Intent intent = getIntent();
            if (intent != null && intent.hasExtra(RecipeDetailsFragment.EXTRA_STEP_LIST) &&
                    intent.hasExtra(RecipeDetailsFragment.EXTRA_STEP_POSITION)) {
                List<Step> steps = intent.getParcelableArrayListExtra(RecipeDetailsFragment.EXTRA_STEP_LIST);
                int stepPosition = intent.getIntExtra(RecipeDetailsFragment.EXTRA_STEP_POSITION, -1);
                String recipeName = intent.getStringExtra(RecipeDetailsFragment.EXTRA_RECIPE_NAME);

                viewModel.setStepList(steps);
                viewModel.setStepPosition(stepPosition);
                viewModel.setRecipeName(recipeName);
            }
        }
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
    }

    private void initializeButtons() {
        previousStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setStepPosition(viewModel.getStepPosition().getValue() - 1);
                playerView.getPlayer().stop();
                updateUI();
            }
        });

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setStepPosition(viewModel.getStepPosition().getValue() + 1);
                playerView.getPlayer().stop();
                updateUI();
            }
        });
    }

    private void updateUI() {
        // updates the UI after a button click (prev/next)
        if (!landscape) {
            // set this only if we are not in landscape mode
            setInstructionText();
        }
        setupPlayerMediaSource();
        setButtonVisibility();
    }

    private void setInstructionText() {
        String instruction = viewModel.getStepList().get(viewModel.getStepPosition().getValue()).getDescription();
        stepInstructions.setText(instruction);
    }

    private void setupPlayerMediaSource() {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(viewModel.getStepList().get(viewModel.getStepPosition().getValue()).getVideoUrl()));
        // Prepare the player with the source.
        player.prepare(videoSource);

        // Start playing the sample
        player.setPlayWhenReady(true);
    }

    private void setButtonVisibility() {
        if (viewModel.getStepPosition().getValue() == 0) {
            // we are at the first step, no need for previous button
            previousStepButton.setVisibility(View.GONE);
        } else {
            // make sure previous button is set to visible cause it may have already
            // been removed if the user was at the first step at some point
            previousStepButton.setVisibility(View.VISIBLE);
        }
        if (viewModel.getStepPosition().getValue() == viewModel.getStepList().size() - 1) {
            // we are at the last step, no need for next button
            nextStepButton.setVisibility(View.GONE);
        } else {
            // make sure next button is set to visible cause it may have already
            // been removed if the user was at the last step at some point
            nextStepButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    private void releasePlayer() {
        playerView.getPlayer().stop();
        playerView.getPlayer().release();
        playerView = null;
    }
}
