package com.example.notetakingapp;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

class ListResponse
{
    @SerializedName("items")
    private ArrayList<Note> notes = new ArrayList<>(0);

    public ListResponse() {
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }
}
