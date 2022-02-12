package com.example.product360photo;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.product360photo.model.Box;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class YolotestActivity extends AppCompatActivity {

    public static boolean USE_GPU = false;
    private double threshold = 0.3, nms_threshold = 0.7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yolotest);
        Intent intent = getIntent();
        String imageFolder = intent.getStringExtra("ImageFolder");

        initModel();
        ImageView result_img = (ImageView) findViewById(R.id.imageView);
        Bitmap img = getBitmapFromAsset(this, "test1.jpg");
        result_img.setImageBitmap(detectAndDraw(img));
    }

    protected void initModel() {
        YOLOv4.init(getAssets(), USE_GPU);

    }
    public static Bitmap getBitmapFromAsset(Context context, String filePath) {
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            // handle exception
        }

        return bitmap;
    }
    protected Bitmap drawBoxRects(Bitmap mutableBitmap, Box[] results) {
        if (results == null || results.length <= 0) {
            return mutableBitmap;
        }
        Bitmap res = Bitmap.createBitmap(mutableBitmap.getWidth(), mutableBitmap.getHeight(), mutableBitmap.getConfig());
        Canvas canvas = new Canvas(res);
        canvas.drawBitmap(mutableBitmap, new Matrix(), null);

        final Paint boxPaint = new Paint();
        boxPaint.setAlpha(200);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(4 * mutableBitmap.getWidth() / 800.0f);
        boxPaint.setTextSize(30 * mutableBitmap.getWidth() / 800.0f);
        for (Box box : results) {
            box.x1 = Math.min(mutableBitmap.getWidth() -1 , box.x1);
            box.y1 = Math.min(mutableBitmap.getHeight() -1 , box.y1);
            boxPaint.setColor(box.getColor());
            boxPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(box.getLabel() + String.format(Locale.ENGLISH, " %.3f", box.getScore()), box.x0 + 3, box.y0 + 30 * mutableBitmap.getWidth() / 1000.0f, boxPaint);
            boxPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(box.getRect(), boxPaint);
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
