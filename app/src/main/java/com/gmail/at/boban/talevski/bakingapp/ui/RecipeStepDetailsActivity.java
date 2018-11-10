package com.gmail.at.boban.talevski.bakingapp.ui;

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

    private static final String EXTRA_PLAYER_IS_PLAYING = "EXTRA_PLAYER_IS_PLAYING";
    private static final String EXTRA_CURRENT_PLAYER_POSITION = "EXTRA_CURRENT_PLAYER_POSITION";
    private static final String EXTRA_PLAYER_IS_PLAYING_IN_ON_PAUSE = "EXTRA_PLAYER_IS_PLAYING_IN_ON_PAUSE";

    private PlayerView playerView;
    private TextView stepInstructions;
    private Button nextStepButton, previousStepButton;
    private SimpleExoPlayer player;
    private ImageView noVideoImageView;

    private boolean landscape;

    private RecipeStepDetailsViewModel viewModel;
    private boolean isPlaying;

    // separate boolean field to indicate if player was playing in onPause since we don't know
    // for sure if onSaveInstance state will be called before or after onPause (on anything other than Pie).
    // And it's a cool challenge to keep the state if we get only to onPause and back to onResume
    // without going to onStop. Shouldn't happen most of the time though.
    private boolean isPlayingInOnPause;

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
                    savedInstanceState.containsKey(EXTRA_PLAYER_IS_PLAYING_IN_ON_PAUSE)) {
                isPlaying = savedInstanceState.getBoolean(EXTRA_PLAYER_IS_PLAYING);
                isPlayingInOnPause = savedInstanceState.getBoolean(EXTRA_PLAYER_IS_PLAYING_IN_ON_PAUSE);

                // and restore the player position
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
                updateUIAfterButtonClick();
            }
        });

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewModel.setStepPosition(viewModel.getStepPosition().getValue() + 1);
                updateUIAfterButtonClick();
            }
        });
    }

    private void updateUIAfterButtonClick() {
        // stop the currently playing video
        playerView.getPlayer().stop();

        // set up isPlaying to true cause we want the newly selected video to start playing
        isPlaying = true;

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

        // put both booleans in the outState, we don't know if this method or onPause is called first
        // if we need to start playing in onResume, at least one of these will be true
        // if we need to keep the player paused in onResume, both will be false
        outState.putBoolean(EXTRA_PLAYER_IS_PLAYING, isPlaying);
        outState.putBoolean(EXTRA_PLAYER_IS_PLAYING_IN_ON_PAUSE, isPlayingInOnPause);

        currentPlayerPosition = playerView.getPlayer().getCurrentPosition();
        outState.putLong(EXTRA_CURRENT_PLAYER_POSITION, currentPlayerPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // get current player status
        isPlayingInOnPause = playerView.getPlayer().getPlayWhenReady();
        // pause the player
        playerView.getPlayer().setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Since we don't know if we are resuming from a paused or stopped
        // activity state (or it's a fresh start altogether),
        // we check if either of isPlaying booleans is true to set current playing status to true
        playerView.getPlayer().setPlayWhenReady(isPlaying || isPlayingInOnPause);
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
