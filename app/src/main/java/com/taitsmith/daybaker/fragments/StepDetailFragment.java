package com.taitsmith.daybaker.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.taitsmith.daybaker.R;

/**
 * Created by tait on 8/19/17
 */

public class StepDetailFragment extends Fragment {
    String name;

    public StepDetailFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);

        final SimpleExoPlayerView playerView = rootView.findViewById(R.id.step_video_player);
        final TextView nameView = rootView.findViewById(R.id.step_fragment_name_view);

        playerView.setDefaultArtwork(BitmapFactory.decodeResource(getResources(),
                R.drawable.exo_controls_fastforward));
        nameView.setText(name);

        return rootView;
    }

    public void setText(String string) {
        name = string;
    }
}
