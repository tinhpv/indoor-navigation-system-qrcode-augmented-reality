package fpt.capstone.inqr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.callbacks.RoomTappedListener;
import fpt.capstone.inqr.fragment.ChangeDestinationBottomSheetFragment;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Room;

/**
 * Demo4
 * Created by TinhPV on 4/14/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomViewHolder> implements Filterable {

    private Context mContext;
    private List<Room> mRoomList;
    private List<Room> mRoomListFull;
    private RoomTappedListener mTappedListener;

    public RoomAdapter(Context context, List<Room> roomList, RoomTappedListener roomTappedListener) {
        mContext = context;
        mTappedListener = roomTappedListener;
        mRoomList = roomList;
        mRoomListFull = new ArrayList<>(mRoomList);
    }

    @NonNull
    @Override
    public RoomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_item_room, parent, false);
        RoomViewHolder viewHolder = new RoomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RoomViewHolder holder, int position) {
        holder.tvRoomName.setText(mRoomList.get(position).getName());
    }



    @Override
    public int getItemCount() {
        return mRoomList.size();
    }

    @Override
    public Filter getFilter() {
        return searchFilter;
    }

    public Filter searchFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Room> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(mRoomListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                mRoomListFull.forEach(room -> {
                    if (room.getName().toLowerCase().contains(filterPattern))
                        filteredList.add(room);
                }); // end for
            } // end if filter matching

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mRoomList.clear();
            mRoomList.addAll((List<Room>) results.values);
            notifyDataSetChanged();
        }
    };

    class RoomViewHolder extends RecyclerView.ViewHolder {

        private TextView tvRoomName;

        RoomViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRoomName = itemView.findViewById(R.id.tv_room_name);
            itemView.setOnClickListener(v -> mTappedListener.onRoomTapped(mRoomList.get(getAdapterPosition()).getName()));
        }
    }
}
