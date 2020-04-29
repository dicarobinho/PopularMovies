/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.popularmovies.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.example.popularmovies.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ImagesOperations {

    public static String finalImagePath = "";

    public static void urlToBitmap(Context context, String imageUrl) {
        Picasso.get().load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                saveImage(context, bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        });
    }

    public static void deleteImageFile(Context context, String imagePath) {
        File imageFile = new File(imagePath);
        boolean deleted = imageFile.delete();
        if (!deleted) {
            String errorMessage = context.getString(R.string.error);
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    private static void galleryAddPic(Context context, String imagePath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(imagePath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
        finalImagePath = imagePath;
    }

    private static void saveImage(Context context, Bitmap image) {

        String savedImagePath;

        String timeStamp = new SimpleDateFormat(Constants.DATE_FORMAT_TYPE, Locale.getDefault()).format(new Date());
        String imageFileName = Constants.JPEG_ + timeStamp + ".jpg";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/PopMovies");
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }

        if (success) {
            File imageFile = new File(storageDir, imageFileName);
            savedImagePath = imageFile.getAbsolutePath();
            try {
                OutputStream fOut = new FileOutputStream(imageFile);
                image.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
                fOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            galleryAddPic(context, savedImagePath);
        }
    }
}
