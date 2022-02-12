package com.example.product360photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Size;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
//    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    private ImageView imageView;
    private TextView angles;
    private TextView state;
    private Button button;
    private PreviewView previewView;
    private Executor executor;
    private int Flag = 0;
    private int count = 0;
    private float cur_orientation;
    private float first_orientation;
    private ImageCapture imageCapture;
    private View view;

    private float[] floatGravity = new float[3];
    private float[] floatGeoMagnetic = new float[3];

    private float[] floatOrientation = new float[3];
    private float[] floatRotationMatrix = new float[9];

    private String imageFolder="";
    private String path = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_user);

        imageView = findViewById(R.id.imageView);
        angles = findViewById(R.id.angles);
        state = findViewById(R.id.state);
        previewView = findViewById(R.id.previewView);
        button = findViewById(R.id.button);
        ImageView btnBack = findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageFolder.equals(""))   gotoHome();
                else gotoProductView();
            }
        });


        if (checkPermission()) {
            cameraProviderFuture = ProcessCameraProvider.getInstance(this);

            cameraProviderFuture.addListener(() -> {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindPreview(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    // No errors need to be handled for this Future.
                    // This should never be reached.
                }
            }, ContextCompat.getMainExecutor(this));

        } else {
            requestPermission();
        }

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sensor sensorMagneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        SensorEventListener sensorEventListenerAccelrometer = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGravity = event.values;

                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

//                imageView.setRotation((float) (-floatOrientation[0]*180/3.14159));
//                angles.setText( String.valueOf((float) (-floatOrientation[0])));
//
//                if(Flag == 1){
//                    if(count < 1){
//                        CaptureImage(view);
//                        cur_orientation = -floatOrientation[0];
//                    }else{
//                        if(cur_orientation >= 3.0 && -floatOrientation[0] < 0)  {
//                            CaptureImage(view);
//                            cur_orientation = -floatOrientation[0];
//                        }
//                        if(-floatOrientation[0] > cur_orientation+0.1){
//                            CaptureImage(view);
//                            cur_orientation = -floatOrientation[0];
//                        }
//                    }
//                }
//
//                if((-floatOrientation[0]) > cur_orientation+0.01){
//                    state.setText( "Wrong direction");
//                    state.setTextColor(Color.parseColor("#ff0303"));
//                    imageView.setVisibility(View.VISIBLE);
//                }
//                else {
//                    state.setText( "Continue");
//                    state.setTextColor(Color.parseColor("#03ff31"));
//                    imageView.setVisibility(View.INVISIBLE);
//                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        SensorEventListener sensorEventListenerMagneticField = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                floatGeoMagnetic = event.values;

                SensorManager.getRotationMatrix(floatRotationMatrix, null, floatGravity, floatGeoMagnetic);
                SensorManager.getOrientation(floatRotationMatrix, floatOrientation);

//                imageView.setRotation((float) (-floatOrientation[0]*180/3.14159));
                angles.setText( String.valueOf(-floatOrientation[0]));

                if(Flag == 1){
                    if(count < 1){
                        CaptureImage(view);
                        cur_orientation = -floatOrientation[0];
                        first_orientation = cur_orientation;
                    }else{
                        if(-floatOrientation[0] > cur_orientation+0.1){
                            CaptureImage(view);
                            cur_orientation = -floatOrientation[0];

                            state.setText(R.string.state_continue);
                            state.setTextColor(Color.parseColor("#03ff31"));
                            imageView.setVisibility(View.INVISIBLE);
                        }else {
                            if(cur_orientation >= 2.9 && -floatOrientation[0] < 0)  {
                                CaptureImage(view);
                                cur_orientation = -floatOrientation[0];
                            }
                            if((-floatOrientation[0]) < cur_orientation-0.1){
                                state.setText(R.string.state_wrong);
                                state.setTextColor(Color.parseColor("#ff0303"));
                                imageView.setVisibility(View.VISIBLE);
                            }
                        }
                        if(-floatOrientation[0] > first_orientation && count > 25){
                            Flag = 0;
                            count = 0;
                            button.setText(R.string.camera_btn_rec);
                            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FDFFFF")));
                            state.setText( "");
                            imageView.setVisibility(View.INVISIBLE);
                        }
                    }

                }

//                if(Flag == 0){
//                    if((-floatOrientation[0]) > cur_orientation+0.1){
//                        cur_orientation = -floatOrientation[0];
//                        state.setText( "Continue");
//                        state.setTextColor(Color.parseColor("#03ff31"));
//                        imageView.setVisibility(View.INVISIBLE);
//                    }
//                    if((-floatOrientation[0]) < cur_orientation-0.1){
//                        cur_orientation = -floatOrientation[0];
//                        state.setText( "Wrong direction");
//                        state.setTextColor(Color.parseColor("#ff0303"));
//                        imageView.setVisibility(View.VISIBLE);
//                    }
//                }

            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        sensorManager.registerListener(sensorEventListenerAccelrometer, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(sensorEventListenerMagneticField, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL);

    }

    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            return false;
        }
        return true;
    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                PERMISSION_REQUEST_CODE);
    }

    void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder()
                        // enable the following line if RGBA output is needed.
                        //.setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                        .setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

//        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
//            @Override
//            public void analyze(@NonNull ImageProxy imageProxy) {
//                int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
//                // insert your code here.
//
//                // after done, release the ImageProxy object
//                imageProxy.close();
//            }
//        });

        imageCapture = new ImageCapture.Builder()
                        .build();

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, imageAnalysis, preview);
    }

    public void cameraStart(View view){
        if(Flag == 0) {
            Flag = 1;
            button.setText(R.string.camera_btn_stop);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFD0303")));

            imageFolder = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            path = GlobalConst.home_path + File.separator + imageFolder;
            File saveFile = new File(path);
            if(!saveFile.exists()){
                saveFile.mkdir();
            }
        }
        else {
            Flag = 0;
            count = 0;
            button.setText(R.string.camera_btn_rec);
            button.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FDFFFF")));
            state.setText( "");
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    public void CaptureImage(View view){
//        imageView.setRotation(180);
        count++;

        String product_photo = "";
        //if(count < 10) product_photo = "product_0"+count+".jpg";
        //else
        product_photo = "product_"+count+".jpg";

        ImageCapture.OutputFileOptions outputFileOptions =
                new ImageCapture.OutputFileOptions.Builder(
                        new File(path, product_photo))
                        .build();

        imageCapture.takePicture(outputFileOptions, executor,
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(ImageCapture.OutputFileResults outputFileResults) {
                        // insert your code here.
                    }
                    @Override
                    public void onError(ImageCaptureException error) {
                        // insert your code here.
                    }
                }
        );
    }

    private void gotoProductView(){
        Intent intent = new Intent(this, ProductViewActivity.class);
        intent.putExtra("ImageFolder", imageFolder);
        startActivity(intent);
        finish();
    }

    private void gotoHome(){
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
        finish();
    }

}