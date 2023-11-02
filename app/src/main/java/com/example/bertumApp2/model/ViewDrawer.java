package com.example.bertumApp2.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Base64;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class ViewDrawer extends View {
    private List<Polygon> polygons;
    private static final int INVALID_POINTER_ID = -1;
    private Bitmap originalPhoto, scaledPhoto;
    private ScaleGestureDetector scaleDetector;
    private ImageView pickedDetail;
    private TextView textView5;
    private Button button;
    private Point lastPoint = new Point(0f, 0f);
    private Point deltaPoint = new Point(0f, 0f);
    private Point posPoint = new Point(0f, 0f);
    private int mActivePointerId = INVALID_POINTER_ID;
    private float scale;
    private boolean landscape;
    private boolean isPinching;
    private Paint otherPaint, outerPaint;

    public ViewDrawer(Context context, String photo, List<Polygon> polygons, boolean landscape, ImageView pickedDetail, Button button, TextView textView5) {
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initPaint();
        this.polygons = polygons;
        this.originalPhoto = base64ToBitmap(photo);
        this.scaledPhoto = originalPhoto;
        this.landscape = landscape;
        this.pickedDetail = pickedDetail;
        this.textView5 = textView5;
        this.button = button;
        this.scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.scale = 1f;
    }

    private void initPaint() {
        otherPaint = new Paint();
        otherPaint.setColor(Color.RED);
        otherPaint.setStyle(Paint.Style.STROKE);

        outerPaint = new Paint();
        outerPaint.setColor(Color.RED);
        outerPaint.setStyle(Paint.Style.FILL);
    }

    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

//        if(isPinching){
//            canvas.translate(posPoint.getX() + *scale, posPoint.getY()*scale);
//        }

        canvas.translate(posPoint.getX(), posPoint.getY());

        canvas.scale(scale, scale);
        if(scale == 1) {
            setScalePhoto();
        }

        float canterLeft = (getWidth() - scaledPhoto.getWidth())/2f;
        float centerTop = (getHeight() - scaledPhoto.getHeight())/2f;

        canvas.drawBitmap(scaledPhoto, canterLeft, centerTop, null);

        drawPolygons(canvas, this.landscape);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleDetector.onTouchEvent(event);
        final int action = event.getAction();
        Point touchPoint = new Point(event.getX(), event.getY());
        int pointerIndex;

        if(scaleDetector.isInProgress()) {
            isPinching = true;
            return true;
        }


        switch (action & event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:

                wasPolygonChosen(touchPoint.getX(), touchPoint.getY());
                setVisible(pickedDetail, button);
                invalidate();
                lastPoint.setX(touchPoint.getX());
                lastPoint.setY(touchPoint.getY());
                mActivePointerId = event.getPointerId(0);
                break;

            case MotionEvent.ACTION_MOVE:
                pointerIndex = event.findPointerIndex(mActivePointerId);
                Point slidePoint = new Point(event.getX(pointerIndex), event.getY(pointerIndex));

                if (!scaleDetector.isInProgress()) {
                    isPinching = false;

                    deltaPoint.setX(slidePoint.getX() - lastPoint.getX());
                    deltaPoint.setY(slidePoint.getY() - lastPoint.getY());

                    posPoint.setX(posPoint.getX() + deltaPoint.getX());
                    posPoint.setY(posPoint.getY() + deltaPoint.getY());

                    invalidate();
                }
                lastPoint.setX(slidePoint.getX());
                lastPoint.setY(slidePoint.getY());


                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = event.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    lastPoint.setX(event.getX(newPointerIndex));
                    lastPoint.setY(event.getY(newPointerIndex));
                    mActivePointerId = event.getPointerId(newPointerIndex);
                }
                break;
        }
        return true;
    }

    private void setVisible(ImageView pickedDetail, Button button) {
        int index = 0;
//        Polygon poly;
        for (Polygon pickedPolygon : polygons) {
            if(pickedPolygon.isPicked()){
                index++;
                textView5.setText(pickedPolygon.getRusName());
//                poly = pickedPolygon;
            }
        }
        if(index > 0){
            pickedDetail.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }else{
            pickedDetail.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }

    }

    public void wasPolygonChosen(float x, float y) {
        for(Polygon polygon: polygons){
            polygon.setPicked(isPointInPolygon(polygon, new Point(x, y)));
        }
    }

    private boolean isPointInPolygon(Polygon polygon, Point point) {
        float x = point.getX();
        float y = point.getY();

        float plusWidth = getWidth() > scaledPhoto.getWidth() ? (getWidth() - scaledPhoto.getWidth()) / 2f : 0;
        float plusHeight = getHeight() > scaledPhoto.getHeight() ? (getHeight() - scaledPhoto.getHeight()) / 2f : 0;

        boolean inside = false;
        ArrayList<Point> originalPoints = (ArrayList<Point>) polygon.getPoints();

        ArrayList<Point> transformedPoints = new ArrayList<>();
        for (Point p : originalPoints) {
            float transformedX = (p.getX() * scaledPhoto.getWidth() + plusWidth) * scale + posPoint.getX() ;
            float transformedY = (p.getY() * scaledPhoto.getHeight() + plusHeight) * scale + posPoint.getY() ;
            transformedPoints.add(new Point(transformedX, transformedY));
        }

        int i, j;

        for (i = 0, j = transformedPoints.size() - 1; i < transformedPoints.size(); j = i++) {
            Point pi = transformedPoints.get(i);
            Point pj = transformedPoints.get(j);

            if (((pi.getY() > y) != (pj.getY() > y)) &&
                    (x < (pj.getX() - pi.getX()) * (y - pi.getY()) /
                            (pj.getY() - pi.getY()) + pi.getX())) {
                inside = !inside;
            }
        }
        return inside;
    }

    private void drawPolygons(Canvas canvas, boolean landscape){
        for(Polygon poly: polygons){
            if(poly.isVisible() && poly.getPoints().size() > 2) {

                float plusWidth = getWidth() > scaledPhoto.getWidth() ? (getWidth() - scaledPhoto.getWidth()) / 2f : 0;
                float plusHeights = getHeight() > scaledPhoto.getHeight() ? (getHeight() - scaledPhoto.getHeight()) / 2f : 0;

                Path path = new Path();
                ArrayList<Point> points = (ArrayList<Point>) poly.getPoints();

                path.moveTo((points.get(0).getX() * scaledPhoto.getWidth()) + plusWidth,
                        (points.get(0).getY() * scaledPhoto.getHeight()) + plusHeights);

                for (Point point : points) {
                    path.lineTo(point.getX() * scaledPhoto.getWidth() + plusWidth,
                            point.getY() * scaledPhoto.getHeight() + plusHeights);
                }

                outerPaint.setColor(Color.parseColor(poly.getColor()));
                int opacity = poly.isPicked() ? 255 : 100;
                outerPaint.setAlpha(opacity);
                canvas.drawPath(path, outerPaint);
            }else{
                break;
            }
        }
    }

    private void setScalePhoto(){
        Matrix matrix = new Matrix();
        int x = 0, y = 0,
                height = originalPhoto.getHeight(),
                width = originalPhoto.getWidth();

        float scalePhoto = Math.min((float) getWidth() / originalPhoto.getWidth(),
                               (float) getHeight() / originalPhoto.getHeight());

        matrix.setScale(scalePhoto, scalePhoto);

        scaledPhoto = Bitmap.createBitmap(originalPhoto, x, y, width, height, matrix, true);
    }

    private Bitmap base64ToBitmap(String base64Image) {
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{
        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            float previousScale = scale;

            scale *= detector.getScaleFactor();

            scale = Math.max(1f, Math.min(scale, 5.0f));

            Point slidePoint = new Point(detector.getFocusX(), detector.getFocusY());

            float deltaX = (slidePoint.getX() - posPoint.getX()) * (scale / previousScale - 1);
            float deltaY = (slidePoint.getY() - posPoint.getY()) * (scale / previousScale - 1);

            posPoint.setX(posPoint.getX() - deltaX);
            posPoint.setY(posPoint.getY() - deltaY);

            invalidate();
            return true;
        }
    }
}
