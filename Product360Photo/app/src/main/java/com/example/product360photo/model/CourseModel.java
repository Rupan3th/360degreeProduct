package com.example.product360photo.model;

import android.graphics.Bitmap;

public class CourseModel {
    // string course_name for storing course_name
    // and imgid for storing image id.
    private String course_name;
    private Bitmap imgid;

    public CourseModel(String course_name, Bitmap imgid) {
        this.course_name = course_name;
        this.imgid = imgid;
    }

    public String getCourse_name() {
        return course_name;
    }

    public void setCourse_name(String course_name) {
        this.course_name = course_name;
    }

    public Bitmap getImgid() {
        return imgid;
    }

    public void setImgid(Bitmap  imgid) {
        this.imgid = imgid;
    }
}
