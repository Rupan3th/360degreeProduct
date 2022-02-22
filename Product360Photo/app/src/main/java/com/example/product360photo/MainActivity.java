package com.example.product360photo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.product360photo.Adapter.CourseGVAdapter;
import com.example.product360photo.model.CourseModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    final String TAG = this.getClass().getSimpleName();

    LinearLayout home_ly;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        String title = "360 Product Photo";
        SpannableString s = new SpannableString(title);
        s.setSpan(new ForegroundColorSpan(Color.BLACK), 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ActionBar actionBar = getSupportActionBar();
//        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#4E68F4"));
//        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setTitle(s);


        init(); //object define
        SettingListener(); //Listener regist

        //First start Tab select
        bottomNavigationView.setSelectedItemId(R.id.home);
    }

    @Override
    public void onStart() {
        super.onStart();

        File dir = new File(GlobalConst.home_path);
        if(!dir.exists()){
            dir.mkdir();
        }

    }



    private void init() {
        home_ly = findViewById(R.id.home_ly);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.home_ly, new HomeFragment())
                .commit();
    }

    private void SettingListener() {
        //select Listener regist
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener{
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new HomeFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_camera: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new CameraFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_user: {
                    /*getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new UserFragment())
                            .commit();*/
                    Intent intent = new Intent(MainActivity.this, YolotestActivity.class);

                    startActivity(intent);


                    return true;
                }
                case R.id.tab_settings: {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.home_ly, new SettingsFragment())
                            .commit();
                    return true;
                }
            }

            return false;
        }
    }
}