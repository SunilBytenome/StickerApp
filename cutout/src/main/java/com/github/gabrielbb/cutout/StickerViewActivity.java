package com.github.gabrielbb.cutout;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.sticker.ZoomIconEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import pl.aprilapps.easyphotopicker.DefaultCallback;
import pl.aprilapps.easyphotopicker.EasyImage;
import uk.co.senab.photoview.PhotoView;
import yuku.ambilwarna.AmbilWarnaDialog;

public class StickerViewActivity extends AppCompatActivity {
    private static final String TAG = StickerViewActivity.class.getSimpleName();
    public static final int PERM_RQST_CODE = 110;
    private static final int INTRO_REQUEST_CODE = 4;
    private static final String INTRO_SHOWN = "INTRO_SHOWN";
    private StickerView stickerView;
    private TextSticker sticker;
    private PhotoView pvPhotoView;
    private LinearLayout llLayout,llBottom;
    private static final short BORDER_SIZE = 45;
    private TextView tvSaveSticker,tvText;
    private static final int WRITE_EXTERNAL_STORAGE_CODE = 1;
    private static final int IMAGE_CHOOSER_REQUEST_CODE = 2;
    private static final int CAMERA_REQUEST_CODE = 3;
    private ImageView ivDecoration,ivEmojis,ivEraseBg,ivAutoEraseBg,ivText;
    private EditText etText;
    private ArrayList<Drawable> al_stickerImages=new ArrayList<>();
    private ProgressBar loadingView;
    private int textSize=50,textColor=-1;
    private Typeface typeface;;
    private int RESULT_LOAD_IMAGE=100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_view);
        llLayout=findViewById(R.id.llLayout);
        ivEraseBg=findViewById(R.id.ivEraseBg);
        ivAutoEraseBg=findViewById(R.id.ivAutoEraseBg);
        ivText=findViewById(R.id.ivText);
        stickerView = (StickerView) findViewById(R.id.sticker_view);
        pvPhotoView= (PhotoView) findViewById(R.id.pvPhotoView);
        tvSaveSticker = findViewById(R.id.tvSaveSticker);
        tvText=findViewById(R.id.tvText);
        ivEmojis=findViewById(R.id.ivEmojis);
        ivDecoration=findViewById(R.id.ivDecoration);
        loadingView=findViewById(R.id.loadingView);
        tvText.setText(Html.fromHtml("&#128104;&#8205;&#128105;&#8205;&#128103;&#8205;&#128102;"));

        ivEraseBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickerView.setLocked(!stickerView.isLocked());
                Bitmap bitmap =  llLayout.getDrawingCache();
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();
                llLayout.setDrawingCacheEnabled(false);
                Intent intent = new Intent(StickerViewActivity.this, EraseOffsetActivity.class);
                intent.putExtra("image",byteArray);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });
        ivAutoEraseBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stickerView.setLocked(!stickerView.isLocked());
                Bitmap bitmap =  llLayout.getDrawingCache();
                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();
                llLayout.setDrawingCacheEnabled(false);
                Intent intent = new Intent(StickerViewActivity.this, CutOutActivity.class);
                intent.putExtra("image",byteArray);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

        ivText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*stickerView.setLocked(!stickerView.isLocked());
                Bitmap bitmap =  llLayout.getDrawingCache();

                ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
                byte[] byteArray = bStream.toByteArray();
                llLayout.setDrawingCacheEnabled(false);
                Intent intent = new Intent(StickerViewActivity.this, CutOutActivity.class);
                intent.putExtra("image",byteArray);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();*/
                dialogForText(StickerViewActivity.this);
             //   Intent i = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
             //   startActivityForResult(i, RESULT_LOAD_IMAGE);


            }
        });
       // tvSaveSticker.setOnClickListener(v -> /*startSaveDrawingTask()*/);
        tvSaveSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSaveDrawingTask();

            }
        });
        ivDecoration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForDecoration(StickerViewActivity.this);
            }
        });

        ivEmojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForStickerAdd(StickerViewActivity.this);
            }
        });

        stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerAdded(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerAdded");
            }

            @Override
            public void onStickerClicked(@NonNull Sticker sticker) {
                //stickerView.removeAllSticker();
                if (sticker instanceof TextSticker) {
                    ((TextSticker) sticker).setTextColor(Color.RED);
                    stickerView.replace(sticker);
                    stickerView.invalidate();
                }
                Log.d(TAG, "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerDragFinished");
            }

            @Override
            public void onStickerTouchedDown(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerTouchedDown");
            }

            @Override
            public void onStickerZoomFinished(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerZoomFinished");
            }

            @Override
            public void onStickerFlipped(@NonNull Sticker sticker) {
                Log.d(TAG, "onStickerFlipped");
            }

            @Override
            public void onStickerDoubleTapped(@NonNull Sticker sticker) {
                Log.d(TAG, "onDoubleTapped: double tap will be with two click");
            }
        });

   

       if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERM_RQST_CODE);
        } else {
          start();
        }
    }

    private void loadSticker() {
        Drawable drawable =
                ContextCompat.getDrawable(this, R.drawable.haizewang_215);
        Drawable drawable1 =
                ContextCompat.getDrawable(this, R.drawable.haizewang_23);
        stickerView.addSticker(new DrawableSticker(drawable));
        stickerView.addSticker(new DrawableSticker(drawable1), Sticker.Position.BOTTOM | Sticker.Position.RIGHT);

        Drawable bubble = ContextCompat.getDrawable(this, R.drawable.bubble);
        stickerView.addSticker(
                new TextSticker(getApplicationContext())

                        .setText("Sticker\n")
                        .setMaxTextSize(14)
                        .resizeText()
                , Sticker.Position.TOP);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            start();
        } else {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }
    }

    public void testReplace(View view) {
        if (stickerView.replace(sticker)) {
            Toast.makeText(StickerViewActivity.this, "Replace Sticker successfully!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(StickerViewActivity.this, "Replace Sticker failed!", Toast.LENGTH_SHORT).show();
        }
    }

    public void testLock(View view) {

        stickerView.setLocked(!stickerView.isLocked());

    }

    public void testRemove(View view) {
        if (stickerView.removeCurrentSticker()) {
            Toast.makeText(StickerViewActivity.this, "Remove current Sticker successfully!", Toast.LENGTH_SHORT)
                    .show();
        } else {
            Toast.makeText(StickerViewActivity.this, "Remove current Sticker failed!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    public void testRemoveAll(View view) {
        stickerView.removeAllStickers();
    }

    public void reset(View view) {
        stickerView.removeAllStickers();
       // loadSticker();
    }

    public void testAdd(View view) {


        final TextSticker sticker = new TextSticker(this);
        sticker.setText("Hello, world!");
        sticker.setTextColor(Color.BLUE);
        sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        sticker.resizeText();
        stickerView.addSticker(sticker);



    }

    @Override
    protected void onResume() {
        try{
if (SaveDrawingTask.SAVED_IMAGE_URi.equals("png")){
   // stickerView.setLocked(!stickerView.isLocked());
    llLayout.setDrawingCacheEnabled(true);
    Bitmap bmp;
    byte[] byteArray = getIntent().getByteArrayExtra("image");
    bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    pvPhotoView.setImageBitmap(bmp);
}else {
    finish();
}
        }catch (Exception e){ }
        super.onResume();
    }

    public void setImage(Uri uri){

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                pvPhotoView.setImageBitmap(bitmap);
            } catch (IOException e) {
                exitWithError(e);
            }



        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());

        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());

        BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
                com.xiaopo.flying.sticker.R.drawable.sticker_ic_flip_white_18dp),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon));
        stickerView.setBackgroundColor(Color.WHITE);
        stickerView.setLocked(false);
        stickerView.setConstrained(true);
        sticker = new TextSticker(this);
        sticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
        R.drawable.sticker_transparent_background));
        sticker.setText("Hello, world!");
        sticker.setTextColor(Color.BLACK);
        sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
        sticker.resizeText();
      //  loadSticker();
    }

    private void startSaveDrawingTask() {
        stickerView.setLocked(!stickerView.isLocked());
        llLayout.setDrawingCacheEnabled(true);
        SaveDrawingTask task = new SaveDrawingTask(StickerViewActivity.this);
        int borderColor;
        if ((borderColor = getIntent().getIntExtra(CutOut.CUTOUT_EXTRA_BORDER_COLOR, -1)) != -1) {
            Bitmap image = BitmapUtility.getBorderedBitmap(this.llLayout.getDrawingCache(), borderColor, BORDER_SIZE);
            task.execute(image);
        } else {
            loadingView.setVisibility(View.VISIBLE);
            llLayout.measure(View.MeasureSpec.makeMeasureSpec(DynamicUnitUtils
                            .convertDpToPixels(512), View.MeasureSpec.EXACTLY),
                    View.MeasureSpec.makeMeasureSpec(DynamicUnitUtils
                            .convertDpToPixels(512), View.MeasureSpec.EXACTLY));
            task.execute(this.llLayout.getDrawingCache());
           // finish();
        }}


   public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == Activity.RESULT_OK) {
              setImage(result.getUri());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                exitWithError(result.getError());
            } else {
                setResult(Activity.RESULT_CANCELED);
                finish();
            }
        }
