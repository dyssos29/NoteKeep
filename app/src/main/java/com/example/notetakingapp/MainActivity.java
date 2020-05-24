package com.example.notetakingapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
{
    private ListView listView;
    private NoteListAdapter adapter;
    private ArrayList<Note> notes;
    private int listIndex;
    private Context context;
    private ProgressDialog progressDialog;

    private final int CREATE_REQUEST_CODE = 1;
    private final int DELETE_REQUEST_CODE = 2;
    private final String EXTRA_NOTE_CONTENTS = "contents";
    private final String EXTRA_NOTE_DATE = "date";
    private final String EXTRA_NOTE_SAFE_KEY = "key";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Wait");
        progressDialog.setMessage("Fetching data from the server...");

        listView = findViewById(R.id.listView);
        context = this;

        getNotesFromServer();

        findViewById(R.id.createButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateActivity.class);
                startActivityForResult(intent,CREATE_REQUEST_CODE);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (notes != null)
                {
                    Note aNote = notes.get(position);
                    listIndex = position;
                    startActivityForResult(NoteDetailsActivity.getIntent(MainActivity.this, aNote.getCreationDate(), aNote.getContents(), aNote.getWebSafeKey()), DELETE_REQUEST_CODE);
                }
            }
        });
    }

    private void getNotesFromServer()
    {
        progressDialog.show();

        Retrofit retrofit = RetrofitClient.getClient(NoteApi.BASE_URL);
        NoteApi noteApi = retrofit.create(NoteApi.class);
        noteApi.getAllNotes().enqueue(new Callback<ListResponse>()
        {
            @Override
            public void onResponse(Call<ListResponse> call, Response<ListResponse> response)
            {
                if (response.isSuccessful())
                {
                    notes = response.body().getNotes();

                    Collections.sort(notes, new Comparator<Note>() {
                        @Override
                        public int compare(Note o1, Note o2) {
                            return o2.getDateForSorting().compareTo(o1.getDateForSorting());
                        }
                    });

                    adapter = new NoteListAdapter(context, R.layout.adapter_view_layout, notes);
                    listView.setAdapter(adapter);
                }
                else
                    showToast("Error getting the data with code: " + response.code());

                progressDialog.dismiss();
            }

            @Override
            public void onFailure(Call<ListResponse> call, Throwable t) {
                showToast("Failure message: " + t.getMessage());
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CREATE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                String noteContents = data.getStringExtra(EXTRA_NOTE_CONTENTS);
                String noteCreationDate = data.getStringExtra(EXTRA_NOTE_DATE);
                String noteWebSafeKey = data.getStringExtra(EXTRA_NOTE_SAFE_KEY);

                Note createdNote = new Note(noteContents,noteCreationDate,noteWebSafeKey);
                notes.add(0,createdNote);
                adapter.notifyDataSetChanged();
            }
            else if (resultCode == RESULT_CANCELED)
                showToast("Note was not created.");
        }
        else if (requestCode == DELETE_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                notes.remove(listIndex);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void showToast(String msgString)
    {
        Toast.makeText(this, msgString, Toast.LENGTH_SHORT).show();
    }
}