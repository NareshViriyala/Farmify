package com.example.nareshviriyala.farmifyagentfarmer.Models;

import android.graphics.Bitmap;

public class ModelImageParameters {

    private Bitmap bitmap;
    private int height;
    private int width;
    private int leftPic;
    private int rightPic;
    private int deletePic;

    public ModelImageParameters(Bitmap bitmap, int height, int width, int leftPic, int rightPic, int deletePic){
        this.bitmap = bitmap;
        this.height = height;
        this.width = width;
        this.leftPic = leftPic;
        this.rightPic = rightPic;
        this.deletePic = deletePic;
    }

    public Bitmap getBitmap(){return this.bitmap;}
    public int getHeight() {return this.height;}
    public int getWidth() {return this.width;}
    public int getLeftPic() {return this.leftPic;}
    public int getRightPic() {return this.rightPic;}
    public int getDeletePic() {return this.deletePic;}
}
