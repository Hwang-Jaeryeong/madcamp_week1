package com.example.intentexample;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
            showImageDialog(position);
        });

        gridView.setOnItemLongClickListener((parent, view1, position, id) -> {
            showDeleteConfirmationDialog(position);
            return true; // return true to indicate the click was handled
        });

        ImageButton btnAddPic = view.findViewById(R.id.btn_add_pic);
        btnAddPic.setOnClickListener(v -> openImagePicker());

        getParentFragmentManager().setFragmentResultListener("rotationRequestKey", this, (requestKey, bundle) -> {
            boolean rotationChanged = bundle.getBoolean("rotationChanged", false);
            if (rotationChanged) {
                if (imageAdapter != null) {
                    imageAdapter.notifyDataSetChanged();
                }
            }
        });

        return view;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (imageAdapter != null) {
            imageAdapter.notifyDataSetChanged();
        }
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

    private void showDeleteConfirmationDialog(int position) {
        // Inflate the custom layout
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_dialog_photo, null);

        // Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);

        ImageView deleteImage = dialogView.findViewById(R.id.deleteImageP);
        deleteImage.setImageResource(R.drawable.delete);
        ImageButton deleteButton = dialogView.findViewById(R.id.deleteButtonP);
        ImageButton cancelButton = dialogView.findViewById(R.id.cancelButtonP);

        // Create the AlertDialog before setting button listeners
        AlertDialog alertDialog = builder.create();

        // Set up buttons and their click listeners
        deleteButton.setOnClickListener(v -> {
            deletePhoto(position);
            alertDialog.dismiss();
        });

        cancelButton.setOnClickListener(v -> alertDialog.dismiss());

        // Show the AlertDialog
        alertDialog.show();
    }
    private void deletePhoto(int position) {
        // Remove the image path from the ViewModel
        viewModel.removeImagePath(position);

        // Update the GridView
        imageAdapter.notifyDataSetChanged();

        // Update SharedPreferences
        viewModel.saveImagePaths(requireContext());
    }

    private void openImagePicker() {
        pickImageLauncher.launch("image/*");
    }


    private void showImageDialog(int position) {
        // Create an instance of ImageDialogFragment with image paths and comments data
        if (position >= 0 && position < viewModel.getImagePaths().size()) {
            String imagePath = viewModel.getImagePaths().get(position);
            ArrayList<String> comments = viewModel.getComments(imagePath);

            ImageDialogFragment dialogFragment = ImageDialogFragment.newInstance(imagePath, comments);

            // Set up a CommentChangeListener to handle comment changes
            dialogFragment.setCommentChangeListener(new ImageDialogFragment.CommentChangeListener() {
                @Override
                public void onCommentChanged(ArrayList<String> updatedComments) {
                    // Perform actions when comments are changed
                    viewModel.setComments(updatedComments);
                }
            });

            // 애니메이션을 포함한 FragmentTransaction 설정
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);

            // FragmentTransaction을 사용하여 ImageDialogFragment 표시
            transaction.replace(android.R.id.content, dialogFragment)
                    .addToBackStack(null)  // 뒤로 가기 지원을 위해 백 스택에 추가
                    .commit();
        }
    }
}