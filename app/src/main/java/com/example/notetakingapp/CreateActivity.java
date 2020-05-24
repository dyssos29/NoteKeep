package com.example.notetakingapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.widget.CircularProgressDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateActivity extends AppCompatActivity
{
    private EditText noteText;
    private ProgressDialog progressDialog;
    private final String EXTRA_NOTE_CONTENTS = "contents";
    private final String EXTRA_NOTE_DATE = "date";
    private final String EXTRA_NOTE_SAFE_KEY = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        setTitle("Note Creation");
        noteText = findViewById(R.id.noteText);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Sending data to the server...");

        DateFormat df = new SimpleDateFormat("dd/MM/yy");
        ((TextView)findViewById(R.id.creationDate)).setText("Creation date: " + df.format(new Date()));

        findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        findViewById(R.id.createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noteText.getText().toString().length() == 0)
                    showToast("Input text to create the note.");
                else
                {
                    sendNoteToServer();
                }
            }
        });
    }

    private void sendNoteToServer()
    {
        progressDialog.show();

        Retrofit retrofit = RetrofitClient.getClient(NoteApi.BASE_URL);
        NoteApi noteApi = retrofit.create(NoteApi.class);
        Note note = new Note(noteText.getText().toString(), new Date());

        noteApi.createNote(note).enqueue(new Callback<Note>() {
            @Override
            public void onResponse(Call<Note> call, Response<Note> response) {
                if (response.isSuccessful())
                {
                    Note noteCreated = response.body();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra(EXTRA_NOTE_CONTENTS,noteCreated.getContents());
                    resultIntent.putExtra(EXTRA_NOTE_DATE,noteCreated.getCreationDate());
                    resultIntent.putExtra(EXTRA_NOTE_SAFE_KEY,noteCreated.getWebSafeKey());
                    setResult(RESULT_OK,resultIntent);
                    finish();
                    showToast("Note created successfully with id: " + noteCreated.getId() + " and web safe key: " + noteCreated.getWebSafeKey());
                }
                else
                    showToast("Error sending the data with code: " + response.code());

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<Note> call, Throwable t) {
                showToast("Failure message: " + t.getMessage());
                System.out.println(t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    private void showToast(String msgString)
    {
        Toast.makeText(this, msgString, Toast.LENGTH_LONG).show();
    }
}
