package com.gmail.at.boban.talevski.bakingapp.ui;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.model.Recipe;
import com.gmail.at.boban.talevski.bakingapp.model.Step;
import com.gmail.at.boban.talevski.bakingapp.utils.SharedPreferencesUtils;
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

    private static final String EXTRA_PLAYER_IS_PLAYING = "EXTRA_PLAYER_IS_PLAYING";
    private static final String EXTRA_CURRENT_PLAYER_POSITION = "EXTRA_CURRENT_PLAYER_POSITION";

    private PlayerView playerView;
    private TextView stepInstructions;
    private Button nextStepButton, previousStepButton;
    private SimpleExoPlayer player;
    private ImageView noVideoImageView;

    private boolean landscape;

    private RecipeStepDetailsViewModel viewModel;
    private boolean isPlaying;
    private long currentPlayerPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step_details);

        boolean isFreshStart = savedInstanceState == null;
        // true if it's a fresh start of activity, not a configuration change (rotation)
        setupViewModel(isFreshStart);

        if (isFreshStart) {
            // If it's a fresh start, we want the video to play by default
            isPlaying = true;
        } else {
            // otherwise get the values from the savedInstanceState
            if (savedInstanceState.containsKey(EXTRA_PLAYER_IS_PLAYING) &&
                    savedInstanceState.containsKey(EXTRA_CURRENT_PLAYER_POSITION)) {
                // and set it in the member fields
                isPlaying = savedInstanceState.getBoolean(EXTRA_PLAYER_IS_PLAYING);
                currentPlayerPosition = savedInstanceState.getLong(EXTRA_CURRENT_PLAYER_POSITION);
            }
        }

        // set title
        getSupportActionBar().setTitle(viewModel.getRecipeName());

        // set up views
        playerView = findViewById(R.id.recipe_step_details_player_view);
        stepInstructions = findViewById(R.id.recipe_step_details_instructions_textview);
        nextStepButton = findViewById(R.id.recipe_step_details_button_next);
        previousStepButton = findViewById(R.id.recipe_step_details_button_previous);
        noVideoImageView = findViewById(R.id.recipe_step_details_no_video_imageview);

        if (stepInstructions == null) {
            // step instructions view is not present in the landscape layout
            // so if that's the case we are in landscape mode
            landscape = true;

            // hide system ui to make the video "full screen" in landscape mode
            hideSystemUi();

            // Don't do anything with buttons so they stay on screen
            // (hence the quotes on "full screen") for navigating to prev/next recipe step
        }

        if (!landscape) {
            // set this only if we are not in landscape mode
            setInstructionText();
        }
        initializeButtons();
        setButtonVisibility();
    }

    private void setupViewModel(boolean isFreshStart) {
        // registers the viewmodel in any case
        viewModel = ViewModelProviders.of(this).get(RecipeStepDetailsViewModel.class);

        // if it's a fresh activity start, acquire data from the intent to set it up in the viewmodel
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

                // update whatever step was chosen on fresh start of activity to shared preferences
                // to keep the app state up to date
                SharedPreferencesUtils.putStepPositionInSharedPreferences(this, stepPosition);
            }
        } else {
            // not a fresh start, so check whether the viewmodel is populated with data - it was a rotation,
            // or it was a restart after process was in the background and being killed by the system
            if (viewModel.getRecipeName() == null || viewModel.getRecipeName().isEmpty()) {
                // viewmodel doesn't have any data, so need to populate it using the
                // recipe object stored as JSON in shared preferences. It's the last open recipe
                Recipe recipe = SharedPreferencesUtils.getRecipeFromSharedPreferences(this);
                viewModel.setStepList(recipe.getSteps());
                viewModel.setRecipeName(recipe.getName());

                viewModel.setStepPosition(SharedPreferencesUtils.getStepPositionFromSharedPreferences(this));
            } // it's a configuration change and the viewmodel is alive and well so do nothing
        }
    }

    private void initializePlayer() {
        if (player == null) {
            player = ExoPlayerFactory.newSimpleInstance(this);
            playerView.setPlayer(player);
        }
    }

    private void setupPlayerMediaSource() {
        // produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, getString(R.string.app_name)));

        String videoUrl = viewModel.getStepList().get(viewModel.getStepPosition().getValue()).getVideoUrl();

        if (videoUrl == null || videoUrl.isEmpty()) {
            // hide the player and show the no video image
            playerView.setVisibility(View.GONE);
            noVideoImageView.setVisibility(View.VISIBLE);
        } else {
            // show and set up the player
            playerView.setVisibility(View.VISIBLE);
            noVideoImageView.setVisibility(View.GONE);

            // this is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoUrl));
            // prepare the player with the source.
            player.prepare(videoSource);

            // seek to the specified position
            player.seekTo(currentPlayerPosition);

            // start playing the sample depending on the value of isPlaying
            player.setPlayWhenReady(isPlaying);
        }
    }

    private void initializeButtons() {
        previousStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newStepPosition = viewModel.getStepPosition().getValue() - 1;
                // update the new step position in viewmodel
                viewModel.setStepPosition(newStepPosition);
                // update the new step position in shared preferences
                SharedPreferencesUtils.putStepPositionInSharedPreferences(
                        RecipeStepDetailsActivity.this,
                        newStepPosition);
                updateUIAfterButtonClick();
            }
        });

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newStepPosition = viewModel.getStepPosition().getValue() + 1;
                // update the new step position in viewmodel
                viewModel.setStepPosition(newStepPosition);
                // update the new step position in shared preferences
                SharedPreferencesUtils.putStepPositionInSharedPreferences(
                        RecipeStepDetailsActivity.this,
                        newStepPosition);
                updateUIAfterButtonClick();
            }
        });
    }

    private void updateUIAfterButtonClick() {
        // stop the currently playing video
        player.stop();

        // set up isPlaying to true and position to 0 since we
        // want the newly selected video to start playing from the beginning
        isPlaying = true;
        currentPlayerPosition = 0;

        // refresh the UI
        if (!landscape) {
            // set this only if we are not in landscape mode
            setInstructionText();
        }
        // setup the new video and play it
        setupPlayerMediaSource();
        setButtonVisibility();
    }

    private void setInstructionText() {
        String instruction = viewModel.getStepList().get(viewModel.getStepPosition().getValue()).getDescription();
        stepInstructions.setText(instruction);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // get current player status
        isPlaying = playerView.getPlayer().getPlayWhenReady();
        currentPlayerPosition = playerView.getPlayer().getCurrentPosition();

        // put current player status in outState
        outState.putBoolean(EXTRA_PLAYER_IS_PLAYING, isPlaying);
        outState.putLong(EXTRA_CURRENT_PLAYER_POSITION, currentPlayerPosition);
    }

    // Before API Level 24 there is no guarantee of onStop being called.
    // So we have to release the player as early as possible in onPause.
    // Starting with API Level 24 (which brought multi and split window mode) onStop is guaranteed
    // to be called and in the paused mode our activity is eventually still visible.
    // Hence we need to wait releasing until onStop.
    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer( );
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if ((Util.SDK_INT <= 23 || player == null)) {
            initializePlayer();
            setupPlayerMediaSource();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Util.SDK_INT > 23) {
            initializePlayer();
            setupPlayerMediaSource();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            currentPlayerPosition = player.getCurrentPosition();
            isPlaying = player.getPlayWhenReady();
            player.stop();
            player.release();
            player = null;
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }
}
