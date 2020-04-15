package fpt.capstone.inqr.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.RoomAdapter;
import fpt.capstone.inqr.callbacks.BottomSheetRoomListener;
import fpt.capstone.inqr.callbacks.RoomTappedListener;
import fpt.capstone.inqr.model.Room;

/**
 * Demo4
 * Created by TinhPV on 4/14/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class ChangeDestinationBottomSheetFragment extends BottomSheetDialogFragment implements RoomTappedListener {

    private RoomAdapter mRoomAdapter;
    private BottomSheetRoomListener mRoomListener;
    private List<Room> mRoomList;
    private SearchView mSearchView;

    public ChangeDestinationBottomSheetFragment(BottomSheetRoomListener roomListener, List<Room> roomList) {
        mRoomListener = roomListener;
        mRoomList = roomList;
    }

    public List<Room> getRoomList() {
        return mRoomList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.choose_destination_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView rvRoom = view.findViewById(R.id.rv_room);
        rvRoom.setLayoutManager(new LinearLayoutManager(getContext()));
        mRoomAdapter = new RoomAdapter(getContext(), mRoomList, this);
        rvRoom.setAdapter(mRoomAdapter);

        mSearchView = view.findViewById(R.id.sv_room);
        mSearchView.clearFocus();
        mSearchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        mSearchView.setOnClickListener(v -> mSearchView.setIconified(false));
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mRoomAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public void onRoomTapped(String roomName) {
        mRoomListener.roomChoosen(roomName);
        dismiss();
    }
}
