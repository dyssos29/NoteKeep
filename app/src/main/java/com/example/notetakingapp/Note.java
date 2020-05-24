package com.example.notetakingapp;

import com.google.gson.annotations.SerializedName;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Note
{
    @SerializedName("content")
    private String contents;
    @SerializedName("dateInMillis")
    private long creationDate;
    @SerializedName("websafeKey")
    private String webSafeKey;
    private int id;
    private transient DateFormat df = new SimpleDateFormat("dd/MM/yy - HH:mm");

    public Note()
    {
        df = new SimpleDateFormat("dd/MM/yy - HH:mm");
    }

    public Note(String contents, Date creationDate)
    {
        df = new SimpleDateFormat("dd/MM/yy - HH:mm");

        this.contents = contents;
        this.creationDate = creationDate.getTime();
    }

    public Note(String contents, String creationDate, String webSafeKey)
    {
        df = new SimpleDateFormat("dd/MM/yy - HH:mm");

        this.contents = contents;
        try {
            this.creationDate = df.parse(creationDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.webSafeKey = webSafeKey;
    }

    public String getContents()
    {
        return contents;
    }

    public String getCreationDate()
    {
        return df.format(new Date(creationDate));
    }

    public Date getDateForSorting()
    {
        return new Date(creationDate);
    }

    public int getId()
    {
        return id;
    }

    public String getWebSafeKey()
    {
        return webSafeKey;
    }
}
