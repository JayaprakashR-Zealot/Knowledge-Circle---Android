package com.truedreamz.facedetection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by augray on 9/14/2016.
 */
public class FaceOverlayView extends View {

    private Bitmap mBitmap;
    SparseArray<Face> mFaces=new SparseArray<Face>();

    public FaceOverlayView(Context context) {
        this(context, null);
    }

    public FaceOverlayView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FaceOverlayView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*public interface AsyncResponse {
        void processFinish(Integer output);
    }*/

    public int setBitmap( Bitmap bitmap ) {
        mBitmap = bitmap;

        FaceDetector detector = new FaceDetector.Builder( getContext() )
                .setTrackingEnabled(false)
                //.setLandmarkType(FaceDetector.ALL_LANDMARKS)
                //.setMode(FaceDetector.FAST_MODE)
                .build();
        if (!detector.isOperational()) {
            //Handle contingency
            Toast.makeText(getContext(), "Face detection error.", Toast.LENGTH_LONG).show();
        } else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            mFaces = detector.detect(frame);
            detector.release();
        }
        invalidate();

        return mFaces.size();
    }


    /*private class detectFaceTask extends AsyncTask<Void,Void,Integer>{
        Context cxt;
        Bitmap faceBitmap;
        public AsyncResponse delegate = null;
        detectFaceTask(Context cxt,Bitmap faceBitmap){
            this.cxt=cxt;
            this.faceBitmap=faceBitmap;
        }

        @Override
        protected Integer doInBackground(Void... params) {
            FaceDetector detector = new FaceDetector.Builder(cxt)
                    .setTrackingEnabled(false)
                            //.setLandmarkType(FaceDetector.ALL_LANDMARKS)
                            //.setMode(FaceDetector.FAST_MODE)
                    .build();
            if (!detector.isOperational()) {
                //Handle contingency
                //Toast.makeText(cxt, "Face detection error.", Toast.LENGTH_LONG).show();
                Log.d("","Face detection error. ");
            } else {
                Frame frame = new Frame.Builder().setBitmap(faceBitmap).build();
                mFaces = detector.detect(frame);
                detector.release();
            }

            return mFaces.size();
        }

       *//* @Override
        protected void onPostExecute(int result) {
            super.onPostExecute(result);
            invalidate();
            delegate.processFinish(result);
        }*//*

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            invalidate();
            delegate.processFinish(result);
        }
    }*/



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ((mBitmap != null) && (mFaces != null)) {
            double scale = drawBitmap(canvas);
            drawFaceBox(canvas, scale);
        }
    }

    private double drawBitmap( Canvas canvas ) {
        double viewWidth = canvas.getWidth();
        double viewHeight = canvas.getHeight();
        double imageWidth = mBitmap.getWidth();
        double imageHeight = mBitmap.getHeight();
        double scale = Math.min( viewWidth / imageWidth, viewHeight / imageHeight );

        Rect destBounds = new Rect( 0, 0, (int) ( imageWidth * scale ), (int) ( imageHeight * scale ) );
        canvas.drawBitmap(mBitmap, null, destBounds, null);
        return scale;
    }

    private void drawFaceBox(Canvas canvas, double scale) {
        //paint should be defined as a member variable rather than
        //being created on each onDraw request, but left here for
        //emphasis.
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        float left = 0;
        float top = 0;
        float right = 0;
        float bottom = 0;

        for( int i = 0; i < mFaces.size(); i++ ) {
            Face face = mFaces.valueAt(i);

            left = (float) ( face.getPosition().x * scale );
            top = (float) ( face.getPosition().y * scale );
            right = (float) scale * ( face.getPosition().x + face.getWidth() );
            bottom = (float) scale * ( face.getPosition().y + face.getHeight() );

            canvas.drawRect( left, top, right, bottom, paint );
        }
    }
}
