package fpt.capstone.inqr.presenter;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fpt.capstone.inqr.callbacks.CallbackData;
import fpt.capstone.inqr.helper.AppHelper;
import fpt.capstone.inqr.helper.DatabaseHelper;
import fpt.capstone.inqr.model.Building;
import fpt.capstone.inqr.model.Company;
import fpt.capstone.inqr.model.Floor;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.view.ListBuildingView;

/**
 * Demo4
 * Created by TinhPV on 2020-04-25
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class ListBuildingPresenter {
    private ListBuildingView mBuildingView;
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;
    private AppHelper appHelper;


    public ListBuildingPresenter(ListBuildingView buildingView, Context context) {
        mBuildingView = buildingView;
        mContext = context;
        mDatabaseHelper = new DatabaseHelper(mContext);
        appHelper = AppHelper.getInstance(mContext);
    }

    public void getAllBuildingsFromServer() {
        appHelper.getAllBuilding(new CallbackData<List<Company>>() {
            @Override
            public void onSuccess(List<Company> companies) {
                mBuildingView.onSuccessGetAllBuildingsServer(companies);
            }

            @Override
            public void onFail(String message) {
                mBuildingView.onFailGetBuildingsFromServer();
            }
        });
    }

    public void getAllLocations(String buildingID) {
        appHelper.getAllLocation(buildingID, new CallbackData<Building>() {
            @Override
            public void onSuccess(Building building) {
                mBuildingView.onSuccessGetAllLocationsServer(building);
            }

            @Override
            public void onFail(String message) {
                mBuildingView.onFailGetLocationsFromServer();
            }
        });
    }


    private boolean checkExistedDb(Context context) {
        File dbFile = context.getDatabasePath("Capstone");
        return dbFile.exists();
    }

    public void addFloor(Floor floor, String buildingId) {
        mDatabaseHelper.addFloor(floor, buildingId);
    }

    public void addNeighbor(String locationId, Neighbor neighbor) {
        mDatabaseHelper.addNeighbor(locationId, neighbor);
    }

    public void addRoom(Room room, String locationId, String floorId) {
        mDatabaseHelper.addRoom(room, locationId, floorId);
    }

    public void addLocation(Location location, String floorID) {
        mDatabaseHelper.addLocation(location, floorID);
    }

    public void deleteBuildingData(String buildingId) {
        mDatabaseHelper.deleteBuildingData(mContext, buildingId);
    }

    public void updateBuildingStatus(String buildingId, int status) {
        mDatabaseHelper.updateBuildingStatus(buildingId, status);
    }

    public void closeDB() {
        mDatabaseHelper.closeDB();
    }

    public void getBuildings() {
        List<Building> buildings = new ArrayList<>();
        if (checkExistedDb(mContext)) {
            buildings = mDatabaseHelper.getAllBuildings();
            if (buildings != null) {
                if (buildings.size() > 1) {
                    Collections.sort(buildings, (o1, o2) -> {
                        int c = o1.getCompanyName().toLowerCase().compareTo(o2.getCompanyName().toLowerCase());
                        if (c == 0) {
                            c = o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
                        }
                        return c;
                    });
                }
            }
        }

        mBuildingView.onSuccessLoadBuildings(buildings);
    }
}
