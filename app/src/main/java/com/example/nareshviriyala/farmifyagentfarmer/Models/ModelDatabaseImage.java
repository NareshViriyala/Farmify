package com.example.nareshviriyala.farmifyagentfarmer.Models;

import java.sql.Blob;

public class ModelDatabaseImage {

    private int id;
    private byte[] image;

    public ModelDatabaseImage(int id, byte[] image) {
        this.id = id;
        this.image = image;
    }


    public int getId() {
        return id;
    }

    public byte[] getImageBlob() {
        return image;
    }

}

