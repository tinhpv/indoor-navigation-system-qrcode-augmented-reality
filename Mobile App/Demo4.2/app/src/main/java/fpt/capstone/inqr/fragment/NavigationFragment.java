package fpt.capstone.inqr.fragment;

import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.ar.core.Anchor;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Session;
import com.google.ar.core.exceptions.UnavailableException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.TransformableNode;
import com.microsoft.azure.spatialanchors.AnchorLocateCriteria;
import com.microsoft.azure.spatialanchors.AnchorLocatedEvent;
import com.microsoft.azure.spatialanchors.CloudSpatialAnchor;
import com.microsoft.azure.spatialanchors.CloudSpatialAnchorSession;
import com.microsoft.azure.spatialanchors.CloudSpatialAnchorWatcher;
import com.microsoft.azure.spatialanchors.LocateAnchorStatus;
import com.microsoft.azure.spatialanchors.NearAnchorCriteria;
import com.microsoft.azure.spatialanchors.OnLogDebugEvent;
import com.microsoft.azure.spatialanchors.SessionErrorEvent;
import com.microsoft.azure.spatialanchors.SessionLogLevel;
import com.microsoft.azure.spatialanchors.SessionUpdatedEvent;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;
import fpt.capstone.inqr.R;
import fpt.capstone.inqr.adapter.LocationStepAdapter;
import fpt.capstone.inqr.callbacks.BottomSheetRoomListener;
import fpt.capstone.inqr.dijkstra.Vertex;
import fpt.capstone.inqr.helper.ImageHelper;
import fpt.capstone.inqr.helper.QRCodeHelper;
import fpt.capstone.inqr.helper.Wayfinder;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.model.supportModel.AnchorModel;
import fpt.capstone.inqr.model.supportModel.LocationDemo;

