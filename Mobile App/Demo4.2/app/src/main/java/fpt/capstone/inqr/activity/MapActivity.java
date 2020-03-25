//package com.example.inqr.activity;
//
//import android.Manifest;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Paint;
//import android.graphics.Path;
//import android.graphics.PointF;
//import android.hardware.Sensor;
//import android.hardware.SensorEvent;
//import android.hardware.SensorEventListener;
//import android.hardware.SensorManager;
//import android.os.Bundle;
//import android.os.Handler;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.SurfaceView;
//import android.view.View;
//import android.view.animation.Animation;
//import android.view.animation.RotateAnimation;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.Button;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.inqr.R;
//import com.example.inqr.dijkstra.DijkstraShortestPath;
//import com.example.inqr.dijkstra.Edge;
//import com.example.inqr.dijkstra.Vertex;
//import com.example.inqr.helper.DatabaseHelper;
//import com.example.inqr.helper.FileHelper;
//import com.example.inqr.helper.VoiceHelper;
//import com.example.inqr.model.Floor;
//import com.example.inqr.model.Location;
//import com.example.inqr.model.Neighbor;
//import com.example.inqr.model.Room;
//import com.karumi.dexter.Dexter;
//import com.karumi.dexter.PermissionToken;
//import com.karumi.dexter.listener.PermissionDeniedResponse;
//import com.karumi.dexter.listener.PermissionGrantedResponse;
//import com.karumi.dexter.listener.PermissionRequest;
//import com.karumi.dexter.listener.single.PermissionListener;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import github.nisrulz.qreader.QREader;
//
//import static java.lang.Math.PI;
//import static java.lang.Math.atan2;
//import static java.lang.Math.cos;
//import static java.lang.Math.sin;
//
//public class MapActivity extends AppCompatActivity implements SensorEventListener {
//
//    private ImageView imgView;
//    private DatabaseHelper db;
//    private Bitmap mapImg;
//    private Canvas canvas;
//    private AutoCompleteTextView tvStart;
//    private AutoCompleteTextView tvEnd;
//    private Button btnFindWay;
//    private TextView tvFloor;
//    private List<Location> locationList;
//    private List<Room> listRoom;
//    private List<Room> listSpecialRoom;
//    private List<String> listLocationName;
//    private List<String> listRoomName;
//    private List<Vertex> vertexList;
//    private List<Vertex> listPointOnWay;
//    private List<Floor> listFloor;
//
//    private Room endRoom;
//
//    private String startLocationId = "";
//
//    //Image scale variables
//    int mode = NONE;
//    static final int NONE = 0;
//    static final int DRAG = 1;
//    static final int ZOOM = 2;
//    private SeekBar seekBar;
//
//
//    private String currentFloorId;
//
//
//    private DijkstraShortestPath shortestPath;
//
//
//    // xoay
//    private QREader qrEader;
//    private ImageView img;
//    private SensorManager mSensorManager;
//    private Sensor mRotation;
//    private float currentDegrees = 0f;
//
//    private FrameLayout bgImg;
//    private SurfaceView cameraView;
//    boolean hadQr = false;
//    private Handler checkQrExistHandler;
//    private Runnable runnable;
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);
//        Dexter.withActivity(this)
//                .withPermission(Manifest.permission.CAMERA)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        if (qrEader != null) {
//                            qrEader.initAndStart(cameraView);
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                    }
//                }).check();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mSensorManager.unregisterListener(this);
//        Dexter.withActivity(this)
//                .withPermission(Manifest.permission.CAMERA)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        if (qrEader != null) {
//                            qrEader.releaseAndCleanup();
//                        }
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                    }
//                }).check();
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_map);
//
//        db = new DatabaseHelper(getApplicationContext());
//        locationList = db.getAllLocations();
//        listLocationName = getListLocationName();
//        listFloor = db.getAllFloors();
////        commonLocations = db.getAllCommonLocations();
//        listRoom = db.getAllRooms();
//        listRoomName = getListRoomName();
//
//        imgView = findViewById(R.id.imgView);
//        tvStart = findViewById(R.id.tvStart);
//        tvEnd = findViewById(R.id.tvEnd);
//        btnFindWay = findViewById(R.id.btnFindWay);
//        tvFloor = findViewById(R.id.tvFloor);
//
////        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listLocationName);
//
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, 0);
//        for (String s : listLocationName) {
//            for (int i = 0; i < adapter.getCount(); i++) {
//                if (adapter.getItem(i).equals(s))
//                    adapter.remove(s);
//            }
//            adapter.add(s);
//        }
//        tvStart.setAdapter(adapter);
//
//        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, 0);
//        for (String s : listRoomName) {
//            for (int i = 0; i < adapter.getCount(); i++) {
//                if (adapter.getItem(i).equals(s))
//                    adapter.remove(s);
//            }
//            adapter.add(s);
//        }
//        tvEnd.setAdapter(adapter);
//        tvEnd.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                startLocationId = "";
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
//
//        //Scale
//        seekBar = findViewById(R.id.seekBar);
//        seekBar.setProgress(1);
//        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
//                float scale = ((i / 10.0f) + 1);
//                imgView.setScaleX(scale);
//                imgView.setScaleY(scale);
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//
//            }
//        });
//
////        imgView.setOnTouchListener(onTouchListener());
//
////        totalFloor = db.getTotalFloors();
//
////        db.closeDB();
//
//
//        img = findViewById(R.id.img);
//        bgImg = findViewById(R.id.bgImg);
//        cameraView = findViewById(R.id.cameraView);
//
//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//
//        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
//
//        Dexter.withActivity(this)
//                .withPermission(Manifest.permission.CAMERA)
//                .withListener(new PermissionListener() {
//                    @Override
//                    public void onPermissionGranted(PermissionGrantedResponse response) {
//                        setupCamera();
//                    }
//
//                    @Override
//                    public void onPermissionDenied(PermissionDeniedResponse response) {
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
//
//                    }
//                }).check();
//
////        img.setVisibility(View.INVISIBLE);
//
//        checkQrExistHandler = new Handler();
//        runnable = () -> {
//            if (hadQr) {
//                mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_GAME);
//                this.runOnUiThread(() -> bgImg.setVisibility(View.VISIBLE));
//            } else {
//                mSensorManager.unregisterListener(this);
//                this.runOnUiThread(() -> bgImg.setVisibility(View.INVISIBLE));
//            }
//
//            hadQr = false;
//            checkQrExistHandler.postDelayed(runnable, 100);
//        };
//        checkQrExistHandler.postDelayed(runnable, 100);
//
//
//        btnFindWay.setOnClickListener(v -> {
//                    startLocationId = getLocationId(tvStart.getText().toString());
//                    findWay(startLocationId);
//                }
//        );
//    }
//
//    private void setupCamera() {
//        qrEader = new QREader.Builder(this, cameraView, data -> {
//            hadQr = true;
//
//            tvFloor.post(() -> {
////                    btnFindWay.setText(data);
//
//                // ID: fpt_demo_l_1 - Name: 112
//                String tmp[] = data.split("\\|");
//
//                if (tmp.length > 1) {
//                    String id[] = tmp[0].trim().toLowerCase().split(":");
//                    if (id.length > 1) {
//                        if (!startLocationId.equals(id[1].trim())) {
//                            String name[] = tmp[1].trim().split(":");
//
//                            tvFloor.setText("Vị trí của bạn: " + name[1].trim());
//
//                            tvStart.setText(name[1].trim());
//
//
//                            startLocationId = id[1].trim();
//
//                            //tìm đường
//                            findWay(startLocationId);
//                        }
//                    }
//                }
//
//
//            });
//
//
//        }).facing(QREader.BACK_CAM)
//                .enableAutofocus(true)
////                .height(cameraView.getHeight())
////                .width(cameraView.getWidth())
//                .build();
//        qrEader.start();
//    }
//
//    private void updateOrientation(int orienId) {
//        switch (orienId) {
//            case 0:
//                img.setImageResource(R.drawable.like);
//                break;
//            case 1:
//                img.setImageResource(R.drawable.left);
//                break;
//            case 2:
//                img.setImageResource(R.drawable.right);
//                break;
//            case 3:
//                img.setImageResource(R.drawable.up);
//                break;
//            case 4:
//                img.setImageResource(R.drawable.down);
//                break;
//        }
//    }
//
//    private int getIndexOfFloor(String id) {
//
//        for (int i = 0; i < listFloor.size(); i++) {
//            if (id.equals(listFloor.get(i).getId())) {
//                return i;
//            }
//        }
//
//        return -1;
//    }
//
//    private List<Room> getListSpecialRoom(String roomName) {
//        List<Room> listWC = new ArrayList<>();
//        for (Room room : listRoom) {
//            if (roomName.equals(room.getName())) {
//                listWC.add(room);
//            }
//        }
//        return listWC;
//    }
//
//    private View.OnTouchListener onTouchListener() {
//        return new View.OnTouchListener() {
//            PointF DownPT = new PointF();
//            PointF StartPT = new PointF();
//
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
//                    case MotionEvent.ACTION_DOWN:
//                        mode = DRAG;
//                        DownPT.set(motionEvent.getX(), motionEvent.getY());
//                        StartPT.set(imgView.getX(), imgView.getY());
////                        System.out.println("Layout width: " + imgLayout.getWidth() + ", Layout height: " + imgLayout.getHeight());
////                        System.out.println("motionEvent.getX(): " + motionEvent.getX() + ", motionEvent.getY(): " + motionEvent.getY());
////                        System.out.println("imgView.getX(): " + imgView.getX() + ", imgView.getY(): " + imgView.getY());
//                        break;
//                    case MotionEvent.ACTION_POINTER_DOWN:
//                        mode = ZOOM;
//                        break;
//                    case MotionEvent.ACTION_POINTER_UP:
//                        mode = NONE;
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//                        if (mode == DRAG) {
//                            imgView.setX((int) (StartPT.x + motionEvent.getX() - DownPT.x));
//                            imgView.setY((int) (StartPT.y + motionEvent.getY() - DownPT.y));
//                            StartPT.set(imgView.getX(), imgView.getY());
//                        } else if (mode == ZOOM) {
//                        }
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        //Nothing to do
//                        mode = NONE;
//                        break;
//                    default:
//                        break;
//                }
//                return true;
//            }
//        };
//    }
//
//    private List<String> getListLocationName() {
//        List<String> listName = new ArrayList<>();
//
//        for (Location location : locationList) {
//            listName.add(location.getName());
//        }
//
//        Collections.sort(listName);
//
//        return listName;
//    }
//
//    private List<String> getListRoomName() {
//        List<String> listName = new ArrayList<>();
//
//        for (Room room : listRoom) {
//            listName.add(room.getName());
//        }
//
//        Collections.sort(listName);
//
//        return listName;
//    }
//
//    private String getLocationId(String name) {
//        if (name != null) {
//            for (Location location : locationList) {
//                if (name.toLowerCase().equals(location.getName().toLowerCase())) {
//                    return location.getId();
//                }
//            }
//        }
//        return null;
//    }
//
//    private String getLocationIdOfRoom(String name) {
//        if (name != null) {
//            for (Room room : listRoom) {
//                if (name.toLowerCase().equals(room.getName().toLowerCase())) {
//                    return room.getLocationId();
//                }
//            }
//        }
//        return null;
//    }
//
//    private int getIndexOfLocation(String id) {
//
//        for (int i = 0; i < locationList.size(); i++) {
//            if (id.equals(locationList.get(i).getId())) {
//                return i;
//            }
//        }
//
//        return -1;
//    }
//
//    private Location getLocation(String id) {
//        for (int i = 0; i < locationList.size(); i++) {
//            if (id.equals(locationList.get(i).getId())) {
//                return locationList.get(i);
//            }
//        }
//        return null;
//    }
//
//    private Room getRoom(String name) {
//        for (Room room : listRoom) {
//            if (name.equals(room.getName())) {
//                return room;
//            }
//        }
//        return null;
//    }
//
//    private void updateFloor(String floorId) {
//        currentFloorId = floorId;
////        tvFloor.setText(currentFloorId);
//        setImage(currentFloorId);
//        imgView.setImageBitmap(mapImg);
//    }
//
//    private void prepareData() {
//        if (vertexList == null) {
//            vertexList = new ArrayList<>();
//        } else {
//            vertexList.clear();
//        }
//
//        for (int i = 0; i < locationList.size(); i++) {
//            Vertex vertex = new Vertex(locationList.get(i).getId(), locationList.get(i).getName());
//            vertexList.add(vertex);
//        }
//
//        for (int i = 0; i < locationList.size(); i++) {
//            for (int j = 0; j < locationList.get(i).getNeighborList().size(); j++) {
//                int index = getIndexOfLocation(locationList.get(i).getNeighborList().get(j).getId());
//
//                vertexList.get(i).addNeighbour(new Edge(locationList.get(i).getNeighborList().get(j).getDistance(), vertexList.get(i), vertexList.get(index)));
//            }
//        }
//    }
//
//    public void findWay(String startLocationId) {
//
//
////        if (!isFinding) {
//        updateFloor(getLocation(startLocationId).getFloorId());
////        }
////        isFinding = true;
//
//        prepareData();
//
//
//        if (shortestPath == null) {
//            shortestPath = new DijkstraShortestPath();
//        }
//
//        // check room belong to which location
//        String locationId = getLocationIdOfRoom(tvEnd.getText().toString());
//        Vertex endPoint = getVertexInList(locationId);
//
//
//        // kiểm tra xem có phải special room không?
//        String roomName = tvEnd.getText().toString();
//        if (getRoom(roomName).isSpecialRoom()) {
//
//            //prepare list special room
//            listSpecialRoom = getListSpecialRoom(roomName);
//
//            double currentPath = 0.0;
////            String startId = getLocationId(tvStart.getText().toString());
//            for (int i = 0; i < listSpecialRoom.size(); i++) {
////                System.out.println("WC: " + commonLocations.get(i).get
//
//                // WC nằm chung vị trí với Start Point
//                if (listSpecialRoom.get(i).getLocationId().equals(startLocationId)) {
//                    listPointOnWay = new ArrayList<>();
//                    listPointOnWay.add(getVertexInList(listSpecialRoom.get(i).getLocationId()));
//                    endRoom = listSpecialRoom.get(i);
//                    break;
//                } else {
//                    shortestPath.computeShortestPaths(getVertexInList(startLocationId));
//                    double tmpPath = getVertexInList(listSpecialRoom.get(i).getLocationId()).getDistance();
//
//                    List<Vertex> listPoint = shortestPath.getShortestsPathTo(getVertexInList(listSpecialRoom.get(i).getLocationId()));
//
//                    if (currentPath == 0) {
//                        listPointOnWay = listPoint;
//
//                        endRoom = listSpecialRoom.get(i);
//
//                        currentPath = tmpPath;
//                    } else if (tmpPath < currentPath) {
//                        listPointOnWay = listPoint;
//
//                        endRoom = listSpecialRoom.get(i);
//
//                        currentPath = tmpPath;
//                    }
//
//
//                    prepareData();
//                }
//            }
//
//
//        } else {
//
//            // check room nằm chung vị trí với Start Point
//            endRoom = getRoom(tvEnd.getText().toString());
//
//            if (endRoom.getLocationId().equals(startLocationId)) {
//                listPointOnWay = new ArrayList<>();
//                listPointOnWay.add(getVertexInList(endRoom.getLocationId()));
//            } else {
//                shortestPath.computeShortestPaths(getVertexInList(startLocationId));
//                listPointOnWay = shortestPath.getShortestsPathTo(endPoint);
//            }
//
//
//        }
////        shortestPath.computeShortestPaths(getVertexInList(getLocationId(tvStart.getText().toString())));
////        listPointOnWay = shortestPath.getShortestsPathTo(getVertexInList(getLocationId(tvEnd.getText().toString())));
//
//        // update tọa độ của node cuối
//
//        Log.d("Path", listPointOnWay.toString());
//
//        // tìm hướng
//        if (listPointOnWay.size() > 1) {
//            Location neighbor = getLocation(listPointOnWay.get(1).getId());
//            Location location = getLocation(startLocationId);
//            updateOrientation(getOrientation(location, neighbor.getId()));
//        } else {
//            // đến nơi rồi
//            updateOrientation(0);
//        }
//
//
//        // refresh map
//        drawImage();
//
//    }
//
//    private int getOrientation(Location location, String neighborId) {
//        for (Neighbor neighbor : location.getNeighborList()) {
//            if (neighborId.equals(neighbor.getId())) {
//                return neighbor.getDirection();
//            }
//        }
//        return 0;
//    }
//
//    private void drawImage() {
//        setImage(currentFloorId);
//        canvas = new Canvas(mapImg);
//
//        drawPoint(listPointOnWay.get(0).getId());
////        System.out.println("Starting point: " + getStartingPoint());
////        drawPoint(0);
//
//        if (listPointOnWay.size() == 1) {
//            String idStart = listPointOnWay.get(0).getId();
//            Location startPoint = getLocation(idStart);
//            float xStart, yStart, xEnd, yEnd;
//            xStart = Math.round(mapImg.getWidth() * startPoint.getRatioX());
//            yStart = Math.round(mapImg.getHeight() * startPoint.getRatioY());
//
//            xEnd = Math.round(mapImg.getWidth() * endRoom.getRatioX());
//            yEnd = Math.round(mapImg.getHeight() * endRoom.getRatioY());
//
//            drawLine(xStart, yStart, xEnd, yEnd);
//            imgView.setImageBitmap(mapImg);
//
//            fillArrow(xStart, yStart, xEnd, yEnd);
//        } else {
//            for (int i = 0; i < listPointOnWay.size(); i++) {
//
//                if (i != listPointOnWay.size() - 1) {
//
////                int currentFloor = Integer.parseInt(tmp[tmp.length - 1]);
//                    if (getLocation(listPointOnWay.get(i).getId()).getFloorId().equals(currentFloorId)) {
//
//
//                        String idStart = listPointOnWay.get(i).getId();
//                        String idEnd = listPointOnWay.get(i + 1).getId();
//
//                        Location startPoint = getLocation(idStart);
//
//                        float xStart, yStart, xEnd, yEnd;
//                        xStart = Math.round(mapImg.getWidth() * startPoint.getRatioX());
//                        yStart = Math.round(mapImg.getHeight() * startPoint.getRatioY());
//
//
//                        if (i == listPointOnWay.size() - 2) { // node cuối lấy tọa độ của room
////                        Room room = getRoom(tvEnd.getText().toString());
//
//                            xEnd = Math.round(mapImg.getWidth() * endRoom.getRatioX());
//                            yEnd = Math.round(mapImg.getHeight() * endRoom.getRatioY());
//                        } else {
//                            Location endPoint = getLocation(idEnd);
//
//                            xEnd = Math.round(mapImg.getWidth() * endPoint.getRatioX());
//                            yEnd = Math.round(mapImg.getHeight() * endPoint.getRatioY());
//                        }
//
//                        drawLine(xStart, yStart, xEnd, yEnd);
//                        imgView.setImageBitmap(mapImg);
//
//                        if (i == 0) {
//                            fillArrow(xStart, yStart, xEnd, yEnd);
//                        }
//                    }
//
//
//                } else {
//                    if (getLocation(listPointOnWay.get(i).getId()).getFloorId().equals(currentFloorId)) {
//                        imgView.setImageBitmap(mapImg);
//                    }
//                }
//            }
//        }
//    }
//
//    private Vertex getVertexInList(String id) {
//        for (Vertex vertex : vertexList) {
//            if (id.equals(vertex.getId())) {
//                return vertex;
//            }
//        }
//        return null;
//    }
//
//
//    private void drawPoint(String idStart) {
//        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        for (int i = 0; i < listPointOnWay.size() - 1; i++) {
//            Location location = getLocation(listPointOnWay.get(i).getId());
//            if (location.getFloorId().equals(currentFloorId)) {
//
//                canvas.drawCircle(Math.round(mapImg.getWidth() * location.getRatioX()), Math.round(mapImg.getHeight() * location.getRatioY()), 30, new Paint());
//            }
//
//        }
//
//        if (idStart != null) {
//            Location location = getLocation(idStart);
//            if (location.getFloorId().equals(currentFloorId)) {
//                paint.setStyle(Paint.Style.FILL);
//                paint.setColor(Color.RED);
//                canvas.drawCircle(Math.round(mapImg.getWidth() * location.getRatioX()), Math.round(mapImg.getHeight() * location.getRatioY()), 50, paint);
//            }
//
////            Room room = getRoom(tvEnd.getText().toString());
//            if (endRoom.getFloorId().equals(currentFloorId)) {
//                paint.setStyle(Paint.Style.FILL);
//                paint.setColor(Color.YELLOW);
//                canvas.drawCircle(Math.round(mapImg.getWidth() * endRoom.getRatioX()), Math.round(mapImg.getHeight() * endRoom.getRatioY()), 30, paint);
//            }
//        }
//
//    }
//
//    private void fillArrow(float from_x, float from_y, float to_x, float to_y) {
//
//        Paint paint = new Paint();
//        paint.setStyle(Paint.Style.FILL);
//        paint.setStrokeWidth(20);
//        paint.setColor(Color.BLACK);
//
//        float angle, anglerad, radius, lineangle;
//
//        //values to change for other appearance *CHANGE THESE FOR OTHER SIZE ARROWHEADS*
//        radius = 100;
//        angle = 60;
//
//        //some angle calculations
//        anglerad = (float) (PI * angle / 180.0f);
//        lineangle = (float) (atan2(to_y - from_y, to_x - from_x));
//
//        //tha line
//        canvas.drawLine(from_x, from_y, to_x, to_y, paint);
//
//        //tha triangle
//        Path path = new Path();
//        path.setFillType(Path.FillType.EVEN_ODD);
//        path.moveTo(to_x, to_y);
//        path.lineTo((float) (to_x - radius * cos(lineangle - (anglerad / 2.0))),
//                (float) (to_y - radius * sin(lineangle - (anglerad / 2.0))));
//        path.lineTo((float) (to_x - radius * cos(lineangle + (anglerad / 2.0))),
//                (float) (to_y - radius * sin(lineangle + (anglerad / 2.0))));
//        path.close();
//
//        canvas.drawPath(path, paint);
//
////        canvas.drawLine(x0, y0, x1, y1, paint);
//        imgView.setImageBitmap(mapImg);
//    }
//
//    private void drawLine(final float xStart, final float yStart, final float xEnd, final float yEnd) {
//        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        paint.setStyle(Paint.Style.FILL);
//        paint.setColor(Color.BLUE);
//        paint.setStrokeWidth(25);
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);
//            }
//        });
//    }
//
//    private void setImage(String currentFloorId) {
//        mapImg = BitmapFactory.decodeStream(FileHelper.getImage(this, FileHelper.TYPE_MAP, currentFloorId)).copy(Bitmap.Config.ARGB_8888, true);
//    }
//
//
//    //
////    public void showNextFloor(View view) {
////        if (currentFloorId.equals(getLocation(startLocationId).getFloorId())) {
////
////            updateFloor(endRoom.getFloorId());
////
////            drawImage();
////        } else {
////            updateFloor(getLocation(startLocationId).getFloorId());
////
////            drawImage();
////        }
////    }
//
//    public void showNextFloor(View view) {
//        if (listPointOnWay == null) {
//            if ((getIndexOfFloor(currentFloorId) + 1) == listFloor.size()) {
//                currentFloorId = listFloor.get(0).getId();
//                updateFloor(currentFloorId);
//                canvas = new Canvas(mapImg);
//                drawPoint(null);
//            } else {
//                currentFloorId = listFloor.get(getIndexOfFloor(currentFloorId) + 1).getId();
//                updateFloor(currentFloorId);
//                canvas = new Canvas(mapImg);
//                drawPoint(null);
//            }
//        } else {
//            if ((getIndexOfFloor(currentFloorId) + 1) == listFloor.size()) {
//                currentFloorId = listFloor.get(0).getId();
//                updateFloor(currentFloorId);
//                drawImage();
//            } else {
//                currentFloorId = listFloor.get(getIndexOfFloor(currentFloorId) + 1).getId();
//                updateFloor(currentFloorId);
//                drawImage();
//            }
//        }
//    }
//
//    @Override
//    public void onSensorChanged(SensorEvent event) {
//        if (event.sensor == mRotation) {
//            // convert a rotation vector to a rotation matrix
//            // the result array is passed to rotationMatrix
//            float[] rotationMatrix = new float[9];
//            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values);
//
//            int worldAxisX = SensorManager.AXIS_X; // just testing, we don't need it
//            int worldAxisZ = SensorManager.AXIS_Z;
//            float[] adjustedRotationMatrix = new float[9];
//            SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
//
//            float[] orientation = new float[3]; // the result array >>> x, y, z - rotated coordinate system of device
//            SensorManager.getOrientation(adjustedRotationMatrix, orientation);
//
//            // Notice: multiple by -60 = chỉ số tâm linh >>>> convert from radian to degree
//            float pitch = orientation[1] * (-60); // rotate by X-axis
//            float roll = orientation[2] * (-60); // rotate by Z-axis ===> That's what we need
//
//
//            RotateAnimation ra = new RotateAnimation(
//                    currentDegrees,
//                    -roll,
//                    Animation.RELATIVE_TO_SELF, 0.5f,
//                    Animation.RELATIVE_TO_SELF, 0.5f);
//            ra.setDuration(300);
//
//            // set the animation after the end of the reservation status
//            ra.setFillAfter(true);
//
//            // Start the animation
//            img.startAnimation(ra);
//            currentDegrees = roll;
//        } // end if sensor type
//    }
//
//    @Override
//    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//    }
//
//    public void speak(View view) {
//        VoiceHelper voiceHelper = new VoiceHelper();
//        int orientation = 0;
//        String startPoint = getLocation(listPointOnWay.get(0).getId()).getName();
//        String nextPoint = "";
//        if (listPointOnWay.size() == 1) {
//            //do Nothing
//        } else {
//            orientation = getOrientation(getLocation(startLocationId), getLocation(listPointOnWay.get(1).getId()).getId());
////        Location neighbor = getLocation(listPointOnWay.get(1).getId());
////        Location location = getLocation(startLocationId);
////        updateOrientation(getOrientation(location, neighbor.getId()));
//            for (int i = 1; i < listPointOnWay.size(); i++) {
//                if (orientation != getOrientation(getLocation(listPointOnWay.get(i - 1).getId()), getLocation(listPointOnWay.get(i).getId()).getId())) {
//                    nextPoint = getLocation(listPointOnWay.get(i - 1).getId()).getName();
//                    break;
//                } else {
//                    nextPoint = getLocation(listPointOnWay.get(i).getId()).getName();
//                }
//            }
//        }
//        System.out.println("Start Point: " + startPoint);
//        System.out.println("Next Point: " + nextPoint);
//        System.out.println("Orien: " + orientation);
//
//        voiceHelper.generateSpeechLine(MapActivity.this, startPoint, nextPoint, orientation);
//    }
//
////    private int detechFloor(String floorId) {
////        String[] tmp = floorId.split("_");
////        return Integer.parseInt(tmp[tmp.length - 1]);
////    }
//}
