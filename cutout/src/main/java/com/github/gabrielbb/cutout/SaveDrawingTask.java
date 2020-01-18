package com.github.gabrielbb.cutout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.util.Pair;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

public class SaveDrawingTask extends AsyncTask<Bitmap, Void, Pair<File, Exception>> {

    public static  String SAVED_IMAGE_URi = "png";
    private static final String SAVED_IMAGE_NAME = "cutout_tmp";
    String text;

    private  WeakReference<StickerViewActivity> activityWeakReference;

    Context context;

    SaveDrawingTask(StickerViewActivity activity) {
        this.activityWeakReference = new WeakReference<>(activity);
        context=activity;
        this.text=text;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Pair<File, Exception> doInBackground(Bitmap... bitmaps) {

        try {
String mText=text;
            File path = Environment.getExternalStorageDirectory();
            File myDir = new File(path.getAbsolutePath() + "/EraseBG");
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            File file = new File(myDir, System.currentTimeMillis() + ".jpg");



            try (FileOutputStream out = new FileOutputStream(file)) {
                   Bitmap mBitmap = bitmaps[0];

                Resources resources = activityWeakReference.get().getApplicationContext().getResources();
                float scale = resources.getDisplayMetrics().density;

                mBitmap.compress(Bitmap.CompressFormat.PNG, 70, out);
                out.flush();
                out.close();
                MediaScannerConnection.scanFile(activityWeakReference.get().getApplicationContext(), new String[]{file.toString()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                                Log.i("ExternalStorage", "Scanned " + path + ":");
                                Log.i("ExternalStorage", "-> uri=" + uri);
                            }
                        });

                return new Pair<>(file, null);
            }
        } catch (IOException e) {
            return new Pair<>(null, e);
        }
    }

    protected void onPostExecute(Pair<File, Exception> result) {
        super.onPostExecute(result);

        Intent resultIntent = new Intent();

        if (result.first != null) {
            Uri uri = Uri.fromFile(result.first);
            SAVED_IMAGE_URi= String.valueOf(uri);
            resultIntent.putExtra(CutOut.CUTOUT_EXTRA_RESULT, uri);
            activityWeakReference.get().setResult(Activity.RESULT_OK, resultIntent);
            activityWeakReference.get().finish();
        } else {
            activityWeakReference.get().exitWithError(result.second);
        }
    }

}