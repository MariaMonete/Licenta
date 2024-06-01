package com.example.incercarelicenta.clase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Parfum implements Parcelable {
    private String name;
    private String brand;
    private String description;
    private String imgUrl;
    private List<String> notes;
    private int id;
    private String parfumId;

    public Parfum() {
    }

    public Parfum(String brand, String name) {
        this.name = name;
        this.brand = brand;
        notes = new ArrayList<>();
    }

    protected Parfum(Parcel in) {
        name = in.readString();
        brand = in.readString();
        description = in.readString();
        imgUrl = in.readString();
        notes = in.createStringArrayList();
        id = in.readInt();
        parfumId = in.readString();
    }

    public static final Creator<Parfum> CREATOR = new Creator<Parfum>() {
        @Override
        public Parfum createFromParcel(Parcel in) {
            return new Parfum(in);
        }

        @Override
        public Parfum[] newArray(int size) {
            return new Parfum[size];
        }
    };

    @Override
    public String toString() {
        return "Perfume{" +
                "name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", notes=" + notes +
                ", description='" + description + '\'' +
                ", imageUrl='" + imgUrl + '\'' +
                ", id='" + id + '\'' +
                ", parfumId='" + parfumId + '\'' +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public List<String> getNotes() {
        return notes;
    }

    public void setNotes(List<String> notes) {
        this.notes = notes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getParfumId() {
        return parfumId;
    }

    public void setParfumId(String parfumId) {
        this.parfumId = parfumId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(brand);
        parcel.writeString(description);
        parcel.writeString(imgUrl);
        parcel.writeStringList(notes);
        parcel.writeInt(id);
        parcel.writeString(parfumId);
    }
}

