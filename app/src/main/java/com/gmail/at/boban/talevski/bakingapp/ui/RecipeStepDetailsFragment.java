package com.gmail.at.boban.talevski.bakingapp.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private RecipeStepDetailsViewModel mViewModel;
    private PlayerView playerView;
    private TextView stepInstructions;
    private Button nextStepButton, previousStepButton;
    private SimpleExoPlayer player;

    private boolean landscape;

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
        nextStepButton = view.findViewById(R.id.recipe_step_details_button_next);
        previousStepButton = view.findViewById(R.id.recipe_step_details_button_previous);
        if (stepInstructions == null) {
            // step instructions view is not present in the landscape layout
            // so if that's the case we are in landscape mode
            landscape = true;
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(RecipeStepDetailsViewModel.class);

        if (!landscape) {
            // set this only if we are not in landscape mode
            setInstructionText();
        }
        initializePlayer();
        setupPlayerMediaSource();
        initializeButtons();
        setButtonVisibility();
    }

    private void initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(getActivity());
        playerView.setPlayer(player);
    }

    private void initializeButtons() {
        previousStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setStepPosition(mViewModel.getStepPosition() - 1);
                playerView.getPlayer().stop();
                updateUI();
            }
        });

        nextStepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mViewModel.setStepPosition(mViewModel.getStepPosition() + 1);
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
        String instruction = mViewModel.getStepList().get(mViewModel.getStepPosition()).getShortDescription() +
                "\n" + mViewModel.getStepList().get(mViewModel.getStepPosition()).getDescription();
        stepInstructions.setText(instruction);
    }

    private void setupPlayerMediaSource() {
        // Produces DataSource instances through which media data is loaded.
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(getActivity(),
                Util.getUserAgent(getActivity(), getString(R.string.app_name)));
        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(Uri.parse(mViewModel.getStepList().get(mViewModel.getStepPosition()).getVideoUrl()));
        // Prepare the player with the source.
        player.prepare(videoSource);

        // Start playing the sample
        player.setPlayWhenReady(true);
    }

    private void setButtonVisibility() {
        if (mViewModel.getStepPosition() == 0) {
            // we are at the first step, no need for previous button
            previousStepButton.setVisibility(View.GONE);
        } else {
            // make sure previous button is set to visible cause it may have already
            // been removed if the user was at the first step at some point
            previousStepButton.setVisibility(View.VISIBLE);
        }
        if (mViewModel.getStepPosition() == mViewModel.getStepList().size() - 1) {
            // we are at the last step, no need for next button
            nextStepButton.setVisibility(View.GONE);
        } else {
            // make sure next button is set to visible cause it may have already
            // been removed if the user was at the last step at some point
            nextStepButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        releasePlayer();
    }

    private void releasePlayer() {
        playerView.getPlayer().stop();
        playerView.getPlayer().release();
        playerView = null;
    }

}
