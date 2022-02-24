package com.example.product360photo;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.LinearLayout;
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
    private LinearLayout productNameSet;
    private EditText productName;
    private Button save_btn;

    int progress=0;
    int dir_size = 0;
    private String imageFolder="";
    private String product_type="";
    private String path = "";

    private String yolo_m01 = "";
    private String yolo_m02 = "";

    private int crop_width =1920;
    private int crop_height = 1080;

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

        if(product_type.equals("vehicle"))  {
            yolo_m01 = "car"; yolo_m02 = "truck";
            crop_width = GlobalConst.Crop_Width;
            crop_height = GlobalConst.Crop_Height;
        }
        if(product_type.equals("product"))  {
            yolo_m01 = "cup"; yolo_m02 = "mouse";
            crop_width = GlobalConst.Resize_Width;
            crop_height = GlobalConst.Resize_Height;
        }

        progressBar = findViewById(R.id.progress_bar);
        progressText = findViewById(R.id.progress_text);

        progressText.setText("Progressing...");

        productNameSet = findViewById(R.id.productName_set);
        productName = findViewById(R.id.product_Name);
        productName.setText(imageFolder);
        save_btn = findViewById(R.id.save_btn);
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!productName.getText().equals("") && !productName.getText().equals(imageFolder)){
                    File oldFolder = new File(GlobalConst.home_path, imageFolder);
                    File newFolder = new File(GlobalConst.home_path, productName.getText().toString());
                    boolean success = oldFolder.renameTo(newFolder);
                    if(success == true)
                    {
                        imageFolder = productName.getText().toString();
                        Intent intent = new Intent(YoloDetectActivity.this, ProductViewActivity.class);
                        intent.putExtra("ImageFolder", imageFolder);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        new AlertDialog.Builder(YoloDetectActivity.this)
                                .setMessage("Please enter another name.")
                                .show();
                    }
                }
            }
        });

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

    private void renameFolder(View view){
        if(!productName.getText().equals("") && !productName.getText().equals(imageFolder)){
            File oldFolder = new File(GlobalConst.home_path,imageFolder);
            File newFolder = new File(GlobalConst.home_path,productName.getText().toString());
            boolean success = oldFolder.renameTo(newFolder);
            if(success == true)
            {
                Intent intent = new Intent(this, ProductViewActivity.class);
                intent.putExtra("ImageFolder", imageFolder);
                startActivity(intent);
                finish();
            }
        }

    }


    private void YoloDetectCrop(){
        path = GlobalConst.home_path + File.separator + imageFolder;
        File dir = new File(path);
        dir_size = dir.listFiles().length;
        if(dir.exists()){
            for (int i = 0; i < dir.listFiles().length; i++) {
                Bitmap bitmap = BitmapFactory.decodeFile(path + "/" + dir.listFiles()[i].getName());
                Bitmap detectImage = detectAndDraw(bitmap);
                if(detectImage == null) continue;
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

//                            if(pro == 100){
//                                progressBar.setVisibility(View.INVISIBLE);
//                                progressText.setVisibility(View.INVISIBLE);
//                                productNameSet.setVisibility(View.VISIBLE);
//                            }
                        }
                    });


                } catch (FileNotFoundException e) {

                } catch (IOException e) {

                }

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    progressText.setVisibility(View.INVISIBLE);
                    productNameSet.setVisibility(View.VISIBLE);
                }
            });
//            Intent intent = new Intent(this, ProductViewActivity.class);
//            intent.putExtra("ImageFolder", imageFolder);
//            startActivity(intent);
//            finish();
        }

    }

    protected Bitmap drawBoxRects(Bitmap mutableBitmap, Box[] results) {
        if (results == null || results.length <= 0) {
            return mutableBitmap;
        }

        Bitmap res = mutableBitmap;
        for (Box box : results) {
            if(box.getLabel() == yolo_m01 || box.getLabel() == yolo_m02)
            {

                int center_x = (int)(box.x0 + box.x1)/2;
                int center_y = (int)(box.y0 + box.y1)/2;
                int left = Math.max(0, center_x - crop_width/2);
                int top = Math.max(0, center_y - crop_height /2);
                res = Bitmap.createBitmap(mutableBitmap, left, top, crop_width, crop_height);

            }

        }
        return res;
    }

    protected boolean checkResult(Box[] results)
    {
        boolean res = false;
        if (results == null || results.length <= 0) {
            return res;
        }
        for (Box box : results) {
            if(box.getLabel() == yolo_m01 || box.getLabel() == yolo_m02)
            {
                res = true;
            }
        }
        return res;
    }
    protected Bitmap detectAndDraw(Bitmap image) {
        Box[] result = null;
        result = YOLOv4.detect(image, threshold, nms_threshold);
        if (result == null ) {
            return null;
        }
        if(!checkResult(result))
            return null;
        Bitmap mutableBitmap = drawBoxRects(image, result);
        if(crop_width != GlobalConst.Resize_Width)
        {
            Bitmap newBitmap = Bitmap.createScaledBitmap(mutableBitmap, GlobalConst.Resize_Width,
                    GlobalConst.Resize_Height, true);
            return newBitmap;
        }
        return mutableBitmap;
    }
}