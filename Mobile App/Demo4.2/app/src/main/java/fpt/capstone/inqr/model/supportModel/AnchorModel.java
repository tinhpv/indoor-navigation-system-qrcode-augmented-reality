package fpt.capstone.inqr.model.supportModel;

import android.content.Context;
import android.net.Uri;

import androidx.appcompat.app.AlertDialog;

import com.google.ar.core.Anchor;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.Color;
import com.google.ar.sceneform.rendering.MaterialFactory;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.ShapeFactory;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.microsoft.azure.spatialanchors.CloudSpatialAnchor;

import fpt.capstone.inqr.helper.MainThreadContext;

/**
 * Demo4
 * Created by TinhPV on 2020-04-18
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class AnchorModel {
    private final AnchorNode anchorNode;
    private CloudSpatialAnchor cloudAnchor;
    private Renderable nodeRenderable;
    TransformableNode transformableNode;

    public AnchorModel(Anchor localAnchor) {
        anchorNode = new AnchorNode(localAnchor);
    }

    public AnchorModel(CloudSpatialAnchor cloudAnchor) {
        this(cloudAnchor.getLocalAnchor());
        setCloudAnchor(cloudAnchor);
    }

    public AnchorNode getAnchorNode() {
        return anchorNode;
    }

    public CloudSpatialAnchor getCloudAnchor() {
        return cloudAnchor;
    }

    public Anchor getLocalAnchor() {
        return this.anchorNode.getAnchor();
    }

    public void setCloudAnchor(CloudSpatialAnchor cloudAnchor) {
        this.cloudAnchor = cloudAnchor;
    }

    public void render(Context context, ArFragment arFragment, Color color) {
        MainThreadContext.runOnUiThread(() -> {
            MaterialFactory.makeOpaqueWithColor(context, color)
                    .thenAccept(material -> {
                        this.nodeRenderable = ShapeFactory.makeCylinder(0.1f, 0.0001f, new Vector3(0.0f, 0.002f, 0.0f), material);
                        this.anchorNode.setRenderable(nodeRenderable);
                        this.anchorNode.setParent(arFragment.getArSceneView().getScene());
                    });
        }); // end MainThread
    }

    public void renderDes(Context context, ArFragment arFragment) {
        MainThreadContext.runOnUiThread(() -> {
            ModelRenderable.builder()
                    .setSource(context, Uri.parse("Locator.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> {
                        this.transformableNode = new TransformableNode(arFragment.getTransformationSystem());
                        this.transformableNode.setParent(this.anchorNode);
                        this.transformableNode.setRenderable(modelRenderable);
                        this.transformableNode.select();
                        arFragment.getArSceneView().getScene().addChild(this.anchorNode);
                    })
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(throwable.getMessage()).show();
                        return null;
                    });
        }); // end MainThread
    }

    public void destroy() {
        MainThreadContext.runOnUiThread(() -> {
            this.anchorNode.setRenderable(null);
            this.anchorNode.setParent(null);
            Anchor localAnchor =  anchorNode.getAnchor();
            if (localAnchor != null) {
                anchorNode.setAnchor(null);
                localAnchor.detach();
            } // end if null local anchor
        }); // end MainThread
    }

}
