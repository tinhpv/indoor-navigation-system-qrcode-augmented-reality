package fpt.capstone.inqr.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
    private TextView tvName, tvDes;
    private Button btClose, btDownload;
    private ImageView imgDataGettingType;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_warning_download_modified, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = view.findViewById(R.id.tvName);
        tvDes = view.findViewById(R.id.tvDes);
        btClose = view.findViewById(R.id.bt_close);
        btDownload = view.findViewById(R.id.bt_download);
        imgDataGettingType = view.findViewById(R.id.img_get_type);

        if (type == TYPE_DOWNLOAD) {
            tvName.setText("Building's data not found!");
            btDownload.setText("Download");
            btClose.setText("Close");
            imgDataGettingType.setImageResource(R.drawable.ic_download_dialog);
            tvDes.setText(building.getName() + " does not have data. Do you want to download?");

            btDownload.setOnClickListener(v -> {
                fragment.downloadBuildingData(building.getId(), position);
                dismiss();
            });

            btClose.setOnClickListener(v -> dismiss());
        } else if (type == TYPE_UPDATE) {
            tvName.setText("Update version available");
            btDownload.setText("Update");
            btClose.setText("Remind later");
            imgDataGettingType.setImageResource(R.drawable.ic_update_dialog);
            tvDes.setText("A new version of " + building.getName() + "'s data is available. You should update the data to ensure the efficiency of the system");

            btDownload.setOnClickListener(v -> {
                fragment.updateBuildingData(building.getId(), position);
                dismiss();
            });

            btClose.setOnClickListener(v -> {
                WarningDialog dialog = new WarningDialog("Warning", "If you continue to use the old map of " + building.getName() + ", you are at risk because the data may not be true anymore.", fragment, building.getId());
                dialog.show(fragment.getChildFragmentManager(), dialog.getTag());
                dismiss();
            });
        }


        return view;
    }
}
