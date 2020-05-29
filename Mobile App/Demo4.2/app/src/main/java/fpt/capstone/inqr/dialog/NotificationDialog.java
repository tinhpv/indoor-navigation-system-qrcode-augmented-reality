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

import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.model.supportModel.Notification;

public class NotificationDialog extends DialogFragment {

    //    private String buildingName, description;
    private TextView tvName, tvDes;
    private Button btClose;

    private List<Notification> listNotification;

    public NotificationDialog(List<Notification> listNotification) {
        this.listNotification = listNotification;
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
        View view = inflater.inflate(R.layout.dialog_notification_modified, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = view.findViewById(R.id.tvName);
        tvDes = view.findViewById(R.id.tvDes);
        btClose = view.findViewById(R.id.bt_close);


        tvName.setText("What's new");

        String buildingAdd = "";
        String buildingUpdate = "";
        String buildingRemove = "";
        for (Notification noti : listNotification) {
            if (noti.getType() == Notification.TYPE_ADD) {
                buildingAdd += " - " + noti.getBuildingName() + "\n";
            } else if (noti.getType() == Notification.TYPE_UPDATE) {
                buildingUpdate += " - " + noti.getBuildingName() + "\n";
            } else if (noti.getType() == Notification.TYPE_REMOVE) {
                buildingRemove += " - " + noti.getBuildingName() + "\n";
            }
        }

        String message = "";
        if (!buildingAdd.isEmpty()) {
            message += "New building(s):" + "\n" + buildingAdd + "\n";
        }
        if (!buildingUpdate.isEmpty()) {
            message += "Updated building(s):" + "\n" + buildingUpdate + "\n";
        }
        if (!buildingRemove.isEmpty()) {
            message += "Remove building(s):" + "\n" + buildingRemove + "\n";
        }

        tvDes.setText(message);

        btClose.setOnClickListener(v -> dismiss());

        return view;
    }
}
