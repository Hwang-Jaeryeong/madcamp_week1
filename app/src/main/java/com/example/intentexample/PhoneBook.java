package com.example.intentexample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.Manifest;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PhoneBook extends Fragment {
    ListView listView;
    ArrayList<Contact> contactList;
    ArrayAdapter<Contact> adapter;
    ArrayList<Contact> originalContactList;
    private static final int REQUEST_CALL = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d("PhoneBook", "onCreateView called");
        View view = inflater.inflate(R.layout.phonebook, container, false);
        listView = view.findViewById(R.id.listView);
        contactList = loadContacts();
        originalContactList = new ArrayList<>(contactList);

        adapter = new ArrayAdapter<Contact>(getContext(), R.layout.list_item, R.id.textViewItem, contactList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Contact selectedContact = adapter.getItem(position);
                showContactDetailsDialog(selectedContact);
            }
        });

        EditText searchBox = view.findViewById(R.id.searchBox);
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                filterContacts(charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        ImageButton myProfileButton = view.findViewById(R.id.MyProfile);
        myProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Code to open the new fragment
                MyProfileFragment myProfileFragment = new MyProfileFragment();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, myProfileFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });
        ImageButton addProfileButton = view.findViewById(R.id.AddProfile);
        addProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQRScanner();
            }
        });
        return view;
    }

    private void showContactDetailsDialog(final Contact contact) {
        // 커스텀 레이아웃 생성
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.profile_detail_dialog, null);

        // 버튼 및 이미지 생성
        ImageButton deleteButton = dialogView.findViewById(R.id.deleteButton);
        ImageButton cancelButton = dialogView.findViewById(R.id.backButton);
        ImageView nameImage = dialogView.findViewById(R.id.profileNameImage);
        ImageView phoneImage = dialogView.findViewById(R.id.profilePhoneImage);
        ImageView schoolImage = dialogView.findViewById(R.id.profileSchoolImage);
        ImageView mailImage = dialogView.findViewById(R.id.profileMailImage);
        ImageView githubImage = dialogView.findViewById(R.id.profileGithubImage);

        nameImage.setImageResource(R.drawable.profile_name);
        phoneImage.setImageResource(R.drawable.profile_phone);
        schoolImage.setImageResource(R.drawable.profile_school);
        mailImage.setImageResource(R.drawable.profile_mail);
        githubImage.setImageResource(R.drawable.github);

        TextView nameTextView = dialogView.findViewById(R.id.profileNameText);
        TextView phoneTextView = dialogView.findViewById(R.id.profilePhoneText);
        TextView schoolTextView = dialogView.findViewById(R.id.profileSchoolText);
        TextView mailTextView = dialogView.findViewById(R.id.profileMailText);
        TextView githubTextView = dialogView.findViewById(R.id.profileGithubText);

        nameTextView.setText(contact.getName());
        phoneTextView.setText(contact.getPhone());
        schoolTextView.setText(contact.getSchool());
        mailTextView.setText(contact.getMail());
        githubTextView.setText(contact.getGithub());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.CustomDialogTheme);

        builder.setView(dialogView);

        final AlertDialog alertDialog = builder.create();

        githubTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the click event, open the URL in a browser
                openUrlInBrowser(contact.getGithub());
            }
        });

        phoneTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall(contact.getPhone());
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact(contact);
                alertDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just close the dialog
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }
    private void openUrlInBrowser(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    private void deleteContact(final Contact contact) {
        // delete_dialog.xml 파일을 이용하여 View를 생성합니다.
        View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.delete_dialog, null);

        // AlertDialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(dialogView);

        // delete_dialog.xml에서 정의한 View들을 찾습니다.
        ImageButton deleteButton = dialogView.findViewById(R.id.deleteButton);
        ImageButton cancelButton = dialogView.findViewById(R.id.cancelButton);
        ImageView deleteImage = dialogView.findViewById(R.id.deleteImage);

        // delete_dialog.xml에서 정의한 ImageView에 이미지 설정
        deleteImage.setImageResource(R.drawable.delete);

        // AlertDialog의 내용을 설정하지 않습니다.
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Just close the dialog
                alertDialog.dismiss();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performDeletion(contact);
                alertDialog.dismiss();
            }
        });
    }

    private void performDeletion(Contact contact) {
        // 연락처 삭제
        contactList.remove(contact);
        originalContactList.remove(contact);

        // internal json 업데이트
        Gson gson = new Gson();
        String json = gson.toJson(contactList);
        saveJsonToStorage(json);

        // ListView 업데이트
        adapter.notifyDataSetChanged();
    }


    private void saveJsonToStorage(String json) {
        try {
            FileOutputStream fos = getContext().openFileOutput("Profile_Internal.json", Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(json);
            writer.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private ArrayList<Contact> loadContactsFromInternalStorage() {
        ArrayList<Contact> contacts = new ArrayList<>();
        try {
            FileInputStream fis = getContext().openFileInput("Profile_Internal.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader reader = new BufferedReader(isr);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Contact>>(){}.getType();
            contacts = gson.fromJson(builder.toString(), listType);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return contacts;
    }

    private ArrayList<Contact> loadContactsFromAssets() {
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

    public void startQRScanner() {
        Log.d("PhoneBook", "startQRScanner called");
        IntentIntegrator integrator = new IntentIntegrator(getActivity()).forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Scan a QR code");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        Log.d("PhoneBook", "Initiating scan");
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("PhoneBook", "onActivityResult called");
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.d("PhoneBook", "QR Scan cancelled");
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Log.d("PhoneBook", "QR Scan result: " + result.getContents());
                addContactFromQRCode(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    private void addContactFromQRCode(String jsonData) {
        try {
            Log.d("PhoneBook", "QR Data: " + jsonData);

            Gson gson = new Gson();
            Contact newContact = gson.fromJson(jsonData, Contact.class);

            if (newContact != null) {
                contactList.add(newContact);
                originalContactList.add(newContact);

                adapter.notifyDataSetChanged();
                saveJsonToStorage(gson.toJson(contactList));

                Toast.makeText(getActivity(), "Added Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("PhoneBook", "Parsed contact is null.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("PhoneBook", "Error parsing QR Data", e);
            Toast.makeText(getActivity(), "Error adding contact from QR code", Toast.LENGTH_SHORT).show();
        }
        sortContacts();
        adapter.notifyDataSetChanged();
    }

    private void makePhoneCall(String number) {
        try {
            if (number.trim().length() > 0) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
                } else {
                    String dial = "tel:" + number;
                    startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                }
            } else {
                Toast.makeText(getActivity(), "Enter Phone Number", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용
            } else {
                Toast.makeText(getActivity(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Comparator<Contact> contactNameComparator = new Comparator<Contact>() {
        @Override
        public int compare(Contact c1, Contact c2) {
            return c1.getName().compareToIgnoreCase(c2.getName());
        }
    };
    private ArrayList<Contact> loadContacts() {
        ArrayList<Contact> contacts = new ArrayList<>();
        File file = new File(getContext().getFilesDir(), "Profile_Internal.json");
        if (file.exists()) {
            // internal storage에서 가져옴(2회차 이후 실행)
            contacts = loadContactsFromInternalStorage();
        } else {
            // Asset에서 가져옴(최초 실행)
            contacts = loadContactsFromAssets();
        }

        Collections.sort(contacts, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
        return contacts;
    }
    private void updateContacts() {
        Collections.sort(contactList, contactNameComparator);
        adapter.notifyDataSetChanged();
    }
    private void filterContacts(String text) {
        ArrayList<Contact> filteredList = new ArrayList<>();

        for (Contact contact : originalContactList) {
            if (contact.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(contact);
            }
        }

        Collections.sort(filteredList, contactNameComparator); // Sort the filtered list

        adapter.clear();
        adapter.addAll(filteredList);
        adapter.notifyDataSetChanged();
    }
    private void sortContacts() {
        Collections.sort(contactList, new Comparator<Contact>() {
            @Override
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareToIgnoreCase(c2.getName());
            }
        });
    }
}