package fpt.capstone.inqr.helper;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fpt.capstone.inqr.dijkstra.DijkstraShortestPath;
import fpt.capstone.inqr.dijkstra.Edge;
import fpt.capstone.inqr.dijkstra.Vertex;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.model.supportModel.Step;

/**
 * Demo4
 * Created by TinhPV on 4/12/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class Wayfinder {

    private List<Vertex> vertexList;
    private List<Location> locationList;
    private List<Location> locationPathList;
    private List<Room> roomList;
    private List<Room> specialRoomList;
    //    private List<String> listLocationName;
//    private List<String> listRoomName;
    private DijkstraShortestPath shortestPath;
    private Room endRoom;
    private double currentShortestDistance;
    private List<Vertex> shortestPathList;
    private List<Step> listStepGuide;


    public Wayfinder(List<Location> locationList, List<Room> roomList) {
        this.locationList = locationList;
        this.roomList = roomList;
        currentShortestDistance = 0.0;
        shortestPathList = null;
    }

    private boolean checkLocationHasNeighbor(String locationId) {
        for (Location location : locationList) {
            if (locationId.equals(location.getId())) {
                return location.getNeighborList().size() > 0;
            }
        }
        return false;
    }

    public void findWay(String startLocationId, String roomName) {
        shortestPathList = new ArrayList<>();

        if (shortestPath == null) {
            shortestPath = new DijkstraShortestPath();
        }

        prepareData();

        String endLocationId = getLocationIdOfRoom(roomName);
        Vertex endPoint = getVertexInList(endLocationId);

        if (!checkLocationHasNeighbor(startLocationId) || !checkLocationHasNeighbor(endLocationId)) {
            currentShortestDistance = -1;
        } else {
            double shortestDistance = 0.0;

            if (getRoom(roomName).isSpecialRoom()) {
                specialRoomList = getListSpecialRoom(roomName);
                for (int i = 0; i < specialRoomList.size(); i++) {
                    Room specialRoom = specialRoomList.get(i);
                    if (startLocationId.equals(specialRoom.getLocationId())) {
                        shortestPathList = new ArrayList<>();
                        shortestPathList.add(getVertexInList(specialRoom.getLocationId()));
                        endRoom = specialRoom;

                        break;
                    } else {
                        shortestPath.computeShortestPaths(getVertexInList(startLocationId));
                        double tempShortestDistance = getVertexInList(specialRoom.getLocationId()).getDistance();
                        List<Vertex> pathPointList = shortestPath.getShortestsPathTo(getVertexInList(specialRoom.getLocationId()));

                        if (0.0 == shortestDistance) {
                            shortestPathList = pathPointList;
                            endRoom = specialRoom;
                            shortestDistance = tempShortestDistance;
                        } else if (tempShortestDistance < shortestDistance) {
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

                    if (shortestPathList.size() == 1) {
                        currentShortestDistance = -1;

                        shortestPathList.clear();

                        shortestPathList.add(getVertexInList(startLocationId));
                        shortestPathList.add(getVertexInList(endRoom.getLocationId()));
                    }
                }
            } // end if special room


        }


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

                // check neighbor active or not
                // add only neighbor active
                if (locationList.get(i).getNeighborList().get(j).isActive()) {
                    int index = getIndexOfLocation(locationList.get(i).getNeighborList().get(j).getId());
                    vertexList.get(i).addNeighbour(new Edge(locationList.get(i).getNeighborList().get(j).getDistance(), vertexList.get(i), vertexList.get(index)));
                }


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

    public Room getRoom(String name) {
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

    private Location getLocation(String id) {
        for (int i = 0; i < locationList.size(); i++) {
            if (id.equals(locationList.get(i).getId())) {
                return locationList.get(i);
            }
        }

        return null;
    }

    public List<Location> getLocationPathList() {
        locationPathList = new ArrayList<>();
        for (Vertex vertex : shortestPathList) {
            locationPathList.add(getLocation(vertex.getId()));
        } // end
        return locationPathList;
    }

    private Neighbor getNeighbor(Location location, String neighborId) {
        for (Neighbor neighbor : location.getNeighborList()) {
            if (neighborId.equals(neighbor.getId())) {
                return neighbor;
            }
        }
        return null;
    }

    public List<Step> getListStepGuide() {
        /*
         * directionGuide map
         *   - key: location number on listPointOnWay
         *   - value: guide on direction in {left, right, turnleft, turnright, turnback, go forward}
         *
         * */
        Map<Integer, String> directionGuide = new HashMap<>();

        // CONSTRUCT DIRECTION_GUIDE MAP
        int step = 0;
        if (shortestPathList.size() == 2) {
            Location A = getLocation(shortestPathList.get(step).getId());
            String neighborID = shortestPathList.get(step + 1).getId();
            Neighbor neighborOfA = getNeighbor(A, neighborID);
            directionGuide.put(step, neighborOfA.getDirection());
        } else {
            while (step < shortestPathList.size() - 2) {
                int nextPointStep = step;

                // GET 3 CONTINUOUS LOCATIONS TO DETERMINE LOCATION
                Location A = getLocation(shortestPathList.get(nextPointStep).getId());
                String neighborID = shortestPathList.get(nextPointStep + 1).getId();
                Neighbor neighborOfA = getNeighbor(A, neighborID);

                Location B = getLocation(shortestPathList.get(nextPointStep + 1).getId());
                neighborID = shortestPathList.get(nextPointStep + 2).getId();
                Neighbor neighborOfB = getNeighbor(B, neighborID);

                Location C = getLocation(shortestPathList.get(nextPointStep + 2).getId());

                // There is no two continuous staircases == three standard locations
                if (!neighborOfA.getDirection().equals(Neighbor.ORIENT_DOWN) && !neighborOfA.getDirection().equals(Neighbor.ORIENT_UP) &&
                        !neighborOfB.getDirection().equals(Neighbor.ORIENT_DOWN) && !neighborOfB.getDirection().equals(Neighbor.ORIENT_UP)) {
                    if (directionGuide.get(step) == null)
                        directionGuide.put(step, neighborOfA.getDirection());
                    String direction = GeoHelper.getDirection(A, B, C, neighborOfB);
                    directionGuide.put(step + 1, direction);
                } else { // IN CASE, THERE'S 2 CONTINUOUS STAIRCASES >> CANNOT CALCULATE DIRECTION
                    directionGuide.put(step, neighborOfA.getDirection());
                    if (nextPointStep + 2 == shortestPathList.size() - 1) { // C is the destination
                        directionGuide.put(++step, neighborOfB.getDirection());
                    }
                } // end if

                step++;
            } // end while
        } // end if


        // THE LAST STEP IN DIRECTION GUIDE HAS VALUE OF DONE
        directionGuide.put(shortestPathList.size() - 1, Neighbor.ORIENT_NULL);

        // EXTRACT DIRECTION_GUIDE TO TEXT LIST
        if (listStepGuide == null) {
            listStepGuide = new ArrayList<>();
        } else {
            listStepGuide.clear();
        }


        if (directionGuide.size() > 1) { // ONLY EXTRACTING WHEN DESTINATION IS NOT A ROOM OF SOURCE LOCATION
            float distance = 0;
            String previousStep = Neighbor.ORIENT_NULL;
            Location previousLocation = null;

            for (int i = 0; i < directionGuide.size(); i++) {
                Location location = getLocation(shortestPathList.get(i).getId());
                Neighbor neighbor = null;

                // THE LAST STEP HAS NO NEIGHBOR => VALUE OF NULL
                if (i != directionGuide.size() - 1) {
                    String neighborID = shortestPathList.get(i + 1).getId();
                    neighbor = getNeighbor(location, neighborID);
                }

                String direction = directionGuide.get(i);

                // INITIALIZE THE VALUE OF PREVIOUS, IN CASE IT IS THE STEP 0
                if (previousStep.equals(Neighbor.ORIENT_NULL)) previousStep = direction;
                if (previousLocation == null) previousLocation = location;

                // IF THE CURRENT STEP IS NOT THE SAME AS SOME PREVIOUS STEPS >> DETECT THE DIFFERENCE
                if (!direction.equals(previousStep)) {
                    switch (previousStep) {
                        case Neighbor.ORIENT_LEFT:
                            listStepGuide.add(new Step(Step.TYPE_GO_STRAIGHT, "At " + previousLocation.getName() + ", go straight to the left", distance + "m"));
                            break;
                        case Neighbor.ORIENT_RIGHT:
                            listStepGuide.add(new Step(Step.TYPE_GO_STRAIGHT, "At " + previousLocation.getName() + ", go straight to the right", distance + "m"));
                            break;
                        case Neighbor.ORIENT_TURN_LEFT:
                            listStepGuide.add(new Step(Step.TYPE_TURN_LEFT, "Turn left at " + previousLocation.getName() + ", go straight.", distance + "m"));
                            break;
                        case Neighbor.ORIENT_TURN_RIGHT:
                            listStepGuide.add(new Step(Step.TYPE_TURN_RIGHT, "Turn right at " + previousLocation.getName() + ", go straight.", distance + "m"));
                            break;
                        case Neighbor.ORIENT_UP:
                            listStepGuide.add(new Step(Step.TYPE_UP_STAIR, "Go upstair at " + previousLocation.getName(), null));
                            break;
                        case Neighbor.ORIENT_DOWN:
                            listStepGuide.add(new Step(Step.TYPE_DOWN_STAIR, "Go downstairs at " + previousLocation.getName(), null));
                            break;
                        case Neighbor.ORIENT_BACKWARD:
                            listStepGuide.add(new Step(Step.TYPE_TURN_BACK, "Go straight in the opposite direction of  " + previousLocation.getName(), null));
                            break;
                        case Neighbor.ORIENT_FORWARD:
                            listStepGuide.add(new Step(Step.TYPE_GO_FORWARD, "Keep going straight from " + previousLocation.getName(), null));
                    } // end switch

                    // THE LAST STEP HAS NO NEIGHBOR > VALUE OF NULL
                    if (i != directionGuide.size() - 1) distance = neighbor.getDistance();
                    previousLocation = location;
                } else {
                    distance += neighbor.getDistance();
                } // end comparison with previous step

                previousStep = direction;
            } // end for
        } // end extracting direction guide

        return listStepGuide;
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

    public Room getEndRoom() {
        return endRoom;
    }

}
