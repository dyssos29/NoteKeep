package com.example.notetakingapp;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface NoteApi
{
    static final String BASE_URL = "https://city-201617.appspot.com/_ah/api/notes/v1/";
    static final String NOTES_URL = "note";

    @GET(NOTES_URL)
    Call<ListResponse> getAllNotes();

    @POST(NOTES_URL)
    Call<Note> createNote(@Body Note aNote);

    @DELETE(NOTES_URL + "/{webSafeKey}")
    Call<Note> deleteNote(@Path("webSafeKey") String webSafeKey);
}
