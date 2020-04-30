package fpt.capstone.inqr.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.fragment.ChooseLocationFragment;
import fpt.capstone.inqr.fragment.MapFragment;
import fpt.capstone.inqr.model.Location;

public class LocationAdapter extends RecyclerView.Adapter<LocationHolder> {

    private ChooseLocationFragment fragment;
    private List<Location> listLocations;

    public LocationAdapter(ChooseLocationFragment fragment) {
        this.fragment = fragment;
    }

    public void setListLocations(List<Location> listLocations) {
        this.listLocations = listLocations;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_location_adapter, parent, false);

        return new LocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {

        if (position == 0) {
            holder.line.setVisibility(View.GONE);
        } else {
            holder.line.setVisibility(View.VISIBLE);
        }

        Location location = listLocations.get(position);

        holder.tvLocation.setText(location.getName());
        holder.tvFloor.setText(location.getFloorName());


        holder.itemView.setOnClickListener(v -> {
            fragment.setLocation(location);
        });
    }

    @Override
    public int getItemCount() {
        return listLocations.size();
    }
}

class LocationHolder extends RecyclerView.ViewHolder {


    TextView tvLocation, tvFloor;
    View line;

    public LocationHolder(@NonNull View itemView) {
        super(itemView);
        tvLocation = itemView.findViewById(R.id.tvLocation);
        tvFloor = itemView.findViewById(R.id.tvFloor);
        line = itemView.findViewById(R.id.line);
    }
}
