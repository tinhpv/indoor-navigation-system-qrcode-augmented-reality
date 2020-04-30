package fpt.capstone.inqr.presenter;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fpt.capstone.inqr.helper.DatabaseHelper;
import fpt.capstone.inqr.model.Floor;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.view.MapView;

/**
 * Demo4
 * Created by TinhPV on 2020-04-25
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class MapPresenter {

    private MapView mMapView;
    private Context mContext;
    private DatabaseHelper mDatabaseHelper;

    public MapPresenter(MapView mapView, Context context) {
        mMapView = mapView;
        mContext = context;
        mDatabaseHelper = new DatabaseHelper(context);
    }

    public void updateRoomCounter(String buildingId, String roomId, int count) {
        mDatabaseHelper.updateRoomCounter(roomId, count);

        // GET FLOORS
        List<Floor> floorList = mDatabaseHelper.getAllFloors(buildingId);

        // GET LOCATIONS OF FLOORS
        List<Location> locationList = new ArrayList<>();
        List<String> floorNames = new ArrayList<>();
        for (Floor floor : floorList) {
            floorNames.add(floor.getName());
            locationList.addAll(mDatabaseHelper.getAllLocations(floor.getId()));
        } // end for
        // GET ROOMS
        List<Room> roomList = new ArrayList<>();
        for (Location location : locationList) {
            roomList.addAll(mDatabaseHelper.getAllRooms(location.getId()));
        }

        mMapView.onLoadRoomData(roomList);
    }

    public void updateFavoriteRoom(String buildingId, String roomId, int status) {
        mDatabaseHelper.updateFavoriteRoom(roomId, status);

        // GET FLOORS
        List<Floor> floorList = mDatabaseHelper.getAllFloors(buildingId);

        // GET LOCATIONS OF FLOORS
        List<Location> locationList = new ArrayList<>();
        List<String> floorNames = new ArrayList<>();
        for (Floor floor : floorList) {
            floorNames.add(floor.getName());
            locationList.addAll(mDatabaseHelper.getAllLocations(floor.getId()));
        } // end for
        // GET ROOMS
        List<Room> roomList = new ArrayList<>();
        for (Location location : locationList) {
            roomList.addAll(mDatabaseHelper.getAllRooms(location.getId()));
        }

        mMapView.onLoadRoomData(roomList);
    }

    public void loadBuildingData(String buildingId) {

        // GET FLOORS
        List<Floor> floorList = mDatabaseHelper.getAllFloors(buildingId);

        // GET LOCATIONS OF FLOORS
        List<Location> locationList = new ArrayList<>();
        List<String> floorNames = new ArrayList<>();
        for (Floor floor : floorList) {
            floorNames.add(floor.getName());
            locationList.addAll(mDatabaseHelper.getAllLocations(floor.getId()));
        } // end for

        // EXTRACT TO GET LOCATION'S NAME
        List<String> locationNameList = new ArrayList<>();
        for (Location location : locationList) {
            locationNameList.add(location.getName());
        }
        Collections.sort(locationNameList);

        // GET ROOMS
        List<Room> roomList = new ArrayList<>();
        for (Location location : locationList) {
            roomList.addAll(mDatabaseHelper.getAllRooms(location.getId()));
        }

        // EXTRACT TO GET ROOM'S NAME
        List<String> roomNameList = new ArrayList<>();
        for (Room room : roomList) {
            roomNameList.add(room.getName());
        }
        Collections.sort(roomNameList);

        mMapView.onSuccessLoadBuildingData(floorList, locationList, roomList, floorNames, locationNameList, roomNameList);
    }


}
