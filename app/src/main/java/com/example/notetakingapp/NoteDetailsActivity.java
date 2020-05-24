package com.example.notetakingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoteDetailsActivity extends AppCompatActivity
{
    private static final String EXTRA_NOTE_CREATION_DATE = "note_creation_date";
    private static final String EXTRA_NOTE_CONTENTS = "note_contents";
    private static final String EXTRA_NOTE_WEB_SAFE_KEY = "note_web_safe_key";

    private String noteSafeKey;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Deleting data on the server...");

        Intent intent = getIntent();
        String creationDate = intent.getStringExtra(EXTRA_NOTE_CREATION_DATE);
        String noteContents = intent.getStringExtra(EXTRA_NOTE_CONTENTS);
        noteSafeKey = intent.getStringExtra(EXTRA_NOTE_WEB_SAFE_KEY);

        ((TextView)findViewById(R.id.detailsDate)).setText(creationDate);
        ((TextView)findViewById(R.id.detailsNoteContents)).setText(noteContents);

        findViewById(R.id.deleteButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNoteFromServer();

            }
        });
    }

    public static Intent getIntent(Context context, String creationDate, String noteContents, String webSafeKey)
    {
        Intent intent = new Intent(context, NoteDetailsActivity.class);
        intent.putExtra(EXTRA_NOTE_CREATION_DATE, creationDate);
        intent.putExtra(EXTRA_NOTE_CONTENTS, noteContents);
        intent.putExtra(EXTRA_NOTE_WEB_SAFE_KEY,webSafeKey);

        return intent;
    }

    private void deleteNoteFromServer()
    {
        progressDialog.show();

        Retrofit retrofit = RetrofitClient.getClient(NoteApi.BASE_URL);
        NoteApi noteApi = retrofit.create(NoteApi.class);
        noteApi.deleteNote(noteSafeKey).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful())
                {
                    Note noteDeleted = response.body();
                    Intent resultIntent = new Intent();
                    setResult(RESULT_OK,resultIntent);
                    finish();
                    showToast("Note deleted successfully with id: " + noteDeleted.getId() + " and web safe key: " + noteDeleted.getWebSafeKey());
                }
                else
                    showToast("Error deleting the data with code: " + response.code());

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                showToast("Failure message: " + t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void showToast(String msgString)
    {
        Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
    }
}
