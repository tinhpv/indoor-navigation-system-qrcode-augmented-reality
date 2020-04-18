package fpt.capstone.inqr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.vipulasri.timelineview.TimelineView;

import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.model.Location;

/**
 * Demo4
 * Created by TinhPV on 4/14/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class LocationStepAdapter extends RecyclerView.Adapter<LocationStepAdapter.LocationStepViewHolder> {

    private Context mContext;
    private List<Location> mLocationList;

    public LocationStepAdapter(Context context, List<Location> locationList) {
        mContext = context;
        mLocationList = locationList;
    }

    @NonNull
    @Override
    public LocationStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.location_item, parent, false);
        LocationStepViewHolder viewHolder = new LocationStepViewHolder(view, viewType);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull LocationStepViewHolder holder, int position) {
        holder.tvLocationStep.setText(mLocationList.get(position).getName());
    }

    @Override
    public int getItemViewType(int position) {
        return TimelineView.getTimeLineViewType(position, getItemCount());
    }

    @Override
    public int getItemCount() {
        return mLocationList.size();
    }

    public class LocationStepViewHolder extends RecyclerView.ViewHolder {
        private TimelineView mTimelineView;
        private TextView tvLocationStep;

        public LocationStepViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            mTimelineView = itemView.findViewById(R.id.timeline);
            mTimelineView.initLine(viewType);
            tvLocationStep = itemView.findViewById(R.id.tv_location_step);
        }
    }
}
