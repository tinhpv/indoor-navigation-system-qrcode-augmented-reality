package fpt.capstone.inqr.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import fpt.capstone.inqr.model.Building;
import fpt.capstone.inqr.model.Floor;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;
import fpt.capstone.inqr.model.Room;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    //Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    //DB version
    private static final int DATABASE_VERSION = 10;

    //DB name
    private static final String DATABASE_NAME = "Capstone";

    //Table names
    private static final String TABLE_BUILDING = "Buildings";
    private static final String TABLE_FLOOR = "Floors";
    private static final String TABLE_LOCATION = "Locations";
    private static final String TABLE_NEIGHBOR = "Neighbors";
    private static final String TABLE_ROOM = "Rooms";

    //Common column names
    private static final String ID = "id";
    private static final String NAME = "name";

    //Locations Table
    private static final String RATIO_X = "ratioX";
    private static final String RATIO_Y = "ratioY";
    private static final String LINK_QR_CODE = "linkQr";
    private static final String QR_ANCHOR_ID = "qrAnchorId";
    private static final String SPACE_ANCHOR_ID = "spaceAnchorId";
//    private static final String FLOOR_ID = "floorId";

    //Neighbor Table
    private static final String LOCATION_ID = "locationId";
    private static final String NEIGHBOR_ID = "neighborId";
    private static final String ORIENTATION = "orientation";
    private static final String DISTANCE = "distance";

    //Floor Table
    private static final String FLOOR_ID = "floorId";
    private static final String BUILDING_ID = "buildingId";

    //Room Table
    private static final String SPECIAL_ROOM = "specialRoom";

    // Building Table
    private static final String VERSION = "version";
    private static final String DAY_EXPIRED = "dayExpired";
    private static final String STATUS = "status";
    private static final String COMPANY_NAME = "companyName";
    private static final String DESCRIPTION = "description";


    // Building table create statement
    private static final String CREATE_TABLE_BUILDING = "CREATE TABLE " + TABLE_BUILDING
            + "("
            + ID + " TEXT PRIMARY KEY, "
            + NAME + " TEXT,"
            + DESCRIPTION + " TEXT,"
            + COMPANY_NAME + " TEXT,"
            + VERSION + " INTEGER,"
            + DAY_EXPIRED + " TEXT,"
            + STATUS + " INTEGER"
            + ")";

    // Floor table create statement
    private static final String CREATE_TABLE_FLOOR = "CREATE TABLE " + TABLE_FLOOR
            + "("
            + ID + " TEXT PRIMARY KEY, "
            + NAME + " TEXT,"
            + BUILDING_ID + " TEXT"
            + ")";

    //Location Table Create statement
    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE " + TABLE_LOCATION
            + "("
            + ID + " TEXT PRIMARY KEY, "
            + NAME + " TEXT,"
            + RATIO_X + " FLOAT,"
            + RATIO_Y + " FLOAT,"
            + FLOOR_ID + " TEXT, "
            + LINK_QR_CODE + " TEXT, "
            + QR_ANCHOR_ID + " TEXT, "
            + SPACE_ANCHOR_ID + " TEXT"
            + ")";

    //Neighbor Table Create statement
    private static final String CREATE_TABLE_NEIGHBOR = "CREATE TABLE " + TABLE_NEIGHBOR
            + "("
            + ID + " INTEGER PRIMARY KEY, "
            + LOCATION_ID + " TEXT, "
            + NEIGHBOR_ID + " TEXT, "
            + ORIENTATION + " TEXT,"
            + DISTANCE + " FLOAT"
            + ")";

    //Room Table create statement
    private static final String CREATE_TABLE_ROOM = "CREATE TABLE " + TABLE_ROOM
            + "("
            + ID + " TEXT PRIMARY KEY, "
            + NAME + " TEXT, "
            + RATIO_X + " FLOAT, "
            + RATIO_Y + " FLOAT, "
            + LOCATION_ID + " TEXT, "
            + FLOOR_ID + " TEXT, "
            + SPECIAL_ROOM + " INTEGER, "
            + SPACE_ANCHOR_ID + " TEXT"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create tables
        db.execSQL(CREATE_TABLE_BUILDING);
        db.execSQL(CREATE_TABLE_FLOOR);
        db.execSQL(CREATE_TABLE_LOCATION);
        db.execSQL(CREATE_TABLE_NEIGHBOR);
        db.execSQL(CREATE_TABLE_ROOM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NEIGHBOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOOR);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BUILDING);

        //create new tables
        onCreate(db);
    }

    // Insert Building
    public long addBuilding(Building building) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, building.getId());
        values.put(NAME, building.getName());
        values.put(DESCRIPTION, building.getDescription());
        values.put(COMPANY_NAME, building.getCompanyName());
        values.put(DAY_EXPIRED, building.getDayExpired());
        values.put(VERSION, building.getVersion());
        values.put(STATUS, Building.NOT_DOWNLOAD);


        //Insert row
        long building_id = db.insert(TABLE_BUILDING, null, values);
        db.close();
        return building_id;
    }

    // update data building + status
    public long updateBuilding(Building building) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, building.getName());
        values.put(DESCRIPTION, building.getDescription());
        values.put(COMPANY_NAME, building.getCompanyName());
        values.put(DAY_EXPIRED, building.getDayExpired());
        values.put(VERSION, building.getVersion());
        values.put(STATUS, Building.UPDATE);

        //update row
        long building_id = db.update(TABLE_BUILDING, values, ID + " = ?", new String[]{building.getId()});
        db.close();
        return building_id;
    }

    // update data building information
    public long updateBuildingInformation(Building building) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(NAME, building.getName());
        values.put(DESCRIPTION, building.getDescription());
        values.put(COMPANY_NAME, building.getCompanyName());
        values.put(DAY_EXPIRED, building.getDayExpired());
