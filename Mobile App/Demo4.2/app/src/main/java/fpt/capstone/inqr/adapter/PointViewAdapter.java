package fpt.capstone.inqr.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.fragment.MapFragment;

import java.util.List;

public class PointViewAdapter extends RecyclerView.Adapter<PointViewHolder> {

    private int position = 0;
    private List<String> listName;
    private MapFragment fragment;

    public PointViewAdapter(MapFragment fragment) {
        this.fragment = fragment;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    public void setListName(List<String> listName) {
        this.listName = listName;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PointViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_dot_adapter, viewGroup, false);
        return new PointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PointViewHolder item, int i) {
//        if (position == i) {
//            item.bgPoint.setVisibility(View.INVISIBLE);
//            item.bgName.setVisibility(View.VISIBLE);
//
//            item.tvName.setText(listName.get(i));
//        } else {
//            item.bgPoint.setVisibility(View.VISIBLE);
//            item.bgName.setVisibility(View.INVISIBLE);
//        }

        item.tvName.setText(listName.get(i));
        item.bgPoint.setVisibility(View.INVISIBLE);
        item.bgName.setVisibility(View.VISIBLE);
        if (position == i) {
           item.tvName.setTextSize(15);
            item.tvName.setTextColor(Color.BLACK);
        } else {
            item.tvName.setTextSize(12);
            item.tvName.setTextColor(Color.LTGRAY);
        }

        item.bgName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               fragment.chooseFloor(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (listName == null) {
            return 0;
        }
        return listName.size();
    }
}

class PointViewHolder extends RecyclerView.ViewHolder {

    View view;
    ImageView bgPoint;
    TextView tvName;
    CardView bgName;

    public PointViewHolder(@NonNull View itemView) {
        super(itemView);

        view = itemView;
        bgName = view.findViewById(R.id.bgName);
        bgPoint = view.findViewById(R.id.bgPoint);
        tvName = view.findViewById(R.id.tvName);
    }


}