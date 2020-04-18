package fpt.capstone.inqr.helper;

import java.util.ArrayList;
import java.util.List;

import fpt.capstone.inqr.dijkstra.DijkstraShortestPath;
import fpt.capstone.inqr.dijkstra.Edge;
import fpt.capstone.inqr.dijkstra.Vertex;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Room;

/**
 * Demo4
 * Created by TinhPV on 4/12/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class Wayfinder {

    private List<Vertex> vertexList;
    private List<Location> locationList;
    private List<Room> roomList;
    private List<Room> specialRoomList;
    private List<String> listLocationName;
    private List<String> listRoomName;
    private DijkstraShortestPath shortestPath;
    private Room endRoom;
    private double currentShortestDistance;
    private List<Vertex> shortestPathList;


    public Wayfinder(List<Location> locationList, List<Room> roomList) {
        this.locationList = locationList;
        this.roomList = roomList;
        currentShortestDistance = 0.0;
        shortestPathList = null;
    }

    public void findWay(String startLocationId, String roomName) {
        shortestPathList = new ArrayList<>();

        if (shortestPath == null) {
            shortestPath = new DijkstraShortestPath();
        }

        prepareData();

        String endLocationId = getLocationIdOfRoom(roomName);
        Vertex endPoint = getVertexInList(endLocationId);

        double shortestDistance = 0.0;

        if (getRoom(roomName).isSpecialRoom()) {
            specialRoomList = getListSpecialRoom(roomName);
            for (int i = 0; i < specialRoomList.size(); i++) {
                Room specialRoom = specialRoomList.get(i);
                if (startLocationId.equals(specialRoom.getLocationId())) {
                    shortestPathList = new ArrayList<>();
                    shortestPathList.add(getVertexInList(specialRoom.getLocationId()));
                    endRoom = specialRoom;
                } else {
                    shortestPath.computeShortestPaths(getVertexInList(startLocationId));
                    double tempShortestDistance = getVertexInList(specialRoom.getLocationId()).getDistance();
                    List<Vertex> pathPointList = shortestPath.getShortestsPathTo(getVertexInList(specialRoom.getLocationId()));

                    if (0.0 == shortestDistance) {
                        shortestPathList = pathPointList;
                        endRoom = specialRoom;
                        shortestDistance = tempShortestDistance;
                    } else {
                        shortestPathList = pathPointList;
                        endRoom = specialRoom;
                        shortestDistance = tempShortestDistance;
                    }

                    prepareData();
                } // end if
            } // end for special room list
            currentShortestDistance = shortestDistance;

        } else {
            endRoom = getRoom(roomName);

            if (startLocationId.equals(endRoom.getLocationId())) {
                shortestPathList = new ArrayList<>();
                shortestPathList.add(getVertexInList(endRoom.getLocationId()));
                currentShortestDistance = 0.0;
            } else {
                shortestPath.computeShortestPaths(getVertexInList(startLocationId));
                shortestPathList = shortestPath.getShortestsPathTo(endPoint);
                currentShortestDistance = endPoint.getDistance();
            }
        } // end if special room
    } // end method


    private void prepareData() {

        if (vertexList == null) {
            vertexList = new ArrayList<>();
        } else {
            vertexList.clear();
        }

        // construct vertexList
        for (int i = 0; i < locationList.size(); i++) {
            Vertex vertex = new Vertex(locationList.get(i).getId(), locationList.get(i).getName());
            vertexList.add(vertex);
        }

        for (int i = 0; i < locationList.size(); i++) {
            for (int j = 0; j < locationList.get(i).getNeighborList().size(); j++) {
                int index = getIndexOfLocation(locationList.get(i).getNeighborList().get(j).getId());
                vertexList.get(i).addNeighbour(new Edge(locationList.get(i).getNeighborList().get(j).getDistance(), vertexList.get(i), vertexList.get(index)));
            } // end for
        } // end for
    }


    private int getIndexOfLocation(String id) {
        for (int i = 0; i < locationList.size(); i++) {
            if (id.equals(locationList.get(i).getId())) {
                return i;
            }
        } // end for
        return -1;
    }

    private Room getRoom(String name) {
        for (Room room : roomList) {
            if (name.equals(room.getName())) {
                return room;
            }
        }
        return null;
    }

    private List<Room> getListSpecialRoom(String roomName) {
        List<Room> listWC = new ArrayList<>();
        for (Room room : roomList) {
            if (roomName.equals(room.getName())) {
                listWC.add(room);
            }
        }
        return listWC;
    }

    private String getLocationIdOfRoom(String name) {
        if (name != null) {
            for (Room room : roomList) {
                if (name.toLowerCase().equals(room.getName().toLowerCase())) {
                    return room.getLocationId();
                }
            }
        }
        return null;
    }

    private Vertex getVertexInList(String id) {
        for (Vertex vertex : vertexList) {
            if (id.equals(vertex.getId())) {
                return vertex;
            }
        }
        return null;
    }

    public double getCurrentShortestDistance() {
        return currentShortestDistance;
    }

    public void setCurrentShortestDistance(double currentShortestDistance) {
        this.currentShortestDistance = currentShortestDistance;
    }

    public List<Vertex> getShortestPathList() {
        return shortestPathList;
    }

    public void setShortestPathList(List<Vertex> shortestPathList) {
        this.shortestPathList = shortestPathList;
    }
}
