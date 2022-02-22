package com.example.product360photo;

import android.os.Environment;

import java.io.File;

public final class GlobalConst {

    public static final  String home_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "product360";

    public static final int Crop_Width = 2880;
    public static final int Crop_Height = 1620;
    public static final int Resize_Width = 1920;
    public static final int Resize_Height = 1080;
}
