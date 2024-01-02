package com.example.intentexample;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InternalStorageUtil {

    // Saves an image to internal storage and returns the file path
    public static String saveToInternalStorage(Context context, Bitmap bitmap) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        String fileName = "IMG_" + timeStamp + ".jpg";

        // Create a file in the internal storage directory
        File directory = context.getFilesDir();
        File imagePath = new File(directory, fileName);

        // Write the bitmap to file
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } finally {
            if (fos != null) {
                fos.close();
            }
        }

        return imagePath.getAbsolutePath();
    }
}
