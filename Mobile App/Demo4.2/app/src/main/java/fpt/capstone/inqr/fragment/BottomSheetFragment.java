package fpt.capstone.inqr.fragment;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.StepAdapter;
import fpt.capstone.inqr.model.supportModel.Step;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private TextToSpeech textToSpeech;

    private RecyclerView rvStep;
    private TextView tvDistance, tvTime;

    private List<Step> listSteps;
    private String distance, time;

    public BottomSheetFragment() {
        // Required empty public constructor
    }

    public BottomSheetFragment(List<Step> listSteps, String distance, String time) {
        this.listSteps = listSteps;
        this.distance = distance;
        this.time = time;
    }

    @Override
    public void onPause() {
        if(textToSpeech !=null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        textToSpeech = new TextToSpeech(getContext(), status -> {
            if (status != TextToSpeech.ERROR) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bottom_sheet_dialog, container, false);


        rvStep = view.findViewById(R.id.rvStep);
        tvDistance = view.findViewById(R.id.tvDistance);
        tvTime = view.findViewById(R.id.tvTime);

        tvDistance.setText(distance);
        tvTime.setText(time);

        StepAdapter adapter = new StepAdapter(listSteps);
//        adapter.setListSteps(listSteps);

        rvStep.setLayoutManager(new LinearLayoutManager(view.getContext(), RecyclerView.VERTICAL, false));
        rvStep.setAdapter(adapter);

        return view;
    }

    public void speak(String message) {
        String utteranceId = UUID.randomUUID().toString();
        textToSpeech.speak(message, TextToSpeech.QUEUE_ADD, null, utteranceId);
    }
}
