package com.github.gabrielbb.cutout;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class EditTextActivity extends AppCompatActivity {
LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_text);

        Bitmap bmp;
        linearLayout=findViewById(R.id.linearLayout);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);


        BitmapDrawable background = new BitmapDrawable(bmp);
        linearLayout.setBackgroundDrawable(background);
    }
}
