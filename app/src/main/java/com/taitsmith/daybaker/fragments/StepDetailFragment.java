package com.taitsmith.daybaker.fragments;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.taitsmith.daybaker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by tait on 8/19/17
 */

public class StepDetailFragment extends Fragment {
    private String shortDescription, description;
    private Uri videoUri;
    private String uriString;
    private SimpleExoPlayer player;

    @BindView(R.id.step_video_player)
    SimpleExoPlayerView playerView;
    @BindView(R.id.step_fragment_name_view)
    TextView nameView;

    public StepDetailFragment(){}

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null){
            if (videoUri != null) {
                videoUri = Uri.parse(savedInstanceState.get("VIDEO_URI").toString());
            }
            description = savedInstanceState.getString("DESCRIPTION");
        } else {
            videoUri = null;
            description = getString(R.string.step_detail_fragment_default);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);

        nameView.setText(shortDescription);
        initializePlayer(videoUri);

        return rootView;
    }

    public void setShortDescription(String shortDesc) {
        shortDescription = shortDesc;
    }

    public void setDescription(String desc){
        description = desc;
    }

    public void setVideoUri(String url){
        videoUri = Uri.parse(url);
    }

    public void initializePlayer(Uri videoUri) {
        if (player == null) {
            TrackSelector selector = new DefaultTrackSelector();
            LoadControl control = new DefaultLoadControl();

            player = ExoPlayerFactory.newSimpleInstance(getContext(), selector, control);
            playerView.setPlayer(player);
            String agent = Util.getUserAgent(getContext(), "DayBaker");
            if (videoUri == null || videoUri.toString().equals("")) {
                playerView.setDefaultArtwork(BitmapFactory.decodeResource(
                        getResources(), R.drawable.sad
                ));
            } else {
                MediaSource source = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                        getContext(), agent), new DefaultExtractorsFactory(), null, null);
                player.prepare(source);
                player.setPlayWhenReady(true);
                playerView.hideController();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        player.stop();
        player.release();
        player = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (videoUri == null){
            uriString = "";
        }
        outState.putString("VIDEO_URI", uriString);
        outState.putString("DESCRIPTION", description);
        super.onSaveInstanceState(outState);
    }
}
