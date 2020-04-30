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

public class DestinationAdapter extends RecyclerView.Adapter<DestinationHolder> {

    private ChooseDestinationFragment fragment;
    private List<Room> listRooms;

    public DestinationAdapter(ChooseDestinationFragment fragment) {
        this.fragment = fragment;
    }

    public void setListRooms(List<Room> listRooms) {
        this.listRooms = listRooms;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DestinationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_destination_adapter, parent, false);

        return new DestinationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DestinationHolder holder, int position) {

        if (position == 0) {
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


        // 3 item đầu

        if (room.getCounter() == 0) {
            holder.imgType.setImageResource(R.drawable.ic_marker);
            holder.tvCounter.setVisibility(View.INVISIBLE);
        } else {

            if (position < 3) {
                holder.imgType.setImageResource(R.drawable.ic_clock);

                holder.tvCounter.setVisibility(View.VISIBLE);
                holder.tvCounter.setText(room.getCounter() + "");
            } else {
                holder.imgType.setImageResource(R.drawable.ic_marker);
                holder.tvCounter.setVisibility(View.INVISIBLE);
            }

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

class DestinationHolder extends RecyclerView.ViewHolder {

    TextView tvDestination, tvFloor;
    View line;
    ImageView imgType;
    TextView tvCounter;

    public DestinationHolder(@NonNull View itemView) {
        super(itemView);
        tvDestination = itemView.findViewById(R.id.tvDestination);
        tvFloor = itemView.findViewById(R.id.tvFloor);
        line = itemView.findViewById(R.id.line);
        imgType = itemView.findViewById(R.id.imgType);
        tvCounter = itemView.findViewById(R.id.tvCounter);
    }
}
