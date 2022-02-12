package com.example.product360photo;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.product360photo.Adapter.CourseGVAdapter;
import com.example.product360photo.model.CourseModel;

import java.io.File;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

//    private Context mContext;
    private GridView coursesGV;
    private ArrayList<CourseModel> courseModelArrayList = new ArrayList<CourseModel>();
    private CourseGVAdapter adapter;


    public HomeFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mContext = context;
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mContext = null;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        coursesGV = view.findViewById(R.id.idGV_courses);

        CreateModelArrayList();

//        courseModelArrayList.add(new CourseModel("DSA", R.drawable.ic_home));
//        courseModelArrayList.add(new CourseModel("JAVA", R.drawable.ic_home));
//        courseModelArrayList.add(new CourseModel("C++", R.drawable.ic_home));
//        courseModelArrayList.add(new CourseModel("Python", R.drawable.ic_home));
//        courseModelArrayList.add(new CourseModel("Javascript", R.drawable.ic_home));
//        courseModelArrayList.add(new CourseModel("DSA", R.drawable.ic_home));

        if(mContext != null) {
            //your code that uses Context
            adapter = new CourseGVAdapter(mContext, courseModelArrayList);
            coursesGV.setAdapter(adapter);
            coursesGV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String itemFolder = ((TextView)view.findViewById(R.id.idTVCourse)).getText().toString();

                    Intent intent = new Intent(getActivity(), Product360WebviewActivity.class);
                    intent.putExtra("ImageFolder", itemFolder);
                    startActivity(intent);

                }
            });
        }

        return view;
    }

    private void CreateModelArrayList(){
        courseModelArrayList.clear();

        File dir = new File(GlobalConst.home_path);
        if(dir.exists()){
            if(dir.listFiles() != null)
            {
                for (int i = 0; i < dir.listFiles().length; i++) {
                    Bitmap bitmap = BitmapFactory.decodeFile(GlobalConst.home_path + "/" + dir.listFiles()[i].getName() + "/product_1.jpg");
                    courseModelArrayList.add(new CourseModel(dir.listFiles()[i].getName(), bitmap));

                }
            }

        }
    }


}