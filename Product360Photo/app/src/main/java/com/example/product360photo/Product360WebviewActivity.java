package com.example.product360photo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class Product360WebviewActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product360webview);
        Intent intent = getIntent();
        String imageFolder = intent.getStringExtra("ImageFolder");

        ProductShowCaseWebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);

        ProductShowCaseWebView wv = (ProductShowCaseWebView) findViewById(R.id.web_view);

        String imagesTag360="";

        /*Taking images from storage folder*/

        String SdCard = Environment.getExternalStorageDirectory().getAbsolutePath().toString();

        String saved_path = GlobalConst.home_path + File.separator + imageFolder;
        File dir = new File(saved_path);
        if(dir.exists()){
            for (int i = 0; i < dir.listFiles().length; i++) {
                String filepath = saved_path + "/" + dir.listFiles()[i].getName();
                imagesTag360=imagesTag360+"<img src=\"file://"+ filepath + "\"/>" ;
            }

            //Log.d("",imagesTag360);

            String folder_url = saved_path + "/";
            int img_cnt = dir.listFiles().length;
            String data = "<head>" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "<div\n" +
                    "class=\"cloudimage-360\"\n" +
                    "data-folder=\"file://" + folder_url +"\"\n" +
                    "data-filename=\"product_{index}.jpg\"\n" +
                    "data-amount=\""+ img_cnt +"\"\n" +
                    "data-magnifier=\"3\"\n" +
                    "data-spin-reverse\n" +
                    "data-full-screen\n" +
                    "></div>" +
                    "\n" +
                    "<script type=\"text/javascript\" src=\"file:///android_asset/js/jscloud360.js\"></script>\n" +
                    "</body>";

            wv.loadDataWithBaseURL("",
                    data, "text/html", "UTF-8", null);

        }


        ImageView btnBack = (ImageView) findViewById(R.id.back_btn);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Product360WebviewActivity.this, MainActivity.class);
                startActivity(intent);

            }
        });
    }
}
