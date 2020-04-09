package fpt.capstone.inqr.fragment;

import android.graphics.ImageFormat;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.helper.ImageHelper;
import fpt.capstone.inqr.helper.QRCodeHelper;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.supportModel.LocationDemo;

/**
 * Demo4
 * Created by TinhPV on 4/9/20
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class NavigationFragment extends BaseFragment implements Scene.OnUpdateListener {

    private static final String TAG = "INQR_NAVIGATION";
    private static final int SCANNING_RADIUS = 10; // scanning radius is 10 meters

    View view;
    private TextView tvProgressStatus, tvScanningResult;
    private CustomArFragment mArFragment;
    private ArSceneView mSceneView;

    private CloudSpatialAnchorSession cloudSession;
    private CloudSpatialAnchor sourceAnchor;
    private AnchorNode sourceAnchorNode, desAnchorNode;
    boolean enoughDataForSaving;
    private final Object progressLock = new Object();

    private int step;
    private boolean didScan, didQrAnchorPlaced;

    private List<LocationDemo> pathList;
    private List<String> qrCodeIdList;
    private Map<String, String> qrAnchorIdList;
    private String srcAnchorId, desAnchorId, scannedID;

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
        tvProgressStatus = view.findViewById(R.id.tv_status);
        tvScanningResult = view.findViewById(R.id.tv_scanning_result);

        // ar initialization
        mArFragment = (CustomArFragment) getChildFragmentManager().findFragmentById(R.id.ar_fragment);
        mSceneView = mArFragment.getArSceneView();
        mSceneView.getScene().addOnUpdateListener(this);
    }

    private void prepareData() {
        qrAnchorIdList = new HashMap<>();

        // Location: NAME -> QR-CODE ANCHOR ID
        qrAnchorIdList.put("Penrose", "944821f9-d39c-42d6-9a68-9471e7df8603");
        qrAnchorIdList.put("Stairs near Reception", "ec1c6f39-358b-4524-ba66-773f44bf8dcc");
        qrAnchorIdList.put("001 - 002", "fc6b741a-715c-4edd-a55b-6fc7d292ea65");


        // QRCODE - ANCHOR ID - LIST
        qrCodeIdList = new ArrayList<>();
        qrCodeIdList.add("944821f9-d39c-42d6-9a68-9471e7df8603");
        qrCodeIdList.add("ec1c6f39-358b-4524-ba66-773f44bf8dcc");
        qrCodeIdList.add("fc6b741a-715c-4edd-a55b-6fc7d292ea65");


        LocationDemo A = new LocationDemo("fc6b741a-715c-4edd-a55b-6fc7d292ea65", "d718f98e-f1bf-4b99-8bf6-75365d75bd26");
        LocationDemo B = new LocationDemo("944821f9-d39c-42d6-9a68-9471e7df8603", "f3fea701-da32-4e7c-9e71-87933d224cf6");
        LocationDemo C = new LocationDemo("ec1c6f39-358b-4524-ba66-773f44bf8dcc", "db2ddc88-f68e-4cf6-98a4-ba47146a4148");

        pathList = new ArrayList<>();
        pathList.add(A); // Source
        pathList.add(B);
        pathList.add(C); // Destination


        step = 0;
        scannedID = null;
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
                    String[] arrOfStr = code.split(":");
                    scannedID = qrAnchorIdList.get(arrOfStr[arrOfStr.length - 1].trim());
                    didQrAnchorPlaced = false;
                    handleLocate();
                    didScan = true;
                } // end if null code

            } catch (Exception e) {
                Log.e(TAG, "Exception copying image", e);
            }
        } // end scanning
    }

    private void handleLocate() {
        if (desAnchorNode != null) {
            mArFragment.getArSceneView().getScene().removeChild(desAnchorNode);
            desAnchorNode.getAnchor().detach();
            desAnchorNode.setParent(null);
            desAnchorNode = null;
            Toast.makeText(getContext(), "Test Delete - anchorNode removed", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Test Delete - markAnchorNode was null", Toast.LENGTH_SHORT).show();
        }

        // stop current cloud session, start another to locate
        cloudSession.stop();
        cloudSession.reset();

        // restart session
        initializeSession();

        // go go go locate by anchor id
        AnchorLocateCriteria criteria = new AnchorLocateCriteria();
        criteria.setIdentifiers(new String[]{ scannedID });

        stopLocating();
        cloudSession.createWatcher(criteria);
    }

    private void onAnchorLocated(AnchorLocatedEvent anchorLocatedEvent) {
        if (anchorLocatedEvent.getStatus() == LocateAnchorStatus.Located) {
            processCloudAnchor(anchorLocatedEvent.getAnchor());
        } // end if
    }

    private boolean isQRCodeAnchor(String id) {
        return qrCodeIdList.contains(id);
    }

    private void processCloudAnchor(CloudSpatialAnchor cloudAnchor) {
        String anchorId = cloudAnchor.getIdentifier();

        getActivity().runOnUiThread(() -> {
            if (!didQrAnchorPlaced && isQRCodeAnchor(anchorId)) {
                didQrAnchorPlaced = true;
                getActivity().runOnUiThread(() -> renderModel(new AnchorNode(cloudAnchor.getLocalAnchor()), new Color(android.graphics.Color.GREEN)));

                if (step + 1 <= pathList.size() - 1) {
                    srcAnchorId = pathList.get(step).getSpaceAnchorId();
                    desAnchorId = pathList.get(step + 1).getSpaceAnchorId();
                    step = step + 1;
                    sourceAnchorNode = null;
                    desAnchorNode = null;
                    sourceAnchor = cloudAnchor;
                    handleLookForNearby();
                }
            } else if (srcAnchorId.equals(anchorId)) {
                sourceAnchorNode = new AnchorNode(cloudAnchor.getLocalAnchor());
                getActivity().runOnUiThread(() -> renderModel(sourceAnchorNode, new Color(android.graphics.Color.BLUE)));
            } else if (desAnchorId.equals(anchorId)) {
                desAnchorNode = new AnchorNode(cloudAnchor.getLocalAnchor());
                getActivity().runOnUiThread(() -> renderModel(desAnchorNode, new Color(android.graphics.Color.BLUE)));
            }

            if (null != sourceAnchorNode && null != desAnchorNode) {
                getActivity().runOnUiThread(() -> drawLine(sourceAnchorNode, desAnchorNode));
                didScan = false;
            }
        });
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
    }


    private void onLogDebugListener(OnLogDebugEvent onLogDebugEvent) {
        Log.d(TAG, "onLogDebugListener: " + onLogDebugEvent.getMessage());
    }

    private void onErrorListener(SessionErrorEvent sessionErrorEvent) {
        Log.d(TAG, "onErrorListener: " + sessionErrorEvent.getErrorMessage());
    }
}