//        values.put(VERSION, building.getVersion());
//        values.put(STATUS, Building.UPDATE);

        //update row
        long building_id = db.update(TABLE_BUILDING, values, ID + " = ?", new String[]{building.getId()});
        db.close();
        return building_id;
    }

    // update data building
    public long updateBuildingStatus(String buildingId, int status) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(STATUS, status);

        //update row
        long building_id = db.update(TABLE_BUILDING, values, ID + " = ?", new String[]{buildingId});
        db.close();
        return building_id;
    }


    //Insert Location
    public long addLocation(Location location, String floorId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, location.getId());
        values.put(NAME, location.getName());
        values.put(RATIO_X, location.getRatioX());
        values.put(RATIO_Y, location.getRatioY());
        values.put(FLOOR_ID, floorId);
        values.put(LINK_QR_CODE, location.getLinkQr());
        values.put(QR_ANCHOR_ID, location.getQrAnchorId());
        values.put(SPACE_ANCHOR_ID, location.getSpaceAnchorId());


        //Insert row
        long floor_id = db.insert(TABLE_LOCATION, null, values);
        db.close();
        return floor_id;
    }

    // Insert Room
    public long addRoom(Room room, String locationId, String floorId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, room.getId());
        values.put(NAME, room.getName());
        values.put(RATIO_X, room.getRatioX());
        values.put(RATIO_Y, room.getRatioY());
        values.put(LOCATION_ID, locationId);
        values.put(FLOOR_ID, floorId);
        if (room.isSpecialRoom()) {
            values.put(SPECIAL_ROOM, 1);
        } else {
            values.put(SPECIAL_ROOM, 0);
        }
        values.put(SPACE_ANCHOR_ID, room.getSpaceAnchorId());


        //Insert row
        long room_id = db.insert(TABLE_ROOM, null, values);
        db.close();
        return room_id;
    }

    // Insert floor
    public long addFloor(Floor floor, String buildingId) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ID, floor.getId());
        values.put(NAME, floor.getName());
        values.put(BUILDING_ID, buildingId);

        //Insert row
        long location_id = db.insert(TABLE_FLOOR, null, values);
        db.close();
        return location_id;
    }

    //Get total floors
    public int getTotalFloors() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM Floors";
        Cursor c = db.rawQuery(selectQuery, null);

        return c.getCount();
    }

    //Insert Neighbor
    public long addNeighbor(String locationId, Neighbor neighbor) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LOCATION_ID, locationId);
        values.put(NEIGHBOR_ID, neighbor.getId());
        values.put(ORIENTATION, neighbor.getDirection());
        values.put(DISTANCE, neighbor.getDistance());

        //Insert row
        long neighbor_id = db.insert(TABLE_NEIGHBOR, null, values);
        db.close();
        return neighbor_id;
    }

    //Fetching all Locations
