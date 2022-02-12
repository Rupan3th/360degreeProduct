package com.example.product360photo;


import android.content.res.AssetManager;
import android.graphics.Bitmap;

import com.example.product360photo.model.Box;

public class YOLOv4 {
    static {
        System.loadLibrary("yolov4");  // yolov4.so
    }

    public static native void init(AssetManager manager, boolean useGPU);
    public static native Box[] detect(Bitmap bitmap, double threshold, double nms_threshold);
}
