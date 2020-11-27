package com.scanlibrary;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by jhansi on 05/04/15.
 */
public class Utils {

    public static ProgressBar progressBar;

    private Utils() {

    }

    public static int progress=0;

    public static Uri getUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        // returning bitmap
    }


    public static AlertDialog convertingDialog(Context context)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View promptsView = LayoutInflater.from(context).inflate(R.layout.dialog_converting, null);

        progressBar= promptsView.findViewById(R.id.progressBar);
        progressBar.setMax(100);
        progressBar.setProgress(progress);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setCancelable(true);


        final AlertDialog alertDialog = alertDialogBuilder.create();


        return alertDialog;
    }
}