package com.example.product360photo;

import android.os.Environment;

import java.io.File;

public final class GlobalConst {

    public static final  String home_path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "product360";

}
