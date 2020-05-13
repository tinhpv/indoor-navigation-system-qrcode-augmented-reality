package fpt.capstone.inqr.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.model.Location;
import fpt.capstone.inqr.model.Neighbor;
import fpt.capstone.inqr.model.Room;
import fpt.capstone.inqr.model.supportModel.Line;
import fpt.capstone.inqr.model.supportModel.Stair;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Demo4
 * Created by TinhPV on 2020-04-21
 * Copyright © 2020 TinhPV. All rights reserved
 **/


public class CanvasHelper {

    public static List<Line> drawImage(Context context, Bitmap mapImg, String currentFloorId, List<Location> locationPathList, Room destinationRoom, double distance) {
        List<Line> lines = new ArrayList<>();

        Canvas canvas = new Canvas(mapImg);

        Paint paint = new Paint();
        int color = ContextCompat.getColor(context, R.color.green);
        paint.setColor(color);
        paint.setStrokeWidth(26);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10));
        paint.setAntiAlias(true);

        // case ko tìm được đường
        if (locationPathList.size() == 2 && distance == -1) {
            drawSourceAndDestinationBitmap(context, canvas, mapImg, locationPathList.get(0), destinationRoom, currentFloorId);

            float startX, startY, endX, endY;

            Location startPoint = locationPathList.get(0);
            startX = Math.round(mapImg.getWidth() * startPoint.getRatioX());
            startY = Math.round(mapImg.getHeight() * startPoint.getRatioY());

            lines.add(new Line(false, startX, startY, startX, startY));


            endX = Math.round(mapImg.getWidth() * destinationRoom.getRatioX());
            endY = Math.round(mapImg.getHeight() * destinationRoom.getRatioY());

            lines.add(new Line(true, endX, endY, endX, endY));

        } else {

            float arrowStartX = 0.0f, arrowStartY = 0.0f, arrowEndX = 0.0f, arrowEndY = 0.0f;

            if (locationPathList.size() == 1) {
                Location startPoint = locationPathList.get(0);
                float startX = Math.round(mapImg.getWidth() * startPoint.getRatioX());
                float startY = Math.round(mapImg.getHeight() * startPoint.getRatioY());
                float endX = Math.round(mapImg.getWidth() * destinationRoom.getRatioX());
                float endY = Math.round(mapImg.getHeight() * destinationRoom.getRatioY());

                lines.add(new Line(true, startX, startY, endX, endY));

                Path path = new Path();
                path.moveTo(startX, startY);
                path.lineTo(endX, endY);
                canvas.drawPath(path, paint);

                arrowStartX = startX;
                arrowStartY = startY;
                arrowEndX = endX;
                arrowEndY = endY;
            } else {
                Path path = new Path();
                for (int i = 0; i < locationPathList.size() - 1; i++) {
//                    if (i != locationPathList.size() - 1) {
                    if (locationPathList.get(i).getFloorId().equals(currentFloorId)) {
                        Location startPoint = locationPathList.get(i);

                        float startX, startY, endX, endY;

                        startX = Math.round(mapImg.getWidth() * startPoint.getRatioX());
                        startY = Math.round(mapImg.getHeight() * startPoint.getRatioY());

                        if (i == locationPathList.size() - 2) { // node cuối lấy tọa độ của room
                            endX = Math.round(mapImg.getWidth() * destinationRoom.getRatioX());
                            endY = Math.round(mapImg.getHeight() * destinationRoom.getRatioY());
                        } else {
                            Location endPoint = locationPathList.get(i + 1);
                            endX = Math.round(mapImg.getWidth() * endPoint.getRatioX());
                            endY = Math.round(mapImg.getHeight() * endPoint.getRatioY());
                        }

                        path.moveTo(startX, startY);
                        path.lineTo(endX, endY);

//                            lines.add(new Line(startX, startY, endX, endY));

                        if (i == locationPathList.size() - 2) {
                            lines.add(new Line(true, startX, startY, endX, endY));
                        } else {
                            lines.add(new Line(false, startX, startY, endX, endY));
                        }

                        if (i == 0) {
                            arrowStartX = startX;
                            arrowStartY = startY;
                            arrowEndX = endX;
                            arrowEndY = endY;
                        }
                    } // end if current floor equal
//                    } // end if max size
                } // end for each point
                canvas.drawPath(path, paint);
            }

            Location startLocation = locationPathList.get(0);

            if (startLocation.getRatioX() != destinationRoom.getRatioX() || startLocation.getRatioY() != destinationRoom.getRatioY()) {
                drawArrow(context, canvas, arrowStartX, arrowStartY, arrowEndX, arrowEndY);
            }

            drawSourceAndDestinationBitmap(context, canvas, mapImg, locationPathList.get(0), destinationRoom, currentFloorId);
        }


        return lines;
    }

    private static void drawSourceAndDestinationBitmap(Context context, Canvas canvas, Bitmap mapImg, Location startLocation,
                                                       Room destinationRoom, String currentFloorId) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (startLocation != null) {
            if (startLocation.getFloorId().equals(currentFloorId)) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.RED);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.current_point);
                canvas.drawBitmap(bitmap,
                        Math.round(mapImg.getWidth() * startLocation.getRatioX() - bitmap.getWidth() / 2),
                        Math.round(mapImg.getHeight() * startLocation.getRatioY() - bitmap.getHeight() / 2), new Paint());
            }

            if (destinationRoom.getFloorId().equals(currentFloorId)) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.YELLOW);
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.destination_on_map);
                canvas.drawBitmap(bitmap,
                        Math.round(mapImg.getWidth() * destinationRoom.getRatioX() - bitmap.getWidth() / 2),
                        Math.round(mapImg.getHeight() * destinationRoom.getRatioY() - bitmap.getHeight()), new Paint());
            }
        } // end if null
    }

    private static void drawArrow(Context context, Canvas canvas, float from_x, float from_y, float to_x, float to_y) {

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(26);
        int color = ContextCompat.getColor(context, R.color.dark_yellow);
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setPathEffect(new CornerPathEffect(10));

        float angle, anglerad, radius, lineangle;

        //values to change for other appearance *CHANGE THESE FOR OTHER SIZE ARROWHEADS*
        radius = 96;
        angle = 60;

        //some angle calculations
        anglerad = (float) (PI * angle / 180.0f);
        lineangle = (float) (atan2(to_y - from_y, to_x - from_x));

        //tha line
        canvas.drawLine(from_x, from_y, to_x, to_y, paint);

        //tha triangle
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(to_x, to_y);
        path.lineTo((float) (to_x - radius * cos(lineangle - (anglerad / 2))),
                (float) (to_y - radius * sin(lineangle - (anglerad / 2))));
        path.lineTo((float) (to_x - radius * cos(lineangle + (anglerad / 2))),
                (float) (to_y - radius * sin(lineangle + (anglerad / 2))));
        path.close();

        canvas.drawPath(path, paint);
    }

    public static List<Stair> getStair(String floorId, Bitmap mapImg, List<Location> locationPathList, double distance) {

        List<Stair> listStairs = new ArrayList<>();
        if (distance > 0) {


            for (int i = 0; i < locationPathList.size() - 1; i++) {
                Location location = locationPathList.get(i);
                Location nextLocation = locationPathList.get(i + 1);

                if (location.getFloorId().equals(floorId)) {

                    float ratioX = Math.round(mapImg.getWidth() * location.getRatioX());
                    float ratioY = Math.round(mapImg.getHeight() * location.getRatioY());

                    if (getNeighborOrientation(location, nextLocation.getId()).equals(Neighbor.ORIENT_UP)) {
                        listStairs.add(new Stair(Neighbor.ORIENT_UP, ratioX, ratioY));
                    } else if (getNeighborOrientation(location, nextLocation.getId()).equals(Neighbor.ORIENT_DOWN)) {
                        listStairs.add(new Stair(Neighbor.ORIENT_DOWN, ratioX, ratioY));
                    }
                }


            }
        }


        return listStairs;
    }

    private static String getNeighborOrientation(Location location, String neighborId) {
        for (Neighbor neighbor : location.getNeighborList()) {
            if (neighborId.equals(neighbor.getId())) {
                return neighbor.getDirection();
            }
        }
        return null;
    }
}
