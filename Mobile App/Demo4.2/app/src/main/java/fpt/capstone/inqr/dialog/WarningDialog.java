package fpt.capstone.inqr.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.fragment.ListBuildingFragment;

public class WarningDialog extends DialogFragment {

    private String buildingName, description;
    private TextView tvName, tvDes;
    private Button btClose, btAccept;

    private ListBuildingFragment fragment;
    private String buildingId;

    public WarningDialog(String buildingName, String description) {
        this.buildingName = buildingName;
        this.description = description;
    }

    public WarningDialog(String buildingName, String description, ListBuildingFragment fragment, String buildingId) {
        this.buildingName = buildingName;
        this.description = description;
        this.fragment = fragment;
        this.buildingId = buildingId;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

//        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_warning_modified, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = view.findViewById(R.id.tvName);
        tvDes = view.findViewById(R.id.tvDes);
        btClose = view.findViewById(R.id.bt_close);
        btAccept = view.findViewById(R.id.bt_accept);


        tvName.setText(buildingName);
        tvDes.setText(description);

        btClose.setOnClickListener(v -> {
            dismiss();
        });

        btAccept.setOnClickListener(v -> {

            if (fragment != null) {
                fragment.showMapFragment(buildingId);
            }

            dismiss();
        });

        return view;
    }
}
