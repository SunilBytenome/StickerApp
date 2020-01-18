/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.gabrielbb.cutout.test.WhatsAppBasedCode;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gabrielbb.cutout.CutOut;
import com.github.gabrielbb.cutout.SaveDrawingTask;
import com.github.gabrielbb.cutout.test.BuildConfig;
import com.github.gabrielbb.cutout.test.DataArchiver;
import com.github.gabrielbb.cutout.test.R;
import com.github.gabrielbb.cutout.test.StickerBook;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class StickerPackDetailsActivity extends BaseActivity {
    /**
     * Do not change below values of below 3 lines as this is also used by WhatsApp
     */
    public static final short CUTOUT_ACTIVITY_REQUEST_CODE = 368;
    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";
    public static final int ADD_PACK = 200;
    public static final String EXTRA_STICKER_PACK_WEBSITE = "sticker_pack_website";
    public static final String EXTRA_STICKER_PACK_EMAIL = "sticker_pack_email";
    public static final String EXTRA_STICKER_PACK_PRIVACY_POLICY = "sticker_pack_privacy_policy";
    public static final String EXTRA_STICKER_PACK_TRAY_ICON = "sticker_pack_tray_icon";
    public static final String EXTRA_SHOW_UP_BUTTON = "show_up_button";
    public static final String EXTRA_STICKER_PACK_DATA = "sticker_pack";
    private static final String TAG = "StickerPackDetails";
    private RecyclerView recyclerView;
    private GridLayoutManager layoutManager;
    private StickerPreviewAdapter stickerPreviewAdapter;
    private int numColumns;
    private View addButton;
    private View tvAlreadyAdded;
    private StickerPack stickerPack;
    private View divider;
    private WhiteListCheckAsyncTask whiteListCheckAsyncTask;
    private RelativeLayout rlSharePack,ivfloatingBtn,rlbackground;
    private ImageView deleteButton;
    private ImageView ivInfo, ivBack,ivBg,ivAddClose;
    private TextView tvAddWhatsApp,tvAddSticker,tvDeletePack;
    private boolean up;
    private CircleImageView ivTrayImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sticker_pack_details);
      initView();
    }

    private void initView() {
        boolean showUpButton = getIntent().getBooleanExtra(EXTRA_SHOW_UP_BUTTON, false);
        TextView tvPackName = findViewById(R.id.tvPackName);
        TextView tvAuthor = findViewById(R.id.tvAuthor);
        ivTrayImage = findViewById(R.id.ivTrayImage);
        stickerPack = StickerBook.getStickerPackById(getIntent().getStringExtra(EXTRA_STICKER_PACK_DATA));
        rlbackground= findViewById(R.id.rlbackground);
        tvAddWhatsApp = findViewById(R.id.tvAddWhatsApp);
        ivAddClose = findViewById(R.id.ivAddClose);
        tvAddSticker = findViewById(R.id.tvAddSticker);
        tvDeletePack = findViewById(R.id.tvDeletePack);
        // addButton = findViewById(R.id.add_to_whatsapp_button);
        ivInfo = findViewById(R.id.ivInfo);
        ivBg = findViewById(R.id.ivBg);
        ivfloatingBtn = findViewById(R.id.ivfloatingBtn);
        ivBack = findViewById(R.id.ivBack);
        rlSharePack = findViewById(R.id.rlSharePack);

        tvAlreadyAdded = findViewById(R.id.tvAlreadyAdded);
        layoutManager = new GridLayoutManager(this, 1);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(pageLayoutListener);
        recyclerView.addOnScrollListener(dividerScrollListener);
        divider = findViewById(R.id.divider);
        if (stickerPreviewAdapter == null) {
            stickerPreviewAdapter = new StickerPreviewAdapter(getLayoutInflater(), R.drawable.sticker_error, getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size), getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_padding), stickerPack);
            recyclerView.setAdapter(stickerPreviewAdapter);
        }
        tvPackName.setText(stickerPack.name);
        tvAuthor.setText(stickerPack.publisher);
        ivTrayImage.setImageURI(stickerPack.getTrayImageUri());
             /*if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(showUpButton);
            getSupportActionBar().setTitle(showUpButton ? R.string.title_activity_sticker_pack_details_multiple_pack : R.string.title_activity_sticker_pack_details_single_pack);
        }*/
        clicks();
    }

    private void clicks() {
        ivTrayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogForTaryIcon(StickerPackDetailsActivity.this,stickerPack);
            }
        });
        findViewById(R.id.llAddToWhatsApp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CutOut.activity()
                        .start(StickerPackDetailsActivity.this);

            }
        });
        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stickerPack != null) {
                    final String publisherWebsite = stickerPack.publisherWebsite;
                    final String publisherEmail = stickerPack.publisherEmail;
                    final String privacyPolicyWebsite = stickerPack.privacyPolicyWebsite;
                    Uri trayIconUri = stickerPack.getTrayImageUri();
                    launchInfoActivity(publisherWebsite, publisherEmail, privacyPolicyWebsite, trayIconUri.toString());
                }
            }
        });


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        rlSharePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataArchiver.createZipFileFromStickerPack(stickerPack, StickerPackDetailsActivity.this);
            }
        });



        tvAddWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stickerPack.getStickers().size() >= 3) {
                    StickerPackDetailsActivity.this.addStickerPackToWhatsApp(stickerPack);
                } else {
                    dialogAddWhatAppSticker(StickerPackDetailsActivity.this);
                }
                if (up) {
                    slideDown(ivBg);

                } else {
                    slideUp(ivBg);
                    ivAddClose.setImageResource(R.drawable.ic_close);
                }
                up = !up;
            }
        });
        tvDeletePack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (up) {
                    slideDown(ivBg);

                } else {
                    slideUp(ivBg);
                    ivAddClose.setImageResource(R.drawable.ic_close);
                }
                up = !up;
                dialogPack(StickerPackDetailsActivity.this);
            }
        });
        tvAddSticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (up) {
                    slideDown(ivBg);

                } else {
                    slideUp(ivBg);
                    ivAddClose.setImageResource(R.drawable.ic_close);
                }
                up = !up;
                CutOut.activity()
                        .start(StickerPackDetailsActivity.this);
            }
        });
        customeFloatingButton();
    }


    private void launchInfoActivity(String publisherWebsite, String publisherEmail, String privacyPolicyWebsite, String trayIconUriString) {
        Intent intent = new Intent(StickerPackDetailsActivity.this, StickerPackInfoActivity.class);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_ID, stickerPack.identifier);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_WEBSITE, publisherWebsite);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_EMAIL, publisherEmail);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_PRIVACY_POLICY, privacyPolicyWebsite);
        intent.putExtra(StickerPackDetailsActivity.EXTRA_STICKER_PACK_TRAY_ICON, stickerPack.getTrayImageUri().toString());
        startActivity(intent); }

    private void openFile() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 3000);
        /* Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        i.setType("image/*");
        startActivityForResult(i, 3000);*/
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_info && stickerPack != null) {
            final String publisherWebsite = stickerPack.publisherWebsite;
            final String publisherEmail = stickerPack.publisherEmail;
            final String privacyPolicyWebsite = stickerPack.privacyPolicyWebsite;
            Uri trayIconUri = stickerPack.getTrayImageUri();
            launchInfoActivity(publisherWebsite, publisherEmail, privacyPolicyWebsite, trayIconUri.toString());
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
//class com.github.gabrielbb.cutout.test.WhatsAppBasedCode.StickerPack
    private void addStickerPackToWhatsApp(StickerPack sp) {
        Intent intent = new Intent();
        intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
        Log.w("IS IT A NEW IDENTIFIER?", sp.getIdentifier());
        intent.putExtra(EXTRA_STICKER_PACK_ID, sp.getIdentifier());
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY, BuildConfig.CONTENT_PROVIDER_AUTHORITY2);
        intent.putExtra(EXTRA_STICKER_PACK_NAME, sp.getName());
        try {
            startActivityForResult(intent, 200);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, R.string.error_adding_sticker_pack, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PACK) {
          if (resultCode == Activity.RESULT_CANCELED && data != null) {
                final String validationError = data.getStringExtra("validation_error");
                if (validationError != null) {
                    if (BuildConfig.DEBUG) {
                        //validation error should be shown to developer only, not users.
                        MessageDialogFragment.newInstance(R.string.title_validation_error, validationError).show(getSupportFragmentManager(), "validation error");
                    }
                    Log.e(TAG, "Validation failed:" + validationError);
                }
            } else {

            }
        } else if (requestCode == CUTOUT_ACTIVITY_REQUEST_CODE) {
            if (data == null) {
                if (SaveDrawingTask.SAVED_IMAGE_URi.equals("png")){
                }else
                {
                    Uri uri= Uri.parse(SaveDrawingTask.SAVED_IMAGE_URi);
                    SaveDrawingTask.SAVED_IMAGE_URi="png";
                    stickerPack.addSticker(uri, this);

                }

            }

            if (data != null) {
                if (data.getClipData() != null) {
                    ClipData clipData = data.getClipData();
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item path = clipData.getItemAt(i);
                      //  Uri uri = path.getUri();
                       // getContentResolver().takePersistableUriPermission(Objects.requireNonNull(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION);
                       // stickerPack.addSticker(uri, this);
                    }
                } else {
                  Uri imageUri = CutOut.getUri(data);
                    SaveDrawingTask.SAVED_IMAGE_URi="png";
                  // getContentResolver().takePersistableUriPermission(Objects.requireNonNull(uri), Intent.FLAG_GRANT_READ_URI_PERMISSION);
              stickerPack.addSticker(imageUri, this);
                }
                finish();
                startActivity(getIntent());
            }
        }
    }

    private final ViewTreeObserver.OnGlobalLayoutListener pageLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            setNumColumns(recyclerView.getWidth() / recyclerView.getContext().getResources().getDimensionPixelSize(R.dimen.sticker_pack_details_image_size));
        }
    };

    private void setNumColumns(int numColumns) {
        if (this.numColumns != numColumns) {
            layoutManager.setSpanCount(numColumns);
            this.numColumns = numColumns;
            if (stickerPreviewAdapter != null) {
                stickerPreviewAdapter.notifyDataSetChanged();
            }
        }
    }

    private final RecyclerView.OnScrollListener dividerScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull final RecyclerView recyclerView, final int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            updateDivider(recyclerView);
        }

        @Override
        public void onScrolled(@NonNull final RecyclerView recyclerView, final int dx, final int dy) {
            super.onScrolled(recyclerView, dx, dy);
            updateDivider(recyclerView);
        }

        private void updateDivider(RecyclerView recyclerView) {
            boolean showDivider = recyclerView.computeVerticalScrollOffset() > 0;
            if (divider != null) {
                divider.setVisibility(showDivider ? View.VISIBLE : View.INVISIBLE);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (SaveDrawingTask.SAVED_IMAGE_URi.equals("png")){
        }else
        {
            Uri uri= Uri.parse(SaveDrawingTask.SAVED_IMAGE_URi);
            SaveDrawingTask.SAVED_IMAGE_URi="png";
            stickerPack.addSticker(uri, this);

        }
        whiteListCheckAsyncTask = new WhiteListCheckAsyncTask(this);
        whiteListCheckAsyncTask.execute(stickerPack);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (whiteListCheckAsyncTask != null && !whiteListCheckAsyncTask.isCancelled()) {
            whiteListCheckAsyncTask.cancel(true);
        }
    }

    private void updateAddUI(Boolean isWhitelisted) {
        if (isWhitelisted) {
            //  addButton.setVisibility(View.GONE);
            tvAlreadyAdded.setVisibility(View.VISIBLE);
        } else {
           // addButton.setVisibility(View.VISIBLE);
            tvAlreadyAdded.setVisibility(View.GONE);
        }
    }

     class WhiteListCheckAsyncTask extends AsyncTask<StickerPack, Void, Boolean> {
        private final WeakReference<StickerPackDetailsActivity> stickerPackDetailsActivityWeakReference;

        WhiteListCheckAsyncTask(StickerPackDetailsActivity stickerPackListActivity) {
            this.stickerPackDetailsActivityWeakReference = new WeakReference<>(stickerPackListActivity);
        }

        @Override
        protected final Boolean doInBackground(StickerPack... stickerPacks) {
            StickerPack stickerPack = stickerPacks[0];
            final StickerPackDetailsActivity stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get();
            //noinspection SimplifiableIfStatement
            if (stickerPackDetailsActivity == null) {
                return false;
            }
            return WhitelistCheck.isWhitelisted(stickerPackDetailsActivity, stickerPack.identifier);
        }

        @Override
        protected void onPostExecute(Boolean isWhitelisted) {
            final StickerPackDetailsActivity stickerPackDetailsActivity = stickerPackDetailsActivityWeakReference.get();
            if (stickerPackDetailsActivity != null) {
                stickerPackDetailsActivity.updateAddUI(isWhitelisted);

                stickerPack = StickerBook.getStickerPackById(getIntent().getStringExtra(EXTRA_STICKER_PACK_DATA));
                if (stickerPreviewAdapter != null) {
                    stickerPreviewAdapter.notifyDataSetChanged();
                }


            }
        }
    }






    public void dialogPack(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemen_2;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_delete_pack);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tvOk);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                StickerBook.deleteStickerPackById(stickerPack.getIdentifier());

                Intent intent = new Intent(StickerPackDetailsActivity.this, StickerPackListActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
                Toast.makeText(StickerPackDetailsActivity.this, "Sticker Pack deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void dialogAddWhatAppSticker(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_to_whatapp);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tvOk);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private void floatingAction(){


    }

    private  void  customeFloatingButton(){
        ivfloatingBtn.setVisibility(View.VISIBLE);
        rlbackground.setVisibility(View.VISIBLE);
        ivBg.setVisibility(View.INVISIBLE);
        tvAddSticker.setVisibility(View.INVISIBLE);
        tvDeletePack.setVisibility(View.INVISIBLE);
        tvAddWhatsApp.setVisibility(View.INVISIBLE);
        ivfloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (up) {
                    slideDown(ivBg);

                } else {
                    slideUp(ivBg);
                    ivAddClose.setImageResource(R.drawable.ic_close);
                }
                up = !up;
                  //slideUp(binding.bg);
            }
        });
    }
    public void slideDown(final View view) {

        ivAddClose.setImageResource(R.drawable.ic_add);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        Animation animation = AnimationUtils.loadAnimation(StickerPackDetailsActivity.this.getApplicationContext(),
                R.anim.fadeout);
        tvDeletePack.startAnimation(animation);
        tvAddSticker.startAnimation(animation);
        tvAddWhatsApp.startAnimation(animation);
    }

    public void slideUp(final View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);
        animate.setDuration(300);
        animate.setFillAfter(true);
        view.startAnimation(animate);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation animation = AnimationUtils.loadAnimation(StickerPackDetailsActivity.this.getApplicationContext(),
                        R.anim.fadein);

                tvDeletePack.startAnimation(animation);
                tvAddSticker.startAnimation(animation);
                tvAddWhatsApp.startAnimation(animation);

            }
        }, 100);

    }


    public void dialogForTaryIcon(Context activity, StickerPack stickerPack){
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemen_3;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_delete_sticker);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tvOk);
        TextView tvTitle = (TextView) dialog.findViewById(R.id.tvTitle);
        TextView tvText = (TextView) dialog.findViewById(R.id.tvText);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        ImageView imageView=dialog.findViewById(R.id.imageView);
        imageView.setImageURI(stickerPack.getTrayImageUri());
        dialog.dismiss();
        tvCancel.setVisibility(View.GONE);
        tvText.setVisibility(View.GONE);
        tvTitle.setText("Tray Icon");
        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });


        dialog.show();

    }

}