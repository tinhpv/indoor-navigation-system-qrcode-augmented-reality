package fpt.capstone.inqr.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import fpt.capstone.inqr.R;
import fpt.capstone.inqr.model.supportModel.Line;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

import static java.lang.Math.PI;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MapAdapter extends RecyclerView.Adapter<MapHolder> {

    private List<Bitmap> listSource;
    private List<List<Line>> listLines;
    private Context context;
    private Animation animation;
    private float zoomLevel;

    public MapAdapter(Context context) {
        this.context = context;
    }

    public void setListSource(List<Bitmap> listSource, List<List<Line>> listLines, float zoomLevel) {
        this.listSource = listSource;
        this.listLines = listLines;
        this.zoomLevel = zoomLevel;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MapHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_map_adapter, parent, false);

        return new MapHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MapHolder holder, int position) {
        holder.imgMap.setImageBitmap(listSource.get(position));
//
//        if (position == 0) {
//            Bitmap bitmap = listSource.get(position);
//            createAnimationPoint(bitmap, holder.imgMap);
//        }

        createAnimation(listSource.get(position), holder.imgMap, position);

        if (position == 0) {
            Line firstLine = listLines.get(0).get(0);

            new Handler().postDelayed(() -> {
                float focalX = firstLine.getxStart()*holder.imgMap.getRight()/listSource.get(0).getWidth();
                float focalY = firstLine.getyStart()*holder.imgMap.getBottom()/listSource.get(0).getHeight();
                holder.imgMap.setMaximumScale(5f);
                holder.imgMap.setScale(zoomLevel, focalX, focalY, true);
            }, 500);

        }
    }

    @Override
    public int getItemCount() {
        if (listSource == null) {
            return 0;
        }
        return listSource.size();
    }

    private void createAnimation(Bitmap mapImg, ImageView imgMap, int indexBig) {
        Canvas canvas = new Canvas(mapImg);

        final int[] indexSmall = {0};
        final boolean[] status = {true};

        animation = new TranslateAnimation(0f, 0f, 0f, 0f);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                List<Line> lines = listLines.get(indexBig);

                for (Line line : lines) {
                    drawLine(canvas, line.getxStart(), line.getyStart(), line.getxEnd(), line.getyEnd());
                }

                if (indexSmall[0] != lines.size()) {
                    Line line = lines.get(indexSmall[0]);
                    drawPath(canvas, line.getxStart(), line.getyStart(), line.getxEnd(), line.getyEnd());

                    indexSmall[0]++;
                } else {
                    indexSmall[0] = 0;
                }

                if (indexBig == 0) {
                    Line firstLine = listLines.get(0).get(0);
//                    Bitmap bitmap;
//                    if (status[0]) {
//                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.scanner);
//                        status[0] = false;
//                    } else {
//                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.current_point_input);
//                        status[0] = true;
//                    }

                    fillArrow(canvas, firstLine.getxStart(), firstLine.getyStart(), firstLine.getxEnd(), firstLine.getyEnd());

//                    canvas.drawBitmap(bitmap, Math.round(firstLine.getxStart() - bitmap.getWidth() / 2), Math.round(firstLine.getyStart() - bitmap.getHeight() / 2), new Paint());
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.current_point_input);
                    canvas.drawBitmap(bitmap, Math.round(firstLine.getxStart() - bitmap.getWidth() / 2), Math.round(firstLine.getyStart() - bitmap.getHeight() / 2), new Paint());
                }
            }
        });

        animation.setDuration(500);
        animation.setRepeatCount(TranslateAnimation.INFINITE);

        imgMap.startAnimation(animation);

    }

    private void drawPath(Canvas canvas, final float xStart, final float yStart, final float xEnd, final float yEnd) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(25);
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);
    }

    private void drawLine(Canvas canvas, final float xStart, final float yStart, final float xEnd, final float yEnd) {
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#F78B03"));
        paint.setStrokeWidth(25);
        canvas.drawLine(xStart, yStart, xEnd, yEnd, paint);
    }

    private void fillArrow(Canvas canvas, float from_x, float from_y, float to_x, float to_y) {

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(30);
        paint.setColor(Color.GREEN);

        float angle, anglerad, radius, lineangle;

        //values to change for other appearance *CHANGE THESE FOR OTHER SIZE ARROWHEADS*
        radius = 100;
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
        path.lineTo((float) (to_x - radius * cos(lineangle - (anglerad / 2.0))),
                (float) (to_y - radius * sin(lineangle - (anglerad / 2.0))));
        path.lineTo((float) (to_x - radius * cos(lineangle + (anglerad / 2.0))),
                (float) (to_y - radius * sin(lineangle + (anglerad / 2.0))));
        path.close();

        canvas.drawPath(path, paint);
    }
}

class MapHolder extends RecyclerView.ViewHolder {

    PhotoView imgMap;

    MapHolder(@NonNull View itemView) {
        super(itemView);

        imgMap = itemView.findViewById(R.id.imgMap);
    }
}
