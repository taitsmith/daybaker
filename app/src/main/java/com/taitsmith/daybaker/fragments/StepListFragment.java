package com.taitsmith.daybaker.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.taitsmith.daybaker.R;
import com.taitsmith.daybaker.activities.StepSummaryActivity;
import com.taitsmith.daybaker.data.StepListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Holds the list of steps.
 */

public class StepListFragment extends Fragment {
    OnStepClickListener listener;

    @BindView(R.id.master_recycler_view)
    GridView stepRecycler;

    public StepListFragment() {
    }


    public interface OnStepClickListener {
        void onStepSelected(int position);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + "Must implement OnStepClickListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_master_list, container, false);
        ButterKnife.bind(this, rootView);
        final GridView gridView = rootView.findViewById(R.id.master_recycler_view);

        final StepListAdapter adapter = new StepListAdapter(getContext(), StepSummaryActivity.stepArray);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Trigger the callback method and pass in the position that was clicked
                listener.onStepSelected(position);
            }
        });

        return rootView;
    }
}
