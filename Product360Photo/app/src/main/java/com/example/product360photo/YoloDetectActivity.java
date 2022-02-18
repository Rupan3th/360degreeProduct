package com.example.product360photo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.product360photo.model.Box;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class YoloDetectActivity extends AppCompatActivity {

    public static boolean USE_GPU = false;
    private double threshold = 0.3, nms_threshold = 0.7;

    private ProgressBar progressBar;
    private TextView progressText;
    int progress=0;
    int dir_size = 0;
    private String imageFolder="";
    private String product_type="";
    private String path = "";

    private String yolo_m01 = "";
    private String yolo_m02 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yolo_detect);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent intent = getIntent();
        imageFolder = intent.getStringExtra("ImageFolder");
        product_type = intent.getStringExtra("product_type");

        if(product_type.equals("vehicle"))  { yolo_m01 = "car"; yolo_m02 = "truck"; }
        if(product_type.equals("product"))  { yolo_m01 = "cup"; yolo_m02 = "mouse"; }

        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        progressText.setText("Progressing...");

        YOLOv4.init(getAssets(), USE_GPU);
        //YoloDetectCrop();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                progress = 0;
                YoloDetectCrop();
            }
        });
        thread.start();
    }


    private void YoloDetectCrop(){
        path = GlobalConst.home_path + File.separator + imageFolder;
        File dir = new File(path);
        dir_size = dir.listFiles().length;
        if(dir.exists()){
            for (int i = 0; i < dir.listFiles().length; i++) {
                Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + dir.listFiles()[i].getName());
                Bitmap detectImage = detectAndDraw(bitmap);
                File pictureFile = new File(path + "/" + dir.listFiles()[i].getName());
                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    detectImage.compress(Bitmap.CompressFormat.PNG, 90, fos);
                    fos.close();
                    progress++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int pro = progress * 100 / dir_size;
                            progressText.setText(pro + "%");
                            progressBar.setProgress(pro);
                        }
                    });
//                    final Handler handler = new Handler();
//                    handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            if (progress <= 100) {
//                                progressText.setText(progress + "%");
//                                progressBar.setProgress(progress);
//                                progress++;
//                                handler.postDelayed(this, 200);
//                            } else {
//                                handler.removeCallbacks(this);
//                            }
//                        }
//                    }, 0);

                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                }

            }
            finish();
        }

    }

    protected Bitmap drawBoxRects(Bitmap mutableBitmap, Box[] results) {
        if (results == null || results.length <= 0) {
            return mutableBitmap;
        }
//        Bitmap res = Bitmap.createBitmap(mutableBitmap.getWidth(), mutableBitmap.getHeight(), mutableBitmap.getConfig());
//        Canvas canvas = new Canvas(res);
//        canvas.drawBitmap(mutableBitmap, new Matrix(), null);

//        final Paint boxPaint = new Paint();
//        boxPaint.setAlpha(200);
//        boxPaint.setStyle(Paint.Style.STROKE);
//        boxPaint.setStrokeWidth(4 * mutableBitmap.getWidth() / 800.0f);
//        boxPaint.setTextSize(30 * mutableBitmap.getWidth() / 800.0f);
        Bitmap res = mutableBitmap;
        for (Box box : results) {
            if(box.getLabel() == yolo_m01 || box.getLabel() == yolo_m02)
            {
//                box.x1 = Math.min(mutableBitmap.getWidth() -1 , box.x1);
//                box.y1 = Math.min(mutableBitmap.getHeight() -1 , box.y1);
                int left = (int)box.x0;
                int top = (int)box.y0;
                int box_width = (int)box.getRect().width();
                int box_height = (int)box.getRect().height();
                int margin = 50;
                if(left-margin >= 0 && top-margin >= 0 && box_width+margin <= mutableBitmap.getWidth() && box_height <= mutableBitmap.getHeight()){
                    res = Bitmap.createBitmap(mutableBitmap, left-margin, top-margin, box_width+margin, box_height+margin);
                }else {
                    res = Bitmap.createBitmap(mutableBitmap, left, top, box_width, box_height);
                }

//                canvas.drawRect(box.getRect(), boxPaint);
            }

        }
        return res;
    }

    protected Bitmap detectAndDraw(Bitmap image) {
        Box[] result = null;
        result = YOLOv4.detect(image, threshold, nms_threshold);
        if (result == null ) {
            return image;
        }
        Bitmap mutableBitmap = drawBoxRects(image, result);
        return mutableBitmap;
    }
}