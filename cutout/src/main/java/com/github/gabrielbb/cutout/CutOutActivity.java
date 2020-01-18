package com.github.gabrielbb.cutout;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.alexvasilkov.gestures.views.interfaces.GestureView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import yuku.ambilwarna.AmbilWarnaDialog;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class CutOutActivity extends AppCompatActivity {

    public FrameLayout loadingModal;
    private GestureView gestureView;
    private DrawView drawView;
    private LinearLayout manualClearSettingsLayout;

    private static final short MAX_ERASER_SIZE = 150;
    private static final short BORDER_SIZE = 45;
    private static final float MAX_ZOOM = 4F;
    private EditText etText;
    private Bitmap bitmap;
    private String strText = "", strTextStyle = "AutourOne-Regular.otf";
    private Button autoClearButton, manualClearButton, btnText, btnDrawText;

    private TextView tvSaveSticker;

    private int textColor = -1, textSize = 35, w, h;
    private RelativeLayout top_linear, llBottom;

    private SeekBar strokeBar;
    private ImageView ivAddSticker, ivMoveSticker;
    private int RESULT_LOAD_IMAGE = 100;
    Uri selectedSticker;

    private Typeface typeface;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_edit);
        initView();


    }

    private void initView() {

        autoClearButton = findViewById(R.id.auto_clear_button);
        manualClearButton = findViewById(R.id.manual_clear_button);

        tvSaveSticker = findViewById(R.id.tvSaveSticker);
        btnText = findViewById(R.id.btnText);
        btnDrawText = findViewById(R.id.btnDrawText);
        ivAddSticker = findViewById(R.id.ivAddSticker);
        ivMoveSticker = findViewById(R.id.ivMoveSticker);
        etText = findViewById(R.id.etText);
        gestureView = findViewById(R.id.gestureView);
        drawView = findViewById(R.id.drawView);
        top_linear = findViewById(R.id.top_linear);

        FrameLayout drawViewLayout = findViewById(R.id.drawViewLayout);
        loadingModal = findViewById(R.id.loadingModal);
        strokeBar = findViewById(R.id.strokeBar);
        int sdk = android.os.Build.VERSION.SDK_INT;

        if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            // drawViewLayout.setBackgroundDrawable(CheckerboardDrawable.create());
        } else {
            //  drawViewLayout.setBackground(CheckerboardDrawable.create());
        }

        strokeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                drawView.setStrokeWidth(seekBar.getProgress());
            }
        });
        strokeBar.setMax(MAX_ERASER_SIZE);
        strokeBar.setProgress(50);
        drawView.setDrawingCacheEnabled(true);
        drawView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        drawView.setStrokeWidth(strokeBar.getProgress());
        loadingModal.setVisibility(INVISIBLE);
        drawView.setLoadingModal(loadingModal);
        manualClearSettingsLayout = findViewById(R.id.manual_clear_settings_layout);

        Bitmap bmp;
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        this.bitmap = bmp;
        drawView.setBitmap(bitmap, "", false, strTextStyle, textColor, textSize, selectedSticker);

        setUndoRedo();
        initializeActionButtons();
        clicks();

    }

    private void clicks() {

        ivAddSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });
        ivMoveSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setAction(DrawView.DrawViewAction.ADD_STICKER);
            }
        });
        btnDrawText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawView.setAction(DrawView.DrawViewAction.TEXT_DRAW);
            }
        });
        tvSaveSticker.setOnClickListener(v -> doneTask());

        btnText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForText(CutOutActivity.this);

            }
        });


    }


    private void doneTask() {
        Bitmap bitmap = drawView.getDrawingCache();
        drawView.setDrawingCacheEnabled(true);
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();
        drawView.setDrawingCacheEnabled(false);


        Intent intent = new Intent(CutOutActivity.this, StickerViewActivity.class);
        intent.putExtra("image", byteArray);
        startActivity(intent);
        finish();
    }


    private void activateGestureView() {
        gestureView.getController().getSettings()
                .setMaxZoom(MAX_ZOOM)
                .setDoubleTapZoom(-1f) // Falls back to max zoom level
                .setPanEnabled(true)
                .setZoomEnabled(true)
                .setDoubleTapEnabled(true)
                .setOverscrollDistance(0f, 0f)
                .setOverzoomFactor(2f);
    }

    private void deactivateGestureView() {
        gestureView.getController().getSettings()
                .setPanEnabled(false)
                .setZoomEnabled(false)
                .setDoubleTapEnabled(false);
    }

    private void initializeActionButtons() {

        Button zoomButton = findViewById(R.id.zoom_button);
        autoClearButton.setActivated(false);
        manualClearButton.setActivated(true);
        autoClearButton.setOnClickListener((buttonView) -> {
            //if (!autoClearButton.isActivated()) {
            //   strText = etText.getText().toString();
            //      drawView.setBitmap(bitmap, etText.getText().toString(), false, strTextStyle,textColor,textSize,selectedSticker);

            btnText.setActivated(false);
            drawView.setAction(DrawView.DrawViewAction.AUTO_CLEAR);
            manualClearSettingsLayout.setVisibility(INVISIBLE);
            autoClearButton.setActivated(true);
            // manualClearButton.setActivated(false);
            zoomButton.setActivated(false);
            deactivateGestureView();
            // }
        });
        // manualClearButton.setActivated(true);
        drawView.setAction(DrawView.DrawViewAction.MANUAL_CLEAR);
        manualClearButton.setOnClickListener((buttonView) -> {
            //   if (!manualClearButton.isActivated()) {

            try {
                drawView.setBitmap(bitmap, etText.getText().toString(), false, strTextStyle, textColor, textSize, selectedSticker);

            } catch (Exception e) {

            }
            drawView.setAction(DrawView.DrawViewAction.MANUAL_CLEAR);
            manualClearSettingsLayout.setVisibility(VISIBLE);
            //  manualClearButton.setActivated(true);
            autoClearButton.setActivated(false);
            zoomButton.setActivated(false);
            btnText.setActivated(false);
            deactivateGestureView();
            //  }

        });

        zoomButton.setActivated(false);
        deactivateGestureView();
        zoomButton.setOnClickListener((buttonView) -> {
            if (!zoomButton.isActivated()) {
                drawView.setAction(DrawView.DrawViewAction.ZOOM);
                manualClearSettingsLayout.setVisibility(INVISIBLE);
                zoomButton.setActivated(true);
                manualClearButton.setActivated(false);
                autoClearButton.setActivated(false);
                activateGestureView();
            }

        });
    }

    private void setUndoRedo() {
        Button undoButton = findViewById(R.id.undo);
        undoButton.setEnabled(false);
        undoButton.setOnClickListener(v -> undo());
        Button redoButton = findViewById(R.id.redo);
        redoButton.setEnabled(false);
        redoButton.setOnClickListener(v -> redo());


        drawView.setButtons(undoButton, redoButton, loadingModal);
    }


    private void setDrawViewBitmap(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            this.bitmap = bitmap;
            drawView.setBitmap(bitmap, "", false, strTextStyle, textColor, textSize, selectedSticker);


        } catch (IOException e) {

        }
    }


    private void undo() {
        drawView.undo();
    }

    private void redo() {
        drawView.redo();
    }


    private void openColorDialog() {
        AmbilWarnaDialog dialog = new AmbilWarnaDialog(this, textColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                textColor = color;
                strText = etText.getText().toString();
                etText.setTextColor(textColor);
            }

            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                //   Toast.makeText(getApplicationContext(), "Action canceled!", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Bitmap bitmap = drawView.getDrawingCache();
        drawView.setDrawingCacheEnabled(true);
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream);
        byte[] byteArray = bStream.toByteArray();
        drawView.setDrawingCacheEnabled(false);

        Intent intent = new Intent(CutOutActivity.this, StickerViewActivity.class);
        intent.putExtra("image", byteArray);
        startActivity(intent);
        finish();
    }

    public void dialogForText(Activity activity) {
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
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemen_2;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        drawView.setAction(DrawView.DrawViewAction.MANUAL_CLEAR);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.edit_text);
        Spinner spSelectTextFont = dialog.findViewById(R.id.spSelectTextFont);
        LinearLayout llColorChosser = dialog.findViewById(R.id.llColorChosser);
        LinearLayout ivTextSizeInc, ivTextSizeDec;
        ivTextSizeInc = dialog.findViewById(R.id.ivTextSizeInc);
        ivTextSizeDec = dialog.findViewById(R.id.ivTextSizeDec);
        etText = dialog.findViewById(R.id.etText);
        TextView tvSaveSticker = dialog.findViewById(R.id.tvSaveSticker);
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
        CustomAdapter customAdapter = new CustomAdapter(getApplicationContext(), al_spinner);
        spSelectTextFont.setAdapter(customAdapter);
        if (customAdapter != null)
            customAdapter.notifyDataSetChanged();

        spSelectTextFont.setAdapter(new NothingSelectedSpinnerAdapter(customAdapter,
                R.layout.spinnner_text, CutOutActivity.this));
        spSelectTextFont.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                    //  String text = spSelectTextFont.getSelectedItem().toString();;
                    String text = al_spinner.get(i);
                    strTextStyle= al_font.get(i);
                    typeface=Typeface.createFromAsset(getAssets(), al_font.get(i));
                    etText.setTypeface(typeface);
                    Toast.makeText(CutOutActivity.this, " " + text, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                }
            }

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
                textSize = textSize + 2;
                etText.setTextSize(textSize);
            }
        });
        ivTextSizeDec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textSize = textSize - 2;
                etText.setTextSize(textSize);
            }
        });
        tvSaveSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etText.getText().toString().equals("")) {
                    Toast.makeText(activity, "Please enter text...", Toast.LENGTH_SHORT).show();
                } else {
                    drawView.setBitmap(bitmap, etText.getText().toString(), true, strTextStyle, textColor, textSize, selectedSticker);
                    dialog.dismiss();

                }


            }
        });
        etText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strText = etText.getText().toString();
                drawView.setBitmap(bitmap, etText.getText().toString(), true, strTextStyle, textColor, textSize, selectedSticker);

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                strText = etText.getText().toString();
                drawView.setBitmap(bitmap, etText.getText().toString(), true, strTextStyle, textColor, textSize, selectedSticker);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dialog.show();
    }



}

