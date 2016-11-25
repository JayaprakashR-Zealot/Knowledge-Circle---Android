package com.truedreamz.facedetection;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private static final String TAG= "FaceDetectionActivity";

    private int mFaceWidth = 100;
    private int mFaceHeight = 100;
    private static final int MAX_FACES = 1;
    private static final int CAMERA_REQUEST = 1;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 2;
    private static final int GALLERY_PERMISSION_REQUEST_CODE = 3;
    private static final int SELECT_PICTURE  = 4;
    private FaceOverlayView imgPhoto;
    private TextView txtFaces;
    private int rotateAngle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgPhoto=(FaceOverlayView)findViewById(R.id.imgPhoto);
        txtFaces=(TextView)findViewById(R.id.txtFaces);
    }

    public void onSelfieListener(View v){
        checkCameraPermission();
    }

    public void onGalleryListener(View v){
        checkGalleryPermission();
    }


    private void checkCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MyApp", "SDK >= 23");
            if (this.checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyApp", "Request permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        CAMERA_PERMISSION_REQUEST_CODE);
            }
            else {
                Log.d("MyApp", "Permission granted: taking pic");
                openCamera();
            }
        }
        else {
            Log.d("MyApp", "Android < 6.0");
            openCamera();
        }
    }

    private void checkGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d("MyApp", "SDK >= 23");
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.d("MyApp", "Request permission");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        GALLERY_PERMISSION_REQUEST_CODE);

            }
            else {
                Log.d("MyApp", "Permission granted: taking pic");
                openGallery();
            }
        }
        else {
            Log.d("MyApp", "Android < 6.0");
            openGallery();
        }
    }

    private void openCamera(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                "Select Picture"), SELECT_PICTURE);
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            setBitmapToImageview(data);
        }
        else if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK) {
            //setBitmapToImageview(data);
            try {
                Uri selectedImage = data.getData();

                rotateAngle =0;
                rotateAngle %= 360;

                Bitmap bitmap=getBitmapFromUri(selectedImage);
                int mDetectedFaces=imgPhoto.setBitmap(rotate(bitmap));

                //txtFaces.setText(String.valueOf(mDetectedFaces));
                if(mDetectedFaces==0){
                    blinkMultiplefaces(txtFaces,"Error : No face detected.");
                }
                else if(mDetectedFaces==1){
                    txtFaces.clearAnimation();
                    txtFaces.setTextColor(Color.parseColor("#149414"));
                    txtFaces.setText("Ready to generate 3D avatar.");
                }
                else if(mDetectedFaces>1){
                    blinkMultiplefaces(txtFaces,"Error : More than 1 face detected.");
                }
            }catch (IOException ex){
                Log.e(TAG,"IOException:"+ex.getMessage());
            }
        }
    }

    public Bitmap rotate(Bitmap paramBitmap)
    {
        if (rotateAngle% 360 == 0) {
            return paramBitmap;
        }
        Matrix localMatrix = new Matrix();
        float f1 = paramBitmap.getWidth() / 2;
        float f2 = paramBitmap.getHeight() / 2;
        localMatrix.postTranslate(-paramBitmap.getWidth() / 2, -paramBitmap.getHeight() / 2);
        localMatrix.postRotate(rotateAngle);
        localMatrix.postTranslate(f1, f2);
        paramBitmap = Bitmap.createBitmap(paramBitmap, 0, 0, paramBitmap.getWidth(), paramBitmap.getHeight(), localMatrix, true);
        new Canvas(paramBitmap).drawBitmap(paramBitmap, 0.0F, 0.0F, null);
        return paramBitmap;
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        /*ExifInterface exif = new ExifInterface(uri.getPath());
        int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_ROTATE_90);
        int rotationInDegrees = exifToDegrees(rotation);
        Matrix matrix = new Matrix();
        if (rotation != 0f) {
            matrix.preRotate(rotationInDegrees);
        }*/

        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        //Bitmap adjustedBitmap = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        parcelFileDescriptor.close();
        return image;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) { return 90; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {  return 180; }
        else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {  return 270; }
        return 0;
    }

    private void setBitmapToImageview(Intent data){
        Bitmap photo = (Bitmap) data.getExtras().get("data");
        int mDetectedFaces=imgPhoto.setBitmap(photo);

        if(mDetectedFaces==0){
            blinkMultiplefaces(txtFaces,"Error : No face detected.");
        }
        else if(mDetectedFaces==1){
            txtFaces.clearAnimation();
            txtFaces.setTextColor(Color.parseColor("#149414"));
            txtFaces.setText("Ready to generate 3D avatar.");
        }
        else if(mDetectedFaces>1){
            blinkMultiplefaces(txtFaces,"Error : More than 1 face detected.");
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //checkCameraPermission();
                    Toast.makeText(this, "Camera permission granted.", Toast.LENGTH_SHORT).show();
                    openCamera();
                } else {
                    Toast.makeText(this, "You did not allow camera usage :(", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            case GALLERY_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //checkCameraPermission();
                    Toast.makeText(this, "Gallery permission granted.", Toast.LENGTH_SHORT).show();
                    openGallery();
                } else {
                    Toast.makeText(this, "You did not allow gallery usage :(", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void blinkMultiplefaces(TextView txtFaces,String text){
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(50); //You can manage the blinking time with this parameter
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        txtFaces.startAnimation(anim);
        txtFaces.setTextColor(Color.parseColor("#FF0000"));
        txtFaces.setText(text);
    }

}
