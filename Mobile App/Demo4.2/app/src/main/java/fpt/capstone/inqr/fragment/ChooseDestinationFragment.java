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

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.DestinationAdapter;
import fpt.capstone.inqr.helper.PreferenceHelper;
import fpt.capstone.inqr.model.Room;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseDestinationFragment extends BaseFragment {

    private EditText edtInput;
    private ImageView imgBack;
    private RecyclerView rvLoveDestination, rvDestination;
    private DestinationAdapter bigAdapter;

    private MapFragment mapFragment;
    private List<Room> listRooms;


    public ChooseDestinationFragment() {
        // Required empty public constructor
    }

    public ChooseDestinationFragment(MapFragment mapFragment, List<Room> listRooms, String buildingId) {
        this.mapFragment = mapFragment;

        // filter duplicate
        listRooms = listRooms.stream()
                .collect(collectingAndThen(toCollection(() -> new TreeSet<>(Comparator.comparing(Room::getName))),
                        ArrayList::new));

        // add count for special room
        for (Room room : listRooms) {
            if (room.isSpecialRoom()) {
                int count = PreferenceHelper.getInt(mapFragment.getContext(), buildingId + "_" + room.getName().toLowerCase());
                room.setCounter(count);
            }
        }

        // sort
        Collections.sort(listRooms, (o1, o2) -> {
            int c = o2.getCounter() - o1.getCounter();
            if (c == 0) {
                c = o1.getFloorId().toLowerCase().compareTo(o2.getFloorId().toLowerCase());
                if (c == 0) {
                    return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                } else {
                    return c;
                }
            } else {
                return c;
            }
        });

        List<Room> listTop3 = new ArrayList<>();
        List<Room> listOutTop3 = new ArrayList<>();

        for (int i = 0; i < listRooms.size(); i++) {
            if (i < 3) {
                listTop3.add(listRooms.get(i));
            } else {
                listOutTop3.add(listRooms.get(i));
            }
        }

        Collections.sort(listOutTop3, (o1, o2) -> {
            int c = o1.getFloorId().toLowerCase().compareTo(o2.getFloorId().toLowerCase());
            if (c == 0) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            } else {
                return c;
            }
        });

        if (this.listRooms == null) {
            this.listRooms = new ArrayList<>();
        } else {
            this.listRooms.clear();
        }

        this.listRooms.addAll(listTop3);
        this.listRooms.addAll(listOutTop3);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_destination, container, false);

        innitView(view);

        // rv
        bigAdapter = new DestinationAdapter(this);

        rvDestination.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        rvDestination.setAdapter(bigAdapter);
        bigAdapter.setListRooms(listRooms);

        edtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    bigAdapter.setListRooms(listRooms);
                } else {
                    List<Room> listTmp = new ArrayList<>();

                    for (Room room : listRooms) {
                        if (room.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                            listTmp.add(room);
                        }
                    }

                    bigAdapter.setListRooms(listTmp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgBack.setOnClickListener(v -> this.getActivity().onBackPressed());

        return view;
    }

    public void setDestination(Room room) {
        mapFragment.setDestination(room);

        this.getActivity().onBackPressed();
    }

    private void innitView(View view) {
        edtInput = view.findViewById(R.id.edtInput);
        imgBack = view.findViewById(R.id.imgBack);
        rvDestination = view.findViewById(R.id.rvDestination);
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edtInput.getWindowToken(), 0);
    }
}
