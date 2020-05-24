package com.example.notetakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NoteListAdapter extends ArrayAdapter<Note>
{
    private Context context;
    private int resource;

    static class ViewHolder
    {
        TextView tvContents;
        TextView tvDate;
    }

    public NoteListAdapter(Context context, int resource, ArrayList<Note> objects)
    {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Note note = getItem(position);
        ViewHolder holder;

        if (convertView == null)
        {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(resource, parent,false);

            holder = new ViewHolder();

            holder.tvContents = convertView.findViewById(R.id.tv1);
            holder.tvDate = convertView.findViewById(R.id.tv2);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.tvContents.setText(note.getContents());
        holder.tvDate.setText(note.getCreationDate());

        return convertView;
    }
}
