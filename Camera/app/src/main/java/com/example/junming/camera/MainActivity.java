package com.example.junming.camera;

import android.content.Intent;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private ImageView mPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPicture = (ImageView) findViewById(R.id.mImageView);
    }
    public void takePhoto(View view){
//        Toast toast = Toast.makeText(this, "button pressed", Toast.LENGTH_LONG);
//        toast.show();
//        Toast.makeText(this, "camera button pressed", Toast.LENGTH_SHORT).show();
        Intent callCameraApplicationIntent = new Intent();
        callCameraApplicationIntent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(callCameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK){
//            Toast.makeText(this, "picture taken", Toast.LENGTH_SHORT).show();

        }
    }
}