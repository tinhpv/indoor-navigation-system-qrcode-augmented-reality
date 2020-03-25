package fpt.capstone.inqr.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.fragment.ListBuildingFragment;

public class DeleteDialog extends DialogFragment {

    private ListBuildingFragment fragment;
    private TextView  tvName, tvDelete, tvCancel;
    private String buildingId, buildingName;

    public DeleteDialog(ListBuildingFragment fragment, String buildingId, String buildingName) {
        this.fragment = fragment;
        this.buildingId = buildingId;
        this.buildingName = buildingName;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_delete, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = view.findViewById(R.id.tvName);
        tvCancel = view.findViewById(R.id.tvCancel);
        tvDelete = view.findViewById(R.id.tvDelete);

        tvName.setText("Delete " + buildingName + " data?");

        tvCancel.setOnClickListener(v -> dismiss());
        tvDelete.setOnClickListener(v -> {
            fragment.deleteBuildingData(buildingId);
            dismiss();
        });

        return view;
    }
}
