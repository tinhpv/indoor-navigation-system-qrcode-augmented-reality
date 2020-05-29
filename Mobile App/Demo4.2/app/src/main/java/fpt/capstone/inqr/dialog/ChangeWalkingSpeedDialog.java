package fpt.capstone.inqr.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.fragment.ListBuildingFragment;
import fpt.capstone.inqr.helper.InputFilterMinMax;
import fpt.capstone.inqr.helper.PreferenceHelper;
import fpt.capstone.inqr.model.Building;

public class ChangeWalkingSpeedDialog extends DialogFragment {

    //    private String buildingName, description;
    private TextView tvName, tvDes;
    private Button btCancel, btSave;
    private Spinner snChooseSex;
    private EditText edtAge;
    private ListBuildingFragment fragment;
    private Building building;
    private int position;

    private TextView tvCurrentSpeed, tvNewSpeed;

    private float speed;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogStyle);

        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_change_walking_speed, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        tvName = view.findViewById(R.id.tvName);
        tvDes = view.findViewById(R.id.tvDes);
        btCancel = view.findViewById(R.id.bt_cancel);
        btSave = view.findViewById(R.id.bt_save);
        snChooseSex = view.findViewById(R.id.snChooseSex);
        edtAge = view.findViewById(R.id.edtAge);
        tvCurrentSpeed = view.findViewById(R.id.tvCurrentSpeed);
        tvNewSpeed = view.findViewById(R.id.tvNewSpeed);


        tvName.setText("Walking-speed");

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sex_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        snChooseSex.setAdapter(adapter);

        snChooseSex.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!edtAge.getText().toString().isEmpty()) {
//                    int number = Integer.parseInt(edtAge.getText().toString());
//                    if (number < 18) {
//                        edtAge.setError("Age is between 18 and 90");
//                    } else {
                    speed = getSpeed();
                    tvNewSpeed.setText(speed + " (km/h)");
//                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        int currentSpeed = PreferenceHelper.getInt(getContext(), "speed_walking");

        if (currentSpeed == 0) {
            currentSpeed = getActivity().getResources().getInteger(R.integer.speed_walking);
        }

        tvCurrentSpeed.setText(currentSpeed / 1000 + "." + currentSpeed % 1000 + " (km/h)");

        edtAge.setFilters(new InputFilter[]{new InputFilterMinMax(1, 90)});
        edtAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!edtAge.getText().toString().isEmpty()) {
                    int number = Integer.parseInt(edtAge.getText().toString());
                    if (number < 18) {
                        edtAge.setError("Age is between 18 and 90");
                    } else {
                        speed = getSpeed();
                        tvNewSpeed.setText(speed + " (km/h)");
                    }
                } else {
                    edtAge.setError("Age is between 18 and 90");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btSave.setOnClickListener(v -> {
            PreferenceHelper.putInt(getContext(), "speed_walking", (int) (speed * 1000));
            ///
            dismiss();
        });

        btCancel.setOnClickListener(v -> dismiss());

        // chạy lần đầu mở luôn
        speed = getSpeed();
        tvNewSpeed.setText(speed + " (km/h)");

        return view;
    }

    private float getSpeed() {
        int age = Integer.parseInt(edtAge.getText().toString());
        String gender = snChooseSex.getSelectedItem().toString();

        float speed = 0f;

        if (age >= 18 && age <= 29) {
            if (gender.equals("Male")) {
                speed = 4.891f;
            } else {
                speed = 4.827f;
            }
        } else if (age >= 30 && age <= 39) {
            if (gender.equals("Male")) {
                speed = 5.149f;
            } else {
                speed = 4.827f;
            }
        } else if (age >= 40 && age <= 49) {
            if (gender.equals("Male")) {
                speed = 5.149f;
            } else {
                speed = 5.004f;
            }
        } else if (age >= 50 && age <= 59) {
            if (gender.equals("Male")) {
                speed = 5.149f;
            } else {
                speed = 4.714f;
            }
        } else if (age >= 60 && age <= 69) {
            if (gender.equals("Male")) {
                speed = 4.827f;
            } else {
                speed = 4.457f;
            }
        } else if (age >= 70 && age <= 79) {
            if (gender.equals("Male")) {
                speed = 4.537f;
            } else {
                speed = 4.071f;
            }
        } else if (age >= 80 && age <= 90) {
            if (gender.equals("Male")) {
                speed = 3.492f;
            } else {
                speed = 3.379f;
            }
        }

        return speed;
    }
}