//    public List<Location> getAllLocations() {
//        List<Location> locations = new ArrayList<>();
//        String selectQuery = "SELECT * FROM " + TABLE_LOCATION;
//
//        Log.e(LOG, selectQuery);
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        //looping through all rows and add to list
//        if (c.moveToFirst()) {
//            do {
//                Location location = new Location();
//                location.setId(c.getString((c.getColumnIndex(ID))));
//                location.setName(c.getString(c.getColumnIndex(NAME)).trim());
//                location.setRatioX(c.getFloat(c.getColumnIndex(RATIO_X)));
//                location.setRatioY(c.getFloat(c.getColumnIndex(RATIO_Y)));
//                location.setFloorId(c.getString(c.getColumnIndex(FLOOR_ID)));
//
//                //add to location list
//                locations.add(location);
//
//                List<Neighbor> list = this.getLocationNeighbors(c.getString((c.getColumnIndex(ID))));
//                location.setNeighborList(list);
//            } while (c.moveToNext());
//        }
//
//
//        return locations;
//    }

    public List<Location> getAllLocations(String floorId) {
        List<Location> locations = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_LOCATION + " WHERE " + FLOOR_ID + " = '" + floorId + "'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows and add to list
        if (c.moveToFirst()) {
            do {
                Location location = new Location();
                location.setId(c.getString((c.getColumnIndex(ID))));
                location.setName(c.getString(c.getColumnIndex(NAME)).trim());
                location.setRatioX(c.getFloat(c.getColumnIndex(RATIO_X)));
                location.setRatioY(c.getFloat(c.getColumnIndex(RATIO_Y)));
                location.setFloorId(c.getString(c.getColumnIndex(FLOOR_ID)));
                location.setQrAnchorId(c.getString(c.getColumnIndex(QR_ANCHOR_ID)));
                location.setSpaceAnchorId(c.getString(c.getColumnIndex(SPACE_ANCHOR_ID)));
                //add to location list
                locations.add(location);

                List<Neighbor> list = this.getLocationNeighbors(c.getString((c.getColumnIndex(ID))));
                location.setNeighborList(list);
            } while (c.moveToNext());
        }


        return locations;
    }


    //Get all neighbor base on location id
    public List<Neighbor> getLocationNeighbors(String locationId) {
        List<Neighbor> neighbors = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NEIGHBOR + " WHERE " + LOCATION_ID + " = '" + locationId + "'";

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        //looping through all rows and add to list
        if (c.moveToFirst()) {
            do {
                Neighbor neighbor = new Neighbor();
                neighbor.setId(c.getString(c.getColumnIndex(NEIGHBOR_ID)));
//                neighbor.setLocationId(c.getInt(c.getColumnIndex(LOCATION_ID)));
                neighbor.setDistance(c.getFloat(c.getColumnIndex(DISTANCE)));
                neighbor.setDirection(c.getString(c.getColumnIndex(ORIENTATION)));

                //add to location list
                neighbors.add(neighbor);
            } while (c.moveToNext());
        }
        return neighbors;
    }

//    public List<Room> getAllRooms() {
//        List<Room> listRoom = new ArrayList<>();
//        String selectQuery = "SELECT * FROM " + TABLE_ROOM;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        if (c.moveToFirst()) {
//            do {
//                Room room = new Room();
//                room.setId(c.getString(c.getColumnIndex(ID)));
//                room.setName(c.getString(c.getColumnIndex(NAME)).trim());
//                room.setRatioX(c.getFloat(c.getColumnIndex(RATIO_X)));
//                room.setRatioY(c.getFloat(c.getColumnIndex(RATIO_Y)));
//                room.setLocationId(c.getString(c.getColumnIndex(LOCATION_ID)));
//                room.setFloorId(c.getString(c.getColumnIndex(FLOOR_ID)));
//
//                int tmp = c.getInt(c.getColumnIndex(SPECIAL_ROOM));
//                if (tmp == 1) {
//                    room.setSpecialRoom(true);
//                } else {
//                    room.setSpecialRoom(false);
//                }
//                listRoom.add(room);
//
//            } while (c.moveToNext());
//        }
//        return listRoom;
//    }

    public List<Room> getAllRooms(String locationId) {
        List<Room> listRoom = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ROOM + " WHERE " + LOCATION_ID + " = '" + locationId + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Room room = new Room();
                room.setId(c.getString(c.getColumnIndex(ID)));
                room.setName(c.getString(c.getColumnIndex(NAME)).trim());
                room.setRatioX(c.getFloat(c.getColumnIndex(RATIO_X)));
                room.setRatioY(c.getFloat(c.getColumnIndex(RATIO_Y)));
                room.setLocationId(c.getString(c.getColumnIndex(LOCATION_ID)));
                room.setFloorId(c.getString(c.getColumnIndex(FLOOR_ID)));
                room.setSpaceAnchorId(c.getString(c.getColumnIndex(SPACE_ANCHOR_ID)));

                int tmp = c.getInt(c.getColumnIndex(SPECIAL_ROOM));
                if (tmp == 1) {
                    room.setSpecialRoom(true);
                } else {
                    room.setSpecialRoom(false);
                }
                listRoom.add(room);

            } while (c.moveToNext());
        }
        return listRoom;
    }

    public List<Building> getAllBuildings() {
        List<Building> list = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_BUILDING;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Building building = new Building();
                building.setId(c.getString(c.getColumnIndex(ID)));
                building.setName(c.getString(c.getColumnIndex(NAME)).trim());
                building.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
                building.setCompanyName(c.getString(c.getColumnIndex(COMPANY_NAME)));
                building.setVersion(c.getInt(c.getColumnIndex(VERSION)));
                building.setDayExpired(c.getString(c.getColumnIndex(DAY_EXPIRED)));

                building.setStatus(c.getInt(c.getColumnIndex(STATUS)));
                list.add(building);

            } while (c.moveToNext());
        }
        return list;
    }

