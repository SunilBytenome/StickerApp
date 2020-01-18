package com.github.gabrielbb.cutout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

class BitmapUtility {

    static Bitmap getResizedBitmap(Bitmap bitmap, int maxWidth, int maxHeight) {
      /*  Bitmap background = Bitmap.createBitmap(mwidth, mheight, Bitmap.Config.ARGB_8888);

        float originalWidth = bitmap.getWidth();
        float originalHeight = bitmap.getHeight();

        Canvas canvas = new Canvas(background);

        float scale = mwidth / originalWidth;

        float xTranslation = 0.0f;
        float yTranslation = (mheight - originalHeight * scale) / 2.0f;
        float pivotX = 0;
        float pivotY = 0;
        float scaleX = mwidth / (float) bitmap.getWidth();
        float scaleY = mheight / (float) bitmap.getHeight();
        Matrix transformation = new Matrix();
        //transformation.postTranslate(xTranslation, yTranslation);
        transformation.setScale(scaleX, scaleY, pivotX, pivotY);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        canvas.setMatrix(transformation);
        canvas.drawBitmap(bitmap, transformation, paint);

  Bitmap     mbitmap = Bitmap.createScaledBitmap(background, 540, 540, true);
        return bitmap;*/


      /*  Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;*/


        if (maxHeight > 0 && maxWidth > 0) {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            bitmap = Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true);
            return bitmap;
        } else {
            return bitmap;
        }
    }

    static Bitmap getBorderedBitmap(Bitmap image, int borderColor, int borderSize) {

        // Creating a canvas with an empty bitmap, this is the bitmap that gonna store the final canvas changes
        Bitmap finalImage = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(finalImage);

        // Make a smaller copy of the image to draw on top of original
        Bitmap imageCopy = Bitmap.createScaledBitmap(image, image.getWidth(), image.getHeight(), true);
        // Let's draw the bigger image using a white paint brush
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(borderColor, PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(image, 0, 0, paint);
        int width = image.getWidth();
        int height = image.getHeight();
        float centerX = (width - imageCopy.getWidth()) * 0.5f;
        float centerY = (height - imageCopy.getHeight()) * 0.5f;
        // Now let's draw the original image on top of the white image, passing a null paint because we want to keep it original
        canvas.drawBitmap(imageCopy, centerX, centerY, null);

        // Returning the image with the final results
        return finalImage;
    }


}
