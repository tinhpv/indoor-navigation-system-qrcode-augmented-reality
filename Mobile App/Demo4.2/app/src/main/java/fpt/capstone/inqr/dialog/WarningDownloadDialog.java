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
import fpt.capstone.inqr.model.Building;

public class WarningDownloadDialog extends DialogFragment {

    public static final int TYPE_DOWNLOAD = 1;
    public static final int TYPE_UPDATE = 2;
//    private String buildingName, description;
    private TextView tvName, tvDes, tvClose, tvDownload;
    private ListBuildingFragment fragment;
    private int type;
    private Building building;
    private int position;


    public WarningDownloadDialog(ListBuildingFragment fragment, int type, Building building, int position) {
        this.fragment = fragment;
        this.type = type;
        this.building = building;
        this.position = position;
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
        View view = inflater.inflate(R.layout.dialog_warning_download, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = view.findViewById(R.id.tvName);
        tvDes = view.findViewById(R.id.tvDes);
        tvClose = view.findViewById(R.id.tvClose);
        tvDownload = view.findViewById(R.id.tvDownload);


        tvName.setText("Warning");

        if (type == TYPE_DOWNLOAD) {
            tvDownload.setText("Download");
            tvClose.setText("Close");

            tvDes.setText(building.getName() + " does not have data. Do you want to download?");

            tvDownload.setOnClickListener(v -> {
                fragment.downloadBuildingData(building.getId(), position);
                dismiss();
            });

            tvClose.setOnClickListener(v -> dismiss());
        } else if (type == TYPE_UPDATE) {
            tvDownload.setText("Update");
            tvClose.setText("Use old data");

            tvDes.setText(building.getName() + " has new data. Do you want to update?");

            tvDownload.setOnClickListener(v -> {
                fragment.updateBuildingData(building.getId(), position);
                dismiss();
            });

            tvClose.setOnClickListener(v -> {
                fragment.showMapFragment(building.getId());
                dismiss();
            });
        }



        return view;
    }
}
