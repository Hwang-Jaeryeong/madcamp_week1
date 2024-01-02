package com.example.intentexample;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Fragment phonebook;
    Fragment gallery;
    Fragment calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phonebook = new PhoneBook();
        gallery  = new Gallery();
        calendar = new Calendar_Frag();

        getSupportFragmentManager().beginTransaction().replace(R.id.container, phonebook).commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            final int tabPhonebookId = R.id.tab_phonebook;
            final int tabGalleryId = R.id.tab_gallery;
            final int tabCalendarId = R.id  .tab_calendar;

            if (item.getItemId() == tabPhonebookId) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, phonebook).commit();
                return true;
            } else if (item.getItemId() == tabGalleryId) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, gallery).commit();
                return true;
            } else if (item.getItemId() == tabCalendarId) {
                getSupportFragmentManager().beginTransaction().replace(R.id.container, calendar).commit();
                return true;
            } else {
                return false;
            }
        });
    }
}
