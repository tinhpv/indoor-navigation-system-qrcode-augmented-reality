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
import fpt.capstone.inqr.fragment.BottomSheetFragment;
import fpt.capstone.inqr.model.supportModel.Step;

public class StepAdapter extends RecyclerView.Adapter<StepHolder> {

    private List<Step> listSteps;

    public StepAdapter(List<Step> listSteps) {
        this.listSteps = listSteps;
    }

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
            holder.imgType.setImageResource(R.drawable.current_point_new);
            holder.imgType.getLayoutParams().height = 50;
        } else if (step.getType() == Step.TYPE_END_POINT) {
            holder.imgType.setImageResource(R.drawable.destination_on_map);
            holder.imgType.getLayoutParams().height = 58;
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
        } else if (step.getType() == Step.TYPE_GO_FORWARD) {
            holder.imgType.setImageResource(R.drawable.arrow_forward);
        } else if (step.getType() == Step.TYPE_TURN_BACK) {
            holder.imgType.setImageResource(R.drawable.arrow_back);
        }

        // nếu distance = null thì ẩn tv distance đi
        if (step.getDistance() != null) {
            holder.tvDistance.setVisibility(View.VISIBLE);

            holder.tvDistance.setText("estimated " + step.getDistance());

            // TODO: TEMPORARILY DISABLE
            // set speaker
            //holder.imgSpeaker.setOnClickListener(v -> fragment.speak(step.getInfo() + " estimated " + step.getDistance()));
        } else {
            holder.tvDistance.setVisibility(View.GONE);

            // set speaker
            //holder.imgSpeaker.setOnClickListener(v -> fragment.speak(step.getInfo()));
        }

        holder.tvInfo.setText(step.getInfo());



    }

    @Override
    public int getItemCount() {
        return listSteps.size();
    }
}

class StepHolder extends RecyclerView.ViewHolder {

    ImageView imgType, imgSpeaker;
    TextView tvInfo, tvDistance;

    public StepHolder(@NonNull View itemView) {
        super(itemView);

        imgType = itemView.findViewById(R.id.imgType);
        tvInfo = itemView.findViewById(R.id.tvInfo);
        tvDistance = itemView.findViewById(R.id.tvDistance);
        imgSpeaker = itemView.findViewById(R.id.imgSpeaker);
    }
}