/**
 * Demo4
 * Created by TinhPV on 4/9/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class NavigationFragment extends BaseFragment implements Scene.OnUpdateListener, BottomSheetRoomListener {

    private static final String TAG = "INQR_NAVIGATION";
    private static final int SCANNING_RADIUS = 20; // scanning radius in meters

    private View view;
    private TextView tvProgressStatus, tvScanningResult, tvDestination, tvCurrentDestination;
    private CustomArFragment mArFragment;
    private ArSceneView mSceneView;

    private CloudSpatialAnchorSession cloudSession;
    private CloudSpatialAnchor sourceAnchor;
    private AnchorNode sourceAnchorNode, desAnchorNode;
    boolean enoughDataForSaving;
    private final Object progressLock = new Object();

    private int step;
    private boolean didScan, didQrAnchorPlaced;

    private List<AnchorModel> mAnchorModelList;
    private List<Vertex> pathList;
    private List<String> qrCodeIdList;
    private Map<String, String> qrAnchorIdList;
    private String srcAnchorId, desAnchorId, scannedAnchorID;

    private List<Location> mLocationList, mLocationPathList;
    private List<Room> mRoomList;
    private String destinationRoomName;
    private String scannedLocationId, previousLocationID;
    private Wayfinder wayfinder;

    private ConstraintLayout expandableView;
    private Button btExpand, btChangeDestination;
    private CardView mCardView;
    private RecyclerView rvLocationStep;
    private LocationStepAdapter mStepAdapter;


    public NavigationFragment(List<Location> locationList, List<Room> roomList, String destinationRoomName) {
        this.mLocationList = locationList;
        this.mRoomList = roomList;
        this.destinationRoomName = destinationRoomName;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSceneView != null && mSceneView.getSession() == null) {
            try {
                Session session = new Session(getContext());
                Config config = new Config(session);
                config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
                config.setFocusMode(Config.FocusMode.AUTO);
                session.configure(config);
                mSceneView.setupSession(session);
            } catch (UnavailableException e) {
                return;
            } // end try-catch
        } // end if

        initializeSession();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_navigation, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initializeUI();
        prepareData();
    }

    private void initializeUI() {
        rvLocationStep = view.findViewById(R.id.rv_location_step);
        rvLocationStep.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));

        tvDestination = view.findViewById(R.id.tv_destination);
        tvDestination.setText(destinationRoomName);

        tvCurrentDestination = view.findViewById(R.id.tv_current_location);

        tvProgressStatus = view.findViewById(R.id.tv_status);
        tvScanningResult = view.findViewById(R.id.tv_scanning_result);
        mCardView = view.findViewById(R.id.card_view);
        expandableView = view.findViewById(R.id.expandable_view);

        btChangeDestination = view.findViewById(R.id.bt_change_destination);
        btChangeDestination.setOnClickListener(v -> {
            ChangeDestinationBottomSheetFragment changeDestinationFragment = new ChangeDestinationBottomSheetFragment(this, mRoomList);
            changeDestinationFragment.show(getActivity().getSupportFragmentManager(), null);
        });

        btExpand = view.findViewById(R.id.bt_expand);
        btExpand.setOnClickListener(v -> {
            if (expandableView.getVisibility() == View.GONE) {
                TransitionManager.beginDelayedTransition(mCardView, new AutoTransition());
                expandableView.setVisibility(View.VISIBLE);
                btExpand.setBackgroundResource(R.drawable.ic_expand_less_black_24dp);
            } else {
                TransitionManager.beginDelayedTransition(mCardView, new AutoTransition());
                expandableView.setVisibility(View.GONE);
                btExpand.setBackgroundResource(R.drawable.ic_expand_more_black_24dp);
            }
        });


        // ar initialization
        mArFragment = (CustomArFragment) getChildFragmentManager().findFragmentById(R.id.ar_fragment);
        mSceneView = mArFragment.getArSceneView();
        mSceneView.getScene().addOnUpdateListener(this);

    }

    private void prepareData() {
        wayfinder = new Wayfinder(mLocationList, mRoomList);

        // extract location's QR Anchor ID list
        qrAnchorIdList = new HashMap<>();
        qrCodeIdList = new ArrayList<>();
        mAnchorModelList = new ArrayList<>();

        mLocationList.forEach(location -> {
            qrAnchorIdList.put(location.getId(), location.getQrAnchorId());
            qrCodeIdList.add(location.getQrAnchorId());
        });

        step = 0;
        scannedAnchorID = "";
        scannedLocationId = "a";
        previousLocationID = "b";
        didScan = false;
        didQrAnchorPlaced = false;
        desAnchorNode = null;
        sourceAnchorNode = null;
    }

    private void initializeSession() {
        Session arcoreSession = this.mSceneView.getSession();

        if (arcoreSession == null) {
            Toast.makeText(getContext(), "The ARCore Session may not be null", Toast.LENGTH_SHORT).show();
            return;
        }

        if (this.cloudSession != null) {
            this.cloudSession.close();
        }

        this.cloudSession = new CloudSpatialAnchorSession();
        this.cloudSession.getConfiguration().setAccountId(getString(R.string.ACCOUNT_ID));
        this.cloudSession.getConfiguration().setAccountKey(getString(R.string.ACCOUNT_KEY));
        this.cloudSession.setSession(arcoreSession);
        this.cloudSession.setLogLevel(SessionLogLevel.All);
        this.cloudSession.addOnLogDebugListener(this::onLogDebugListener);
        this.cloudSession.addErrorListener(this::onErrorListener);

        this.cloudSession.addSessionUpdatedListener(this::onSessionUpdated);
        this.cloudSession.addAnchorLocatedListener(this::onAnchorLocated);
        this.cloudSession.start();
    }


    @Override
    public void onUpdate(FrameTime frameTime) {
        // delegate this frame to cloud session to process
        if (this.cloudSession != null) {
            this.cloudSession.processFrame(mSceneView.getArFrame());
        }

        // ----- get image from arcore frame to analyze QR-Code ----- //
        if (!didScan) {
            Frame frame = mSceneView.getArFrame();
            if (null == frame) return;

            try (Image image = frame.acquireCameraImage()) {
                if (image.getFormat() != ImageFormat.YUV_420_888) {
                    throw new IllegalArgumentException(
                            "Expected image in YUV_420_888 format, got format " + image.getFormat());
                } // end if not exact format

                // read qr code from image
                String code = QRCodeHelper.detectQRCode(getContext(), ImageHelper.fromYUVImageToARGB(image));

                if (code != null) {
                    getActivity().runOnUiThread(() -> tvScanningResult.setText(code));
                    String[] arrOfStr = code.split("\\|")[0].split(":");
                    scannedLocationId = arrOfStr[arrOfStr.length - 1].trim();
//                    scannedAnchorID = qrAnchorIdList.get(scannedLocationId);
                    // TODO: change hard code here
                    scannedAnchorID = "72521f11-8da9-495e-8f75-8a630ed5d7fa";

                    if (!scannedLocationId.equals(previousLocationID)) {
                        didQrAnchorPlaced = false;
                        handleLocate();
                        didScan = true;
                    }
                } // end if null code

            } catch (Exception e) {
                Log.e(TAG, "Exception copying image", e);
            }
        } // end scanning
    }

    private void handleLocate() {
        // remove all models on scene
        resetAnchors();

        // stop current cloud session, start another to locate
        cloudSession.stop();
        cloudSession.reset();

        // restart session
        initializeSession();

        // go go go locate by anchor id
        AnchorLocateCriteria criteria = new AnchorLocateCriteria();
        criteria.setIdentifiers(new String[]{ scannedAnchorID });

        stopLocating();
        cloudSession.createWatcher(criteria);
    }

    private void onAnchorLocated(AnchorLocatedEvent anchorLocatedEvent) {
        if (anchorLocatedEvent.getStatus() == LocateAnchorStatus.Located) {
            getActivity().runOnUiThread(() -> processCloudAnchor(anchorLocatedEvent.getAnchor()));
        } // end if
    }

    private boolean isQRCodeAnchor(String id) {
        // TODO: ADD
        return id.equals("72521f11-8da9-495e-8f75-8a630ed5d7fa");
//        return qrCodeIdList.contains(id);
    }

    private int didPathListContainLocation(String locationID) {
        if (null != pathList) {
            for (int i = 0; i < pathList.size(); i++) {
                if (locationID.equals(pathList.get(i).getId())) return i;
            }  // end for
        } // end if
        return -1;
    }

    private Location getLocation(String id) {
        for (int i = 0; i < mLocationList.size(); i++) {
            if (id.equals(mLocationList.get(i).getId())) {
                return mLocationList.get(i);
            }
        }
        return null;
    }

    private void processCloudAnchor(CloudSpatialAnchor cloudAnchor) {
        String anchorId = cloudAnchor.getIdentifier();

        if (!didQrAnchorPlaced && isQRCodeAnchor(anchorId)) {
            didQrAnchorPlaced = true;
            previousLocationID = scannedLocationId;

            getActivity().runOnUiThread(() -> {
                tvCurrentDestination.setText(getLocation(scannedLocationId).getName());
                AnchorModel model = new AnchorModel(cloudAnchor.getLocalAnchor());
                model.render(getContext(), mArFragment, new Color(android.graphics.Color.BLUE));
                mAnchorModelList.add(model);
            });

            pathList = wayfinder.getShortestPathList();
            int index = didPathListContainLocation(scannedLocationId);

            // TODO: CHECK OR CALCULATE NEW PATH
            if (index != -1) {
                step = index;
            } else {
                step = 0;
                wayfinder.findWay(scannedLocationId, destinationRoomName);
                pathList = wayfinder.getShortestPathList();
                setupLocationStep();
            }

            if (step + 1 <= pathList.size() - 1) {
                srcAnchorId = getLocation(pathList.get(step).getId()).getSpaceAnchorId();
                desAnchorId = getLocation(pathList.get(step + 1).getId()).getSpaceAnchorId();
                step = step + 1;
                sourceAnchorNode = null;
                desAnchorNode = null;
                sourceAnchor = cloudAnchor;
                handleLookForNearby();
            }

        } else if (srcAnchorId.equals(anchorId)) {
            sourceAnchorNode = new AnchorNode(cloudAnchor.getLocalAnchor());
            AnchorModel model = new AnchorModel(cloudAnchor.getLocalAnchor());
            model.render(getContext(), mArFragment, new Color(android.graphics.Color.BLUE));
            mAnchorModelList.add(model);
        } else if (desAnchorId.equals(anchorId)) {
            desAnchorNode = new AnchorNode(cloudAnchor.getLocalAnchor());
            AnchorModel model = new AnchorModel(cloudAnchor.getLocalAnchor());
            model.render(getContext(), mArFragment, new Color(android.graphics.Color.BLUE));
            mAnchorModelList.add(model);
        }

        if (null != sourceAnchorNode && null != desAnchorNode) {
            getActivity().runOnUiThread(() -> drawLine(sourceAnchorNode, desAnchorNode));
            didScan = false;
        }




    }

    private void setupLocationStep() {
        mLocationPathList = new ArrayList<>();
        pathList.forEach(vertex -> mLocationPathList.add(getLocation(vertex.getId())));
        mStepAdapter = new LocationStepAdapter(getContext(), mLocationPathList);
        rvLocationStep.setAdapter(mStepAdapter);
    }

    private void handleLookForNearby() {
        NearAnchorCriteria nearAnchorCriteria = new NearAnchorCriteria();
        nearAnchorCriteria.setSourceAnchor(this.sourceAnchor);

        // SEARCHING FOR ANCHORs AROUND 10m
        nearAnchorCriteria.setDistanceInMeters(SCANNING_RADIUS);

        AnchorLocateCriteria nearbyLocateCriteria = new AnchorLocateCriteria();
        nearbyLocateCriteria.setNearAnchor(nearAnchorCriteria);

        stopLocating();
        cloudSession.createWatcher(nearbyLocateCriteria);
    }

    private void stopLocating() {
        List<CloudSpatialAnchorWatcher> watchers = cloudSession.getActiveWatchers();
        if (watchers.isEmpty()) return;
        CloudSpatialAnchorWatcher watcher = watchers.get(0);
        watcher.stop();
    }

    private void resetAnchors() {
        for (AnchorModel model : mAnchorModelList) {
            model.destroy();
        } // end for
        mAnchorModelList.clear();
    }

    private void renderModel(AnchorNode anchorNode, Color color) {
        MaterialFactory.makeOpaqueWithColor(getContext(), color)
                .thenAccept(material -> {
                    Renderable nodeRenderable = ShapeFactory.makeCylinder(0.1f, 0.0001f, new Vector3(0.0f, 0.002f, 0.0f), material);
                    anchorNode.setRenderable(nodeRenderable);
                    anchorNode.setParent(mArFragment.getArSceneView().getScene());
                }); // end rendering
    }

    private void onSessionUpdated(SessionUpdatedEvent sessionUpdatedEvent) {
        // frame collected by cloud session
        float progress = sessionUpdatedEvent.getStatus().getRecommendedForCreateProgress();
        enoughDataForSaving = progress >= 1.0;

        synchronized (progressLock) {
            DecimalFormat decimalFormat = new DecimalFormat("00");
            getActivity().runOnUiThread(() -> {
                String progressMessage = "progress: " + decimalFormat.format(Math.min(1.0f, progress) * 100) + "%";
                tvProgressStatus.setText(progressMessage);
            });

            if (enoughDataForSaving) {
                getActivity().runOnUiThread(() -> {
                    tvProgressStatus.setText("READ TO SAVE");
                });
            } // end if enough data
        } // end synchronized

    }

    private void drawLine(AnchorNode node1, AnchorNode node2) {

        Vector3 point1, point2;
        point1 = node1.getWorldPosition();
        point2 = node2.getWorldPosition();
        node1.setParent(mArFragment.getArSceneView().getScene());

        //find the vector extending between the two points and define a look rotation
        //in terms of this Vector.
        final Vector3 difference = Vector3.subtract(point1, point2);
        final Vector3 directionFromTopToBottom = difference.normalized();
        final Quaternion rotationFromAToB =
                Quaternion.lookRotation(directionFromTopToBottom, Vector3.up());

//        Texture.Sampler sampler = Texture.Sampler.builder()
//                .setMinFilter(Texture.Sampler.MinFilter.LINEAR_MIPMAP_LINEAR)
//                .setMagFilter(Texture.Sampler.MagFilter.LINEAR)
//                .setWrapModeR(Texture.Sampler.WrapMode.REPEAT)
//                .setWrapModeS(Texture.Sampler.WrapMode.REPEAT)
//                .setWrapModeT(Texture.Sampler.WrapMode.REPEAT)
//                .build();

        MaterialFactory.makeTransparentWithColor(getContext(), new Color(247, 181, 0, 0.7f))
                .thenAccept(
                        material -> {
                            // create a rectangular prism, using ShapeFactory.makeCube()
                            // use the difference vector to extend to the necessary length
                            ModelRenderable model = ShapeFactory.makeCube(
                                    new Vector3(.15f, .001f, difference.length()),
                                    Vector3.zero(), material);

                            // set the world rotation of the node to the rotation calculated earlier
                            // and set the world position to the midpoint between the given points
                            Node nodeForLine = new Node();
                            nodeForLine.setParent(node1);
                            nodeForLine.setRenderable(model);
                            nodeForLine.setWorldPosition(Vector3.add(point1, point2).scaled(.5f));
                            nodeForLine.setWorldRotation(rotationFromAToB);
                        }
                ); // end rendering


        // RENDER THE ARROW
        ModelRenderable.builder()
                .setSource(getContext(), Uri.parse("arrow.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    AnchorNode anchorNode = new AnchorNode(node1.getAnchor());
                    TransformableNode transformableNode = new TransformableNode(mArFragment.getTransformationSystem());
                    transformableNode.setParent(anchorNode);
                    transformableNode.setRenderable(modelRenderable);

                    // RELATIVELY SET THE ORIENTATION OF ARROW OBJECT TO THE ANCHOR NODE 2
                    // THIS IS THE DEFAULT RATATION ANGLE
                    transformableNode.setWorldRotation(rotationFromAToB);
                    transformableNode.select();
                    mArFragment.getArSceneView().getScene().addChild(anchorNode);


                    // ROTATE MORE 45 DEGREES FOR EXACTLY HEADING TO THE ANCHOR 2
                    transformableNode.setLocalRotation(Quaternion
                            .multiply(transformableNode.getLocalRotation(), new Quaternion(Vector3.up(), 45f)));
                })
                .exceptionally(throwable -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(throwable.getMessage()).show();
                    return null;
                });
    }


    private void onLogDebugListener(OnLogDebugEvent onLogDebugEvent) {
        Log.d(TAG, "onLogDebugListener: " + onLogDebugEvent.getMessage());
    }

    private void onErrorListener(SessionErrorEvent sessionErrorEvent) {
        Log.d(TAG, "onErrorListener: " + sessionErrorEvent.getErrorMessage());
    }

    @Override
    public void roomChoosen(String roomName) {
        tvDestination.setText(roomName);
        wayfinder.setCurrentShortestDistance(0.0);
        wayfinder.setShortestPathList(null);
    }
}
