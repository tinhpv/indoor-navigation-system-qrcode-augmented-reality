package fpt.capstone.inqr.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.LocationAdapter;
import fpt.capstone.inqr.model.Location;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseLocationFragment extends BaseFragment {

    private ImageView imgBack;
    private EditText edtInput;
    private LinearLayout bgScan;
    private RecyclerView rvLocation;
    private LocationAdapter adapter;
    private MapFragment mapFragment;

    private List<Location> listLocations;

    public ChooseLocationFragment() {
        // Required empty public constructor
    }

    public ChooseLocationFragment(MapFragment mapFragment, List<Location> listLocations) {
        this.mapFragment = mapFragment;

        // order
        Collections.sort(listLocations, (o1, o2) -> {
            int c = o1.getFloorId().toLowerCase().compareTo(o2.getFloorId().toLowerCase());
            if (c == 0) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            } else {
                return c;
            }
        });
        this.listLocations = listLocations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_location, container, false);

        initView(view);

        //setup rv
        adapter = new LocationAdapter(this);

        rvLocation.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvLocation.setAdapter(adapter);

        adapter.setListLocations(listLocations);


        bgScan.setOnClickListener(v -> {
            mapFragment.setupCameraPreview();

            this.getActivity().onBackPressed();
        });

        imgBack.setOnClickListener(v -> {

            getActivity().onBackPressed();
        });

        // setup edit text
        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    adapter.setListLocations(listLocations);
                } else {
                    List<Location> listTmp = new ArrayList<>();

                    for (Location location : listLocations) {
                        if (location.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                            listTmp.add(location);
                        }
                    }

                    adapter.setListLocations(listTmp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        return view;
    }

    public void setLocation(Location location) {
        mapFragment.setStartLocation(location);

        this.getActivity().onBackPressed();
    }

    private void initView(View view) {
        imgBack = view.findViewById(R.id.imgBack);
        edtInput = view.findViewById(R.id.edtInput);
        bgScan = view.findViewById(R.id.bgScan);
        rvLocation = view.findViewById(R.id.rvLocation);
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtInput.getWindowToken(), 0);
    }
}
