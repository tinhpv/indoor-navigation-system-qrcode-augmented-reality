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
import fpt.capstone.inqr.model.supportModel.Step;

public class StepAdapter extends RecyclerView.Adapter<StepHolder> {

    private List<Step> listSteps;

    public void setListSteps(List<Step> listSteps) {
        this.listSteps = listSteps;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StepHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_step_adapter, parent, false);

        return new StepHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StepHolder holder, int position) {

        Step step = listSteps.get(position);
        // set UI theo Type
        if (step.getType() == Step.TYPE_START_POINT) {
            holder.imgType.setImageResource(R.drawable.current_point);
        } else if (step.getType() == Step.TYPE_END_POINT) {
            holder.imgType.setImageResource(R.drawable.destination_on_map);
        } else if (step.getType() == Step.TYPE_GO_STRAIGHT) {
            holder.imgType.setImageResource(R.drawable.go_ahead);
        } else if (step.getType() == Step.TYPE_TURN_LEFT) {
            holder.imgType.setImageResource(R.drawable.turn_left);
        } else if (step.getType() == Step.TYPE_TURN_RIGHT) {
            holder.imgType.setImageResource(R.drawable.turn_right);
        } else if (step.getType() == Step.TYPE_UP_STAIR) {
            holder.imgType.setImageResource(R.drawable.stairs_up);
        } else if (step.getType() == Step.TYPE_DOWN_STAIR) {
            holder.imgType.setImageResource(R.drawable.stairs_down);
        }

        // nếu distance = null thì ẩn tv distance đi
        if (step.getDistance() != null) {
            holder.tvDistance.setVisibility(View.VISIBLE);

            holder.tvDistance.setText(step.getDistance());
        } else {
            holder.tvDistance.setVisibility(View.GONE);
        }

        holder.tvInfo.setText(step.getInfo());

    }

    @Override
    public int getItemCount() {
        return listSteps.size();
    }
}

class StepHolder extends RecyclerView.ViewHolder {

    ImageView imgType;
    TextView tvInfo, tvDistance;

    public StepHolder(@NonNull View itemView) {
        super(itemView);

        imgType = itemView.findViewById(R.id.imgType);
        tvInfo = itemView.findViewById(R.id.tvInfo);
        tvDistance = itemView.findViewById(R.id.tvDistance);
    }
}
