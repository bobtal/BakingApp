package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gmail.at.boban.talevski.bakingapp.R;
import com.gmail.at.boban.talevski.bakingapp.viewmodel.RecipeStepDetailsViewModel;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class RecipeStepDetailsFragment extends Fragment {

    private static final String EXTRA_PLAYER_IS_PLAYING = "EXTRA_PLAYER_IS_PLAYING";
    private static final String EXTRA_CURRENT_PLAYER_POSITION = "EXTRA_CURRENT_PLAYER_POSITION";
    private static final String EXTRA_CURRENT_STEP = "EXTRA_CURRENT_STEP";

    private RecipeStepDetailsViewModel detailsViewModel;
    private PlayerView playerView;
    private TextView stepInstructions;
    private SimpleExoPlayer player;
    private ImageView noVideoImageView;

    // variables to keep player state
    private boolean isPlaying;
    private long currentPlayerPosition;
    // a "hack variable" to prevent observer's onChanged method from executing
    // if there wasn't actually a change in the step
    // it executes on initialization and/or rotation "for no apparent reason"
    // which caused the currentPlayerPosition to be reset to 0 even if we didn't change the step
    // and only rotated the device.
    // Debate on whether this behavior is a bug or it's by design
    // https://stackoverflow.com/questions/50236778/why-livedata-observer-is-being-triggered-twice-for-a-newly-attached-observer
    private int currentStep;

    // Mandatory empty constructor
    public RecipeStepDetailsFragment() {}

    public static RecipeStepDetailsFragment newInstance() {
        return new RecipeStepDetailsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recipe_step_details_fragment, container, false);
        playerView = view.findViewById(R.id.recipe_step_details_player_view);
        stepInstructions = view.findViewById(R.id.recipe_step_details_instructions_textview);
        noVideoImageView = view.findViewById(R.id.recipe_step_details_no_video_imageview);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(EXTRA_PLAYER_IS_PLAYING)
                && savedInstanceState.containsKey(EXTRA_CURRENT_PLAYER_POSITION)
                && savedInstanceState.containsKey(EXTRA_CURRENT_STEP)) {
            // setup the state of the player if there are saved values in savedInstanceState
            currentStep = savedInstanceState.getInt(EXTRA_CURRENT_STEP);
            isPlaying = savedInstanceState.getBoolean(EXTRA_PLAYER_IS_PLAYING);
            currentPlayerPosition = savedInstanceState.getLong(EXTRA_CURRENT_PLAYER_POSITION);
        } else {
            // set defaults if there are not, player should start playing from the beginning
            currentStep = 0;
            isPlaying = true;
            currentPlayerPosition = 0;
        }

        detailsViewModel = ViewModelProviders.of(getActivity()).get(RecipeStepDetailsViewModel.class);
        detailsViewModel.getStepPosition().observe(getActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(@Nullable Integer newStep) {
                if (currentStep == newStep) {
                    // do nothing if there's no change in the step and
                    // onChanged is triggered on initialization
                } else {
                    // do the stuff supposed to be done on actual change in the step

                    // stop the player
                    playerView.getPlayer().stop();

                    // update the step instructions using the new step instructions from the viewmodel
                    setInstructionText();

                    // we want the player to start playing from the beginning of the video
                    // automatically on switching steps
                    isPlaying = true;
                    currentPlayerPosition = 0;
                    setupPlayerMediaSource();

                    // set the current step to the new one
                    // to keep the hack working
                    currentStep = newStep;
                }
            }
        });

        setInstructionText();
        initializePlayer();
        setupPlayerMediaSource();
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(getActivity());
        playerView.setPlayer(player);
    }

    private void setInstructionText() {
        String instruction = detailsViewModel.getStepList()
                .get(detailsViewModel.getStepPosition().getValue()).getDescription();
        stepInstructions.setText(instruction);
    }

    private void setupPlayerMediaSource() {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), getString(R.string.app_name)));

        String videoUrl = detailsViewModel.getStepList()
                .get(detailsViewModel.getStepPosition().getValue()).getVideoUrl();

        if (videoUrl == null || videoUrl.isEmpty()) {
            // hide the player and show the no video image
            playerView.setVisibility(View.GONE);
            noVideoImageView.setVisibility(View.VISIBLE);
        } else {
            // show and set up the player
            playerView.setVisibility(View.VISIBLE);
            noVideoImageView.setVisibility(View.GONE);

            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(videoUrl));
            // Prepare the player with the source.
            player.prepare(videoSource);

            // seek to the specified position
            player.seekTo(currentPlayerPosition);

            // Start playing the sample
            player.setPlayWhenReady(isPlaying);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // save the state in the outState
        boolean isPlaying = playerView.getPlayer().getPlayWhenReady();
        long currentPlayerPosition = playerView.getPlayer().getCurrentPosition();
        int currentStep = detailsViewModel.getStepPosition().getValue();
        outState.putBoolean(EXTRA_PLAYER_IS_PLAYING, isPlaying);
        outState.putLong(EXTRA_CURRENT_PLAYER_POSITION, currentPlayerPosition);
        outState.putInt(EXTRA_CURRENT_STEP, currentStep);

        // pause the player
        playerView.getPlayer().setPlayWhenReady(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer(true);
    }

    private void releasePlayer(boolean releaseView) {
        playerView.getPlayer().stop();
        playerView.getPlayer().release();
        if (releaseView) {
            playerView = null;
        }
    }

}
