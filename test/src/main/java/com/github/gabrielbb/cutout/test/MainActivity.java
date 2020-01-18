package com.github.gabrielbb.cutout.test;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.Trigger;
import com.github.gabrielbb.cutout.CutOut;
import com.github.gabrielbb.cutout.test.WhatsAppBasedCode.StickerPackListActivity;

public class MainActivity extends AppCompatActivity {
    Context context;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       /* imageView = findViewById(R.id.imageView);

        final Uri imageIconUri = getUriFromDrawable(R.drawable.image_icon);
        imageView.setImageURI(imageIconUri);
        imageView.setTag(imageIconUri);*/

        TextView fab = findViewById(R.id.fab);

        fab.setOnClickListener(view -> {
            final Uri testImageUri = getUriFromDrawable(R.drawable.test_image);

            CutOut.activity()
                    .start(this);
        });


        Driver driver = new GooglePlayDriver(MainActivity.this);
        FirebaseJobDispatcher firebaseJobDispatcher = new FirebaseJobDispatcher(driver);

        Job constraintReminderJob = firebaseJobDispatcher.newJobBuilder()
                .setService(ReminderService.class)
                .setTag("TAG")
                .setConstraints(Constraint.DEVICE_CHARGING)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(true)
                .setTrigger(Trigger.executionWindow(
                        0,
                        0
                ))
                .setReplaceCurrent(true)
                .build();

        firebaseJobDispatcher.schedule(constraintReminderJob);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CutOut.CUTOUT_ACTIVITY_REQUEST_CODE) {

            switch (resultCode) {
                case Activity.RESULT_OK:
                    Uri imageUri = CutOut.getUri(data);
                    // Save the image using the returned Uri here
                    startActivity(new Intent(MainActivity.this, StickerPackListActivity.class));
                    break;
                case CutOut.CUTOUT_ACTIVITY_RESULT_ERROR_CODE:
                    Exception ex = CutOut.getError(data);
                    break;
                default:
                    System.out.print("User cancelled the CutOut screen");

            }
        }
    }

    public Uri getUriFromDrawable(int drawableId) {
        return Uri.parse("android.resource://" + getPackageName() + "/drawable/" + getApplicationContext().getResources().getResourceEntryName(drawableId));
    }
}
