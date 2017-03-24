package com.example.junming.cameratangle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int ACTIVITY_START_CAMERA_APP = 0;
    private ImageView mPhoto;
    private String imageDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mPhoto = (ImageView) findViewById(R.id.capturePhotoImageView);
    }

    public void takePhoto(View view){
//        Toast.makeText(this, "button pressed", Toast.LENGTH_SHORT).show();
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


}
