package com.example.junming.cameratangle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.icu.text.StringPrepParseException;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private ImageView mPhoto;
    private String imageDir;

    private static String myTag = "myTag";
    static {
        if(OpenCVLoader.initDebug()){
            Log.d(myTag, "opencv loaded");
        } else {
            Log.d(myTag, "opencv not loaded");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhoto = (ImageView) findViewById(R.id.capturePhotoImageView);
    }

    public void takePhoto(View view){

        // Intent is a message
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

        File photo = null;
        try{
            photo = createImageFile();
        } catch (IOException e){
            e.printStackTrace();
        }
        callCameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);

    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data){
        if (requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
//            Toast.makeText(this, "photo taken", Toast.LENGTH_SHORT).show();
//            Bundle extras = data.getExtras();
//            Bitmap photoCapturedBitmap = (Bitmap) extras.get("data");
//            mPhoto.setImageBitmap(photoCapturedBitmap);
            Bitmap photoCaptyredBitmap = BitmapFactory.decodeFile(imageDir);
            mPhoto.setImageBitmap(photoCaptyredBitmap);

        }
    }

    File createImageFile() throws IOException{
        String filename = "new_image";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(filename, ".jpg", storageDir);
        imageDir = image.getAbsolutePath();

        return image;
    }

    public void filters(View view){

        // read original color image from directory path
        Bitmap img = BitmapFactory.decodeFile(imageDir);
        Mat colorImage = new Mat();
        Mat grayImage = new Mat();

        // convert bitmap to opencv mat
        Utils.bitmapToMat(img, colorImage);
        // convert color image to grayscale image
        Imgproc.cvtColor(colorImage, grayImage, Imgproc.COLOR_RGB2GRAY);
        // use histogram to smooth grayscale
        Imgproc.equalizeHist(grayImage, grayImage);


        switch (view.getId()) {
            case R.id.blackWhiteFilterButton:
//                Toast.makeText(this, "black/WhiteButton clicked", Toast.LENGTH_SHORT).show();
//                Log.e("myTag", "This is my message");
//                Imgproc.threshold(grayImage, grayImage, 127, 255, Imgproc.THRESH_BINARY);

                // create bitmap for latter use
                Bitmap blackWhiteImage = Bitmap.createBitmap(grayImage.cols(), grayImage.rows(), Bitmap.Config.ARGB_8888);
                // convert opencv mat to bitmap
                Utils.matToBitmap(grayImage, blackWhiteImage);
                // assign bitmap to image view
                mPhoto.setImageBitmap(blackWhiteImage);
                break;

            case R.id.blueYellowFilterButton:
                // create a list of Mat type
                List<Mat> lMat = new ArrayList<Mat>(4);
                // split an image into four channels (r,g,b,alpha), and pass them into four Mats
                Core.split(colorImage, lMat);
                // base on grayscale image, only need to change b channel matrix (255 - value)
                lMat.set(0, grayImage);
                lMat.set(1, grayImage);

                // compute b channel value, first create 255 * ones[], then subtract gray scale value
                Scalar maximum = new Scalar(255);
                Mat max = Mat.ones(grayImage.size(), grayImage.type());
                Core.multiply(max, maximum, max);
                Mat blueChannel = new Mat();
                Core.subtract(max, grayImage, blueChannel);

//                for(int i=1; i < 500; i++){
//                    double[] intensity1 = blueChannel.get(i,i);
//                    double[] intensity2 = blueChannel.get(i,i);
//                    int tmp1 = (int) intensity1[0];
//                    int tmp2 = (int) intensity2[0];
//                    Log.d(myTag, Integer.toString(tmp1)+"#");
//                    Log.d(myTag, Integer.toString(tmp2));
//                }

                lMat.set(2, blueChannel);
                Mat blueYellowMat = new Mat();
                // merge four-channel mat list into a big mat
                Core.merge(lMat, blueYellowMat);

                Bitmap blueYellowImage = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(blueYellowMat, blueYellowImage);
                mPhoto.setImageBitmap(blueYellowImage);
                break;

            case R.id.edgeFilterButton:
//                Imgproc.threshold(grayImage, grayImage, 127, 255, Imgproc.THRESH_BINARY);
                // use adaptive threshold method to do binary color converting
                Imgproc.adaptiveThreshold(grayImage, grayImage, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 15, 2);
                Bitmap edgeImage = Bitmap.createBitmap(grayImage.cols(), grayImage.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(grayImage, edgeImage);
                mPhoto.setImageBitmap(edgeImage);
                break;

            case R.id.originalImageButton:
                mPhoto.setImageBitmap(img);
                break;

        }
    }



}
