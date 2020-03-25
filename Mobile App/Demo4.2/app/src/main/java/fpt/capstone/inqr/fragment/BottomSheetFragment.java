package fpt.capstone.inqr.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.StepAdapter;
import fpt.capstone.inqr.model.supportModel.Step;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private RecyclerView rvStep;

    private List<Step> listSteps;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public void setListSteps(List<Step> listSteps) {
        this.listSteps = listSteps;

//        this.listSteps = new ArrayList<>();
//        this.listSteps.add(new Step(Step.TYPE_START_POINT, "Vị trí của bạn - phòng 1", null));
//        this.listSteps.add(new Step(Step.TYPE_GO_AHEAD, "Đi tiếp", "100m"));
//        this.listSteps.add(new Step(Step.TYPE_UP_STAIR, "lên lầu", "120m"));
//        this.listSteps.add(new Step(Step.TYPE_TURN_RIGHT, "Rẽ phải", null));
//        this.listSteps.add(new Step(Step.TYPE_GO_AHEAD, "Đi tiếp", "100m"));
//        this.listSteps.add(new Step(Step.TYPE_UP_STAIR, "lên lầu", "120m"));
//        this.listSteps.add(new Step(Step.TYPE_TURN_RIGHT, "Rẽ phải", null));
//        this.listSteps.add(new Step(Step.TYPE_GO_AHEAD, "Đi tiếp", "100m"));
//        this.listSteps.add(new Step(Step.TYPE_UP_STAIR, "lên lầu", "120m"));
//        this.listSteps.add(new Step(Step.TYPE_TURN_RIGHT, "Rẽ phải", null));
//        this.listSteps.add(new Step(Step.TYPE_END_POINT, "Điểm đến - phòng 2", null));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);

        rvStep = view.findViewById(R.id.rvStep);

        StepAdapter adapter = new StepAdapter();
        adapter.setListSteps(listSteps);

        rvStep.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        rvStep.setAdapter(adapter);

        return view;
    }
}
