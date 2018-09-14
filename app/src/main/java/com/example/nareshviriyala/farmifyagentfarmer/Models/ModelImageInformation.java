package com.example.nareshviriyala.farmifyagentfarmer.Models;

import java.util.List;

public class ModelImageInformation {

    private int Id;
    private String ImageType;
    private byte[] ImageSource;

    public ModelImageInformation(int Id, String ImageType, byte[] ImageSource) {
        this.Id = Id;
        this.ImageType = ImageType;
        this.ImageSource = ImageSource;
    }

    public int getId() {
        return Id;
    }

    public String getImageType() {
        return ImageType;
    }

    public byte[] getImageSource() {
        return ImageSource;
    }

}
