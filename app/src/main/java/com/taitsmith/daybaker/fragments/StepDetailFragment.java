package com.taitsmith.daybaker.fragments;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
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
import com.squareup.picasso.Picasso;
import com.taitsmith.daybaker.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.taitsmith.daybaker.activities.StepSummaryActivity.SHARED_PREFS;

/**
 * Created by tait on 8/19/17
 */

public class StepDetailFragment extends Fragment {
    private String shortDescription;
    private Uri videoUri;
    private String uriString;
    private SimpleExoPlayer player;
    private SharedPreferences preferences;
    private long playerPosition;

    @BindView(R.id.step_video_player)
    SimpleExoPlayerView playerView;
    @BindView(R.id.step_fragment_name_view)
    TextView stepDetailTv;
    @BindView(R.id.noVideoImageView)
    ImageView noVideoIv;

    public StepDetailFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step, container, false);
        ButterKnife.bind(this, rootView);
        preferences = getContext().getSharedPreferences(SHARED_PREFS, 0);

        //for some reason there's a weird issue with getting the player position
        //from savedInstanceState. This works.
        if (savedInstanceState != null) {
            playerPosition = preferences.getLong("PLAYER_POSITION", C.TIME_UNSET);
            shortDescription = savedInstanceState.getString("DESCRIPTION");
            setVideoUri(savedInstanceState.getString("VIDEO_URI"));
            Log.d("TIME LOG ", Long.toString(playerPosition));
        }

        initializePlayer(videoUri);

        stepDetailTv.setText(shortDescription);

        Log.d("ON CREATE", " 1");
        return rootView;
    }



    public void setDescription(String shortDesc) {
        shortDescription = shortDesc;
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

            if (videoUri == null || videoUri.toString().isEmpty()) {
                hidePlayer(true);
            } else {
                hidePlayer(false);

                MediaSource source = new ExtractorMediaSource(videoUri, new DefaultDataSourceFactory(
                        getContext(), agent), new DefaultExtractorsFactory(), null, null);
                player.seekTo(playerPosition);
                player.prepare(source);
                player.setPlayWhenReady(true);
                playerView.hideController();

                Log.d("TIME LOG II", Long.toString(playerPosition));
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences.Editor editor= preferences.edit();

        if (player != null) {
            editor.putLong("PLAYER_POSITION", player.getContentPosition());
            player.release();
            player = null;
        }
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (videoUri != null) {
            initializePlayer(videoUri);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (videoUri == null){
            uriString = "";
        } else {
            uriString = videoUri.toString();
        }
        outState.putLong("PLAYER_POSITION", playerPosition);
        outState.putString("VIDEO_URI", uriString);
        outState.putString("DESCRIPTION", shortDescription);
        super.onSaveInstanceState(outState);
    }

    //hide the player and show the sad android.
    //maybe the json has an image thumbnail instead,
    //so we'll show that if it does.
    public void hidePlayer(boolean hide) throws NullPointerException {
        String imageUrl = preferences.getString("IMAGE_URL", null);
        if (hide) {
            playerView.setVisibility(View.INVISIBLE);
            noVideoIv.setVisibility(View.VISIBLE);
            try {
                if (!imageUrl.isEmpty()) {
                    showImageThumbnail(imageUrl);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        } else {
            playerView.setVisibility(View.VISIBLE);
            noVideoIv.setVisibility(View.INVISIBLE);
        }
    }

    public void showImageThumbnail(String imageUrl){
        Picasso.with(getContext()).load(imageUrl).into(noVideoIv);
    }
}