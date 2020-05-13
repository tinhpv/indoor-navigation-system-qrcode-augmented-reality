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

/**
 * Demo4
 * Created by TinhPV on 2020-05-13
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class InternetWarningDialog extends DialogFragment {

    private TextView tvName, tvDes;
    private Button btClose;


    public InternetWarningDialog() {
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
        View view = inflater.inflate(R.layout.dialog_warning_internet, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = view.findViewById(R.id.tvName);
        tvDes = view.findViewById(R.id.tvDes);
        btClose = view.findViewById(R.id.bt_close);


        tvName.setText("Internet connection is required");
        tvDes.setText("Please check your internet connection! AR Navigation requires Internet!");

        btClose.setOnClickListener(v -> {
            dismiss();
        });

        return view;
    }
}

