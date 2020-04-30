package fpt.capstone.inqr.view;

import java.util.List;

import fpt.capstone.inqr.model.Floor;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Room;

/**
 * Demo4
 * Created by TinhPV on 2020-04-25
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public interface MapView {
    void onSuccessLoadBuildingData(List<Floor> floors, List<Location> locations, List<Room> rooms,
                                   List<String> floorNames, List<String> locationNames, List<String> roomNames);

    void onLoadRoomData(List<Room> listRooms);
}
