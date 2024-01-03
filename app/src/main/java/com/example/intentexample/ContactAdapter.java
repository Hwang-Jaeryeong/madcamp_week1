package com.example.intentexample;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ContactAdapter extends ArrayAdapter<Contact> {
    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact contact = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.contact_list_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.contactImage);
        TextView textViewName = convertView.findViewById(R.id.contactName);

        if (contact.getDefaultImageResId() != -1) {
            imageView.setImageResource(contact.getDefaultImageResId());
        }

        textViewName.setText(contact.getName());

        return convertView;
    }
}
