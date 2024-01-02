package com.example.intentexample;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;
import java.util.ArrayList;

public class Gallery extends Fragment {

    private GalleryViewModel viewModel;
    private ImageAdapter imageAdapter;
    private ActivityResultLauncher<String> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(GalleryViewModel.class);

        // Load saved image paths when the fragment is created
        viewModel.loadImagePaths(requireContext());

        // Observe LiveData for comments
        viewModel.getCommentsLiveData().observe(this, comments -> {
            // Handle comment changes if necessary
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gallery, container, false);

        imageAdapter = new ImageAdapter(requireContext(), viewModel.getImagePaths());

        // Set up the GridView
        GridView gridView = view.findViewById(R.id.feed_gallery_view);
        gridView.setAdapter(imageAdapter);

        gridView.setOnItemClickListener((parent, view1, position, id) -> {
            String clickedImagePath = viewModel.getImagePaths().get(position);
            Toast.makeText(requireContext(), "Clicked: " + clickedImagePath, Toast.LENGTH_SHORT).show();
            showImageDialog(position);
        });

        ImageButton btnAddPic = view.findViewById(R.id.btn_add_pic);
        btnAddPic.setOnClickListener(v -> openImagePicker());

        // Register image picker launcher
        registerImagePickerLauncher();

        return view;
    }

    private void registerImagePickerLauncher() {
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                result -> {
                    if (result != null) {
                        try {
                            // Convert Uri to Bitmap
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), result);

                            // Save the bitmap to internal storage and get the path
                            String imagePath = InternalStorageUtil.saveToInternalStorage(requireContext(), bitmap);

                            // Add the image path to the view model
                            viewModel.addImagePath(imagePath);
                            imageAdapter.notifyDataSetChanged();

                            // Save updated paths to SharedPreferences
                            viewModel.saveImagePaths(requireContext());
                        } catch (IOException e) {
                            e.printStackTrace();
                            Toast.makeText(requireContext(), "Failed to save image", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(requireContext(), "Image upload failed", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize and register the image picker launcher
        registerImagePickerLauncher();

        // Get the button from the layout
        ImageButton btnAddPic = view.findViewById(R.id.btn_add_pic);

        // Set the click listener for the button
        btnAddPic.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        pickImageLauncher.launch("image/*");
    }


    private void showImageDialog(int position) {
        // Create an instance of ImageDialogFragment with image paths and comments data
        ImageDialogFragment dialogFragment = ImageDialogFragment.newInstance(
                position,
                new ArrayList<>(viewModel.getImagePaths()),
                new ArrayList<>(viewModel.getComments())
        );

        // Set up a CommentChangeListener to handle comment changes
        dialogFragment.setCommentChangeListener(new ImageDialogFragment.CommentChangeListener() {
            @Override
            public void onCommentChanged(ArrayList<String> updatedComments) {
                // Perform actions when comments are changed
                viewModel.setComments(updatedComments);
            }
        });

        // Display the ImageDialogFragment using FragmentTransaction
        getParentFragmentManager().beginTransaction()
                .replace(android.R.id.content, dialogFragment)
                .addToBackStack(null)  // Add to back stack for back navigation support
                .commit();
    }





    // SimpleAnimationListener class to override only onAnimationEnd
    private static class SimpleAnimationListener implements android.view.animation.Animation.AnimationListener {
        @Override
        public void onAnimationStart(android.view.animation.Animation animation) {
        }

        @Override
        public void onAnimationEnd(android.view.animation.Animation animation) {
        }

        @Override
        public void onAnimationRepeat(android.view.animation.Animation animation) {
        }
    }
}