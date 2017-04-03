package com.example.junming.customcamera;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String myTag = "myTag";
    JavaCameraView mJavaCameraView;
    private Mat mRgba, imgGray;
    private int indicator = 0;

    BaseLoaderCallback mLoaderCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case BaseLoaderCallback.SUCCESS:
                {
                    mJavaCameraView.enableView();
                    break;
                }
            default:
                {
                super.onManagerConnected(status);
                break;
                }
            }
        }
    };

    static {
        if (OpenCVLoader.initDebug()) {
            Log.d(myTag, "opencv loaded");
        } else {
            Log.d(myTag, "opencv not loaded");
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mJavaCameraView = (JavaCameraView) findViewById(R.id.JCView);
        mJavaCameraView.setVisibility(SurfaceView.VISIBLE);
        mJavaCameraView.setCvCameraViewListener(this);
        Button mChangeModeButton = (Button) findViewById(R.id.changeModeButton);

        mChangeModeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // once changeModeButton is clicked, update indicator by one
                indicator = indicator + 1;
                Log.d(myTag, Integer.toString(indicator));
            }
        });


    }

    @Override
    protected  void onPause(){
        super.onPause();
        if (mJavaCameraView != null){
            mJavaCameraView.disableView();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (mJavaCameraView != null) {
            mJavaCameraView.disableView();
        }

    }


    @Override
    protected void onResume(){
        super.onResume();
        if (OpenCVLoader.initDebug()) {
            Log.d(myTag, "opencv loaded");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);

        } else {
            Log.d(myTag, "opencv not loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallBack);
        }
    }


    @Override
    public void onCameraViewStarted(int width, int height) {
        // initialize two mat variables
        mRgba = new Mat(height, width, CvType.CV_8UC4);
        imgGray = new Mat(height, width, CvType.CV_8UC1);

    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }



    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        // read input image from original cameraViewFrame
        mRgba = inputFrame.rgba();
        // use equalized histogram method to convert color image to grayscale image
        Imgproc.cvtColor(mRgba, imgGray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.equalizeHist(imgGray, imgGray);
        // create a list of Mat type
        List<Mat> lMat = new ArrayList<Mat>(4);
        // split an image into four channels (r,g,b,alpha), and pass them into four Mats
        Core.split(mRgba, lMat);

        switch (indicator % 3) {
            // display gray scale image
            case (1):
                return imgGray;

            // display blue/yellow image
            case (2):
                // base on grayscale image, only need to change b channel matrix (255 - value)
                lMat.set(0, imgGray);
                lMat.set(1, imgGray);

                // compute b channel value, first create 255 * ones[], then subtract gray scale value
                Scalar maximum = new Scalar(255);
                Mat max = Mat.ones(imgGray.size(), imgGray.type());
                Core.multiply(max, maximum, max);
                Mat blueChannel = new Mat();
                Core.subtract(max, imgGray, blueChannel);

                lMat.set(2, blueChannel);
                Mat blueYellowMat = new Mat();
                // merge four-channel mat list into a big mat
                Core.merge(lMat, blueYellowMat);

                return blueYellowMat;

            default:
                // display original color image
                return mRgba;

        }


    }



}
