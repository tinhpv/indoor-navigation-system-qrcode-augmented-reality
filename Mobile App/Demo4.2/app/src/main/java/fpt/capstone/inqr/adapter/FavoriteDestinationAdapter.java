package fpt.capstone.inqr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.fragment.ChooseDestinationFragment;
import fpt.capstone.inqr.model.Room;

public class FavoriteDestinationAdapter extends RecyclerView.Adapter<FavoriteDestinationHolder> {

    private ChooseDestinationFragment fragment;
    private List<Room> listRooms;

    public FavoriteDestinationAdapter(ChooseDestinationFragment fragment) {
        this.fragment = fragment;
    }

    public void setListRooms(List<Room> listRooms) {
        this.listRooms = listRooms;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FavoriteDestinationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_destination_adapter, parent, false);

        return new FavoriteDestinationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteDestinationHolder holder, int position) {

        if (position == listRooms.size() - 1) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }

        Room room = listRooms.get(position);

        holder.tvDestination.setText(room.getName());
        holder.tvFloor.setText(room.getFloorName());

        if (room.isSpecialRoom()) {
            holder.tvFloor.setVisibility(View.GONE);
        } else {
            holder.tvFloor.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            fragment.setDestination(room);
        });
    }

    @Override
    public int getItemCount() {
        return listRooms.size();
    }
}

class FavoriteDestinationHolder extends RecyclerView.ViewHolder {

    TextView tvDestination, tvFloor;
    View line;

    public FavoriteDestinationHolder(@NonNull View itemView) {
        super(itemView);
        tvDestination = itemView.findViewById(R.id.tvDestination);
        tvFloor = itemView.findViewById(R.id.tvFloor);
        line = itemView.findViewById(R.id.line);
    }
}