else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK
                && null != data) {
            Uri imageUri = data.getData();
            Bitmap bitmap=null;
            try {
                bitmap= MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imageUri,
                    filePathColumn, null, null, null);

            llLayout.setBackgroundDrawable(new BitmapDrawable(llLayout.getResources(), bitmap));

}
        else {
            EasyImage.handleActivityResult(requestCode, resultCode, data, this, new DefaultCallback() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                    exitWithError(e);
                }

                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                 //   setDrawViewBitmap(Uri.parse(imageFile.toURI().toString()));
                }

                @Override
                public void onCanceled(EasyImage.ImageSource source, int type) {
                    // Cancel handling, removing taken photo if it was canceled
                    if (source == EasyImage.ImageSource.CAMERA) {
                        File photoFile = EasyImage.lastlyTakenButCanceledPhoto(StickerViewActivity.this);
                        if (photoFile != null) photoFile.delete();
                    }

                    setResult(RESULT_CANCELED);
                    finish();
                }
            });
        }


    }
    void exitWithError(Exception e) {
        Intent intent = new Intent();
        intent.putExtra(CutOut.CUTOUT_EXTRA_RESULT, e);
        setResult(CutOut.CUTOUT_ACTIVITY_RESULT_ERROR_CODE, intent);
        finish();
    }


    private void start() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Uri uri = getExtraSource();
            if (getIntent().getBooleanExtra(CutOut.CUTOUT_EXTRA_CROP, false)) {
                CropImage.ActivityBuilder cropImageBuilder;
                if (uri != null) {
                    cropImageBuilder = CropImage.activity(uri);
                } else {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        cropImageBuilder = CropImage.activity();
                    } else {
                        ActivityCompat.requestPermissions(this,
                                new String[]{Manifest.permission.CAMERA},
                                CAMERA_REQUEST_CODE);
                        return;
                    }
                }

                cropImageBuilder = cropImageBuilder.setGuidelines(CropImageView.Guidelines.ON);
                cropImageBuilder.start(this);
            } else {
                if (uri != null) {

                }
            }

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_CODE);
        }
    }

    private Uri getExtraSource() {
        return getIntent().hasExtra(CutOut.CUTOUT_EXTRA_SOURCE) ? (Uri) getIntent().getParcelableExtra(CutOut.CUTOUT_EXTRA_SOURCE) : null;
    }


    public void dialogForDecoration(Activity activity){
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AdapterDecoration adapter;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_decoration);
      RecyclerView rvView = dialog.findViewById(R.id.rvView);
        rvView.setHasFixedSize(true);
        ArrayList<Integer> data= new ArrayList<>();

        data.add(R.drawable.dec_one);
        data.add(R.drawable.dec_two);
        data.add(R.drawable.dec_three);
        data.add(R.drawable.dec_four);
        data.add(R.drawable.dec_five);
        data.add(R.drawable.dec_six);
        data.add(R.drawable.dec_seven); data.add(R.drawable.dec_eight);
        data.add(R.drawable.dec_nine);
        rvView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter=new AdapterDecoration(activity, data, new AdapterDecoration.interfaceClick() {
            @Override
            public void image(int i) {
                Drawable drawable = ContextCompat.getDrawable(activity,i);
                dialog.dismiss();
                  stickerView.addSticker(new DrawableSticker(drawable));
            }
        });
        rvView.setAdapter(adapter);
      adapter.notifyDataSetChanged();


        dialog.show();
    }


    public void dialogForStickerAdd(Activity activity){
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Light);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        AdapertSticker adapter;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_decoration);
        RecyclerView rvView = dialog.findViewById(R.id.rvView);
        rvView.setHasFixedSize(true);
        ArrayList<String> data= new ArrayList<>();

        InputStream is = getResources().openRawResource(R.raw.emoji_list);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();
        Log.e(">>>", String.valueOf(jsonString));

        try {
            JSONObject object=new JSONObject(jsonString);
            JSONArray array=object.getJSONArray("Smileys & People");
            for (int i = 0; i < array.length(); i++) {

                JSONObject object1=array.getJSONObject(i);
                String code=object1.getString("emoji");

                data.add(code);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
       /* try {
            JSONArray array=new JSONArray(jsonString);
            for (int i = 0; i <array.length() ; i++) {
                JSONObject object=array.getJSONObject(i);
             data.add(object.getString("emoji"))   ;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }*/

        rvView.setLayoutManager(new GridLayoutManager(this, 3));
        adapter=new AdapertSticker(activity, data, new AdapertSticker.interfaceClick() {
            @Override
            public void image(String i) {
                dialog.dismiss();
                final TextSticker sticker = new TextSticker(activity);
                sticker.setText(i);

                sticker.resizeText();
                stickerView.addSticker(sticker);
            }
        });

        rvView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        dialog.show();

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    public void dialogForText(Activity activity){

        List<String> al_font = new ArrayList<String>();
        al_font.add("Pacifico.ttf");
        al_font.add("Pacifico.ttf");
        al_font.add("Dreamwod.ttf");
        al_font.add("LemonJelly.ttf");
        al_font.add("QuiteMagical.ttf");
        al_font.add("Raphtalia.ttf");
        al_font.add("AutourOne-Regular.otf");
        al_font.add("Roboto-Light.ttf");
        al_font.add("Roboto-LightItalic.ttf");
        al_font.add("Roboto-Medium.ttf");
        al_font.add("Smilen.otf");
        al_font.add("TheBrands.otf");
        al_font.add("WhaleITried.ttf");
        al_font.add("SourceSansPro-Bold.ttf");
        al_font.add("SourceSansPro-Italic.ttf");
        al_font.add("SourceSansPro-Light.ttf");
        al_font.add("SourceSansPro-Regular.ttf");
        al_font.add("SourceSansPro-Semibold.ttf");

        final Dialog dialog = new Dialog(activity,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemen_2;
      //  dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.edit_text);
        Spinner spSelectTextFont=dialog.findViewById(R.id.spSelectTextFont);
        LinearLayout llColorChosser=dialog.findViewById(R.id.llColorChosser);
        LinearLayout ivTextSizeInc,ivTextSizeDec;
        ivTextSizeInc=dialog.findViewById(R.id.ivTextSizeInc);
        ivTextSizeDec=dialog.findViewById(R.id.ivTextSizeDec);
        etText=dialog.findViewById(R.id.etText);
        TextView tvSaveSticker=dialog.findViewById(R.id.tvSaveSticker);

        List<String> al_spinner = new ArrayList<String>();
        al_spinner.add("Pacifico");
        al_spinner.add("Pacifico");
        al_spinner.add("Dreamwod");
        al_spinner.add("LemonJelly");
        al_spinner.add("QuiteMagical");
        al_spinner.add("Raphtalia");
        al_spinner.add("AutourOne");
        al_spinner.add("Roboto");
        al_spinner.add("LightItalic");
        al_spinner.add("Medium");
        al_spinner.add("Smilen");
        al_spinner.add("TheBrand");
        al_spinner.add("WhalelTired");
        al_spinner.add("SourceBold");
        al_spinner.add("SourceItalic");
        al_spinner.add("SourceLight");
        al_spinner.add("SourceRegular");
        al_spinner.add("Semibold");
        CustomAdapter customAdapter=new CustomAdapter(getApplicationContext(),al_spinner);
        spSelectTextFont.setAdapter(customAdapter);
        if (customAdapter != null)
            customAdapter.notifyDataSetChanged();
        spSelectTextFont.setAdapter(new NothingSelectedSpinnerAdapter(customAdapter,
        R.layout.spinnner_text, StickerViewActivity.this));
        spSelectTextFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try{
                    String text = al_spinner.get(i);
                    typeface=Typeface.createFromAsset(getAssets(), al_font.get(i));
                    etText.setTypeface(typeface);
                    Toast.makeText(StickerViewActivity.this, " " + text, Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                }}
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        //COLOR
        llColorChosser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorDialog();
            }
        });

        // ++ ---
        ivTextSizeInc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSize=textSize+2;
                etText.setTextSize(textSize);
            }
        });
        ivTextSizeDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSize=textSize-2;
                etText.setTextSize(textSize);
            }
        });
        tvSaveSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etText.getText().toString().equals("")){
                    Toast.makeText(activity, "Please enter text...", Toast.LENGTH_SHORT).show();
                }else {
                    final TextSticker sticker = new TextSticker(activity);
                    sticker.setText(etText.getText().toString());
                    sticker.setTextColor(textColor);
                    sticker.setTypeface(typeface);
                    sticker.setMaxTextSize((float)textSize);
                    sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                    sticker.resizeText();
                    stickerView.addSticker(sticker);
                    dialog.dismiss();
                    stickerView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                           if (!sticker.getText().equals("")){
                               Toast.makeText(StickerViewActivity.this, sticker.getText().toString(), Toast.LENGTH_SHORT).show();
                           }
                        }
                    });
                }
            }});
        dialog.show();
    }

    private void openColorDialog() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, textColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                textColor = color;

               etText.setTextColor(textColor);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
           }
        });
        dialog.show();
    }
}