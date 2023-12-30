package com.example.intentexample;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PhoneBook extends Fragment {
    ListView listView;
    ArrayList<Contact> contactList;
    ArrayAdapter<Contact> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.phonebook, container, false);
        listView = view.findViewById(R.id.listView);
        contactList = loadContacts();

        adapter = new ArrayAdapter<Contact>(getContext(), R.layout.list_item, R.id.textViewItem, contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedContact = adapter.getItem(position);
                showContactDetailsDialog(selectedContact);
            }
        });
        return view;
    }
    private void showContactDetailsDialog(Contact contact) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);
        builder.setTitle("Detail");

        // Create the message showing contact details
        String message = "Name: " + contact.getName() + "\n" +
                "Phone: " + contact.getPhone() + "\n" +
                "School: " + contact.getSchool() + "\n\n" +
                contact.getMemo();
        builder.setMessage(message);

        // Modify button
        builder.setPositiveButton("Modify", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Implement the functionality to modify the contact
                // showModifyContactDialog(contact);
            }
        });

        // Delete button
        builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Implement the functionality to delete the contact
                // deleteContact(contact);
            }
        });

        // Cancel button
        builder.setNeutralButton("back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private ArrayList<Contact> loadContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            InputStream is = getContext().getAssets().open("Profile.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Contact>>(){}.getType();
            contacts = gson.fromJson(json, listType);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return contacts;
    }
}