//    public Building getBuilding(String buildingId) {
//        String selectQuery = "SELECT * FROM " + TABLE_BUILDING + " WHERE " + ID + " = '" + buildingId + "'";
//        ;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        if (c.moveToFirst()) {
//            Building building = new Building();
//            building.setId(c.getString(c.getColumnIndex(ID)));
//            building.setName(c.getString(c.getColumnIndex(NAME)).trim());
//            building.setDescription(c.getString(c.getColumnIndex(DESCRIPTION)));
//            building.setCompanyName(c.getString(c.getColumnIndex(COMPANY_NAME)));
//            building.setVersion(c.getInt(c.getColumnIndex(VERSION)));
//            building.setDayExpired(c.getString(c.getColumnIndex(DAY_EXPIRED)));
//
//            building.setStatus(c.getInt(c.getColumnIndex(STATUS)));
//
//            return building;
//
//        }
//        return null;
//    }

//    public List<Floor> getAllFloors() {
//        List<Floor> listFloor = new ArrayList<>();
//        String selectQuery = "SELECT * FROM " + TABLE_FLOOR;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor c = db.rawQuery(selectQuery, null);
//
//        if (c.moveToFirst()) {
//            do {
//                Floor floor = new Floor();
//                floor.setId(c.getString(c.getColumnIndex(ID)));
//                floor.setName(c.getString(c.getColumnIndex(NAME)).trim());
//                floor.setBuildingId(c.getString(c.getColumnIndex(BUILDING_ID)));
//
//                listFloor.add(floor);
//
//            } while (c.moveToNext());
//        }
//        return listFloor;
//    }

    public String getFloorName(String floorId) {
        String selectQuery = "SELECT " + NAME + " FROM " + TABLE_FLOOR + " WHERE " + ID + " = '" + floorId + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {

            return c.getString(c.getColumnIndex(NAME)).trim();
        }
        return null;
    }

    public List<Floor> getAllFloors(String buildingId) {
        List<Floor> listFloor = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_FLOOR + " WHERE " + BUILDING_ID + " = '" + buildingId + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Floor floor = new Floor();
                floor.setId(c.getString(c.getColumnIndex(ID)));
                floor.setName(c.getString(c.getColumnIndex(NAME)).trim());
                floor.setBuildingId(c.getString(c.getColumnIndex(BUILDING_ID)));

                listFloor.add(floor);

            } while (c.moveToNext());
        }
        return listFloor;
    }

    private void deleteBuilding(String buildingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BUILDING, ID + " = ?", new String[]{buildingId});
        db.close();
    }

    private void deleteFloor(String buildingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_FLOOR, BUILDING_ID + " = ?", new String[]{buildingId});
        db.close();
    }

    private void deleteLocation(String floorId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_LOCATION, FLOOR_ID + " = ?", new String[]{floorId});
        db.close();
    }

    private void deleteNeighbor(String locationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NEIGHBOR, LOCATION_ID + " = ?", new String[]{locationId});
        db.close();
    }

    private void deleteRoom(String locationId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ROOM, LOCATION_ID + " = ?", new String[]{locationId});
        db.close();
    }

    public void deleteBuildingData(String buildingId) {
        List<Floor> listFloor = getAllFloors(buildingId);

        List<Location> listLocation = new ArrayList<>();

        for (Floor floor : listFloor) {
            listLocation.addAll(getAllLocations(floor.getId()));
        }


        // delete room + neighbor
        for (Location location : listLocation) {
            deleteNeighbor(location.getId());

            deleteRoom(location.getId());
        }

        // delete location
        for (Floor floor : listFloor) {
            deleteLocation(floor.getId());
        }

        // delete floor
        deleteFloor(buildingId);

        // update Building status
        updateBuildingStatus(buildingId, Building.NOT_DOWNLOAD);
    }

    public void deleteAllBuilding(String buildingId) {
       deleteBuildingData(buildingId);

       deleteBuilding(buildingId);
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }
}
