package com.example.intentexample; // Use your own package name

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.*;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class MyProfileFragment extends Fragment {
    private ImageView imageProfile;
    private String uploadedImagePath = null; // Path of the uploaded image
    private int selectedImageResId = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.myprofile, container, false);

        EditText editTextName = view.findViewById(R.id.editTextName);
        EditText editTextPhone = view.findViewById(R.id.editTextPhone);
        EditText editTextSchool = view.findViewById(R.id.editTextSchool);
        EditText editTextMail = view.findViewById(R.id.editTextMail);
        EditText editTextGithub = view.findViewById(R.id.editTextGithub);
        imageProfile = view.findViewById(R.id.imageProfile);


        // Load saved data
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String photoPath = sharedPreferences.getString("photoPath", null);
        String name = sharedPreferences.getString("name", "");
        String phone = sharedPreferences.getString("phone", "");
        String school = sharedPreferences.getString("school", "");
        String mail = sharedPreferences.getString("mail", "");
        String github = sharedPreferences.getString("github", "");

        editTextName.setText(name);
        editTextPhone.setText(phone);
        editTextSchool.setText(school);
        editTextMail.setText(mail);
        editTextGithub.setText(github);

        int savedImageResId = sharedPreferences.getInt("profileImageId", -1);

        if (photoPath != null) {
            imageProfile.setImageURI(Uri.parse(photoPath));
        } else if (savedImageResId != -1) {
            imageProfile.setImageResource(savedImageResId);
        }

        // 뒤로 가기 버튼 ImageView 찾기
        ImageView backButton = view.findViewById(R.id.backButton);

        // 뒤로 가기 버튼에 대한 OnClickListener 설정
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    fragmentManager.popBackStack(); // 이전 Fragment로 돌아가기
                }
            }
        });


        Button buttonUploadPhoto = view.findViewById(R.id.buttonChoosePhoto);
        buttonUploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        Button buttonSave = view.findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileData();
                Toast.makeText(getActivity(), "Saved!", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton buttonGenerateQR = view.findViewById(R.id.buttonGenerateQR);
        buttonGenerateQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String qrContent = getProfileDataAsJson();
                    Bitmap qrBitmap = generateQRCodeBitmap(qrContent);
                    // Display or do something with the QR code bitmap
                    showQRCodeDialog(qrBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle exception
                }
            }
        });

        return view;
    }

    private String getProfileDataAsJson() {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("name", "");
        String phone = sharedPreferences.getString("phone", "");
        String school = sharedPreferences.getString("school", "");
        String mail = sharedPreferences.getString("mail", "");
        String github = sharedPreferences.getString("github", "");
        String photoPath = sharedPreferences.getString("photoPath", null);
        int imageResId = sharedPreferences.getInt("profileImageId", -1);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("phone", phone);
            jsonObject.put("school", school);
            jsonObject.put("mail", mail);
            jsonObject.put("github", github);
            if (photoPath != null) {
                jsonObject.put("photoPath", photoPath);
            } else if (imageResId != -1) {
                jsonObject.put("profileImageId", imageResId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    private Bitmap generateQRCodeBitmap(String content) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 400, 400);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }

    private void showQRCodeDialog(Bitmap qrBitmap) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.custom_qr_dialog, null);
        builder.setView(dialogView);

        ImageView imageView = dialogView.findViewById(R.id.imageViewQRCode); // Replace with your ImageView ID
        imageView.setImageBitmap(qrBitmap);

        AlertDialog dialog = builder.create();

        Button closeButton = dialogView.findViewById(R.id.buttonClose); // Replace with your Button ID
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void saveProfileData() {
        EditText editTextName = getView().findViewById(R.id.editTextName);
        EditText editTextPhone = getView().findViewById(R.id.editTextPhone);
        EditText editTextSchool = getView().findViewById(R.id.editTextSchool);
        EditText editTextMail = getView().findViewById(R.id.editTextMail);
        EditText editTextGithub = getView().findViewById(R.id.editTextGithub);

        String name = editTextName.getText().toString();
        String phone = editTextPhone.getText().toString();
        String school = editTextSchool.getText().toString();
        String mail = editTextMail.getText().toString();
        String github = editTextGithub.getText().toString();

        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("phone", phone);
        editor.putString("school", school);
        editor.putString("mail", mail);
        editor.putString("github", github);

        if (uploadedImagePath != null) {
            // Save the path of the uploaded image
            editor.putString("photoPath", uploadedImagePath);
        } else if (selectedImageResId != -1) {
            // Save the resource ID of the selected default image
            editor.putInt("profileImageId", selectedImageResId);
        }

        editor.apply();
    }

    private void openImageChooser() {
        showImageChoiceDialog();
    }

    private void showImageChoiceDialog() {
        final int[] imageIds = new int[]{
                R.drawable.default_image1, R.drawable.default_image2, R.drawable.default_image3,
                R.drawable.default_image4, R.drawable.default_image5, R.drawable.default_image6,
                R.drawable.default_image7, R.drawable.default_image8
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_choice_dialog, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        GridView gridView = dialogView.findViewById(R.id.gridView);
        gridView.setAdapter(new ProfileAdapter(getActivity(), imageIds));
        gridView.setNumColumns(4); // Adjust as needed

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            selectedImageResId = imageIds[position];
            updateProfileImage(selectedImageResId);
            uploadedImagePath = null;
            dialog.dismiss();
        });

        // Setup the close button
        Button closeButton = dialogView.findViewById(R.id.buttonClose);
        closeButton.setBackgroundColor(getResources().getColor(R.color.nav_bar_deactive));
        closeButton.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void updateProfileImage(int imageResId) {
        imageProfile.setImageResource(imageResId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}