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
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gabrielbb.cutout.test.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StickerPreviewAdapter extends RecyclerView.Adapter<StickerPreviewViewHolder> {

    @NonNull
    private StickerPack stickerPack;

    private final int cellSize;
    private int cellLimit;
    private int cellPadding;
    private final int errorResource;

    private final LayoutInflater layoutInflater;

    StickerPreviewAdapter(
            @NonNull final LayoutInflater layoutInflater,
            final int errorResource,
            final int cellSize,
            final int cellPadding,
            @NonNull final StickerPack stickerPack) {
        this.cellSize = cellSize;
        this.cellPadding = cellPadding;
        this.cellLimit = 0;
        this.layoutInflater = layoutInflater;
        this.errorResource = errorResource;
        this.stickerPack = stickerPack;
    }

    @NonNull
    @Override
    public StickerPreviewViewHolder onCreateViewHolder(@NonNull final ViewGroup viewGroup, final int i) {
        View itemView = layoutInflater.inflate(R.layout.sticker_image, viewGroup, false);
        StickerPreviewViewHolder vh = new StickerPreviewViewHolder(itemView);

        ViewGroup.LayoutParams layoutParams = vh.stickerPreviewView.getLayoutParams();
        layoutParams.height = cellSize;
        layoutParams.width = cellSize;
        vh.stickerPreviewView.setLayoutParams(layoutParams);
        vh.stickerPreviewView.setPadding(cellPadding, cellPadding, cellPadding, cellPadding);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final StickerPreviewViewHolder stickerPreviewViewHolder, final int i) {
        Sticker thisSticker = stickerPack.getSticker(i);
        Context thisContext = stickerPreviewViewHolder.stickerPreviewView.getContext();
        stickerPreviewViewHolder.stickerPreviewView.setImageResource(errorResource);
        stickerPreviewViewHolder.stickerPreviewView.setImageURI(thisSticker.getUri());
        stickerPreviewViewHolder.stickerPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogDeleteSticker(thisContext,thisSticker,thisContext);
               /* ImageView image = new ImageView(thisContext);
                image.setImageURI(thisSticker.getUri());
                AlertDialog alertDialog = new AlertDialog.Builder(thisContext)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(stickerPack.getStickers().size()>3 || !WhitelistCheck.isWhitelisted(thisContext, stickerPack.getIdentifier())){
                                    dialogInterface.dismiss();
                                    stickerPack.deleteSticker(thisSticker);
                                    Activity thisActivity = ((Activity)thisContext);
                                    thisActivity.finish();
                                    thisActivity.startActivity(thisActivity.getIntent());
                                    Toast.makeText(thisContext, "Sticker Pack deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    AlertDialog alertDialog = new AlertDialog.Builder(thisContext)
                                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            }).create();
                                    alertDialog.setTitle("Invalid Action");
                                    alertDialog.setMessage("A sticker pack that is already applied to WhatsApp cannot have less than 3 stickers. " +
                                            "In order to remove additional stickers, please add more to the pack first or remove the pack from the WhatsApp app.");
                                    alertDialog.show();
                                }
                            }
                        })
                        .setView(image)
                        .create();
                alertDialog.setTitle("Do you want to delete this sticker?");
                alertDialog.setMessage("Deleting this sticker will also remove it from your WhatsApp app.");
                alertDialog.show();*/
            }
        });
    }

    @Override
    public int getItemCount() {
        int numberOfPreviewImagesInPack;
        numberOfPreviewImagesInPack = stickerPack.getStickers().size();
        if (cellLimit > 0) {
            return Math.min(numberOfPreviewImagesInPack, cellLimit);
        }
        return numberOfPreviewImagesInPack;
    }




    public void dialogDeleteSticker(Context activity, Sticker thisSticker,Context thisContext){
        final Dialog dialog = new Dialog(activity);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogThemen_3;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.setContentView(R.layout.dialog_delete_sticker);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tvOk);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        ImageView imageView=dialog.findViewById(R.id.imageView);
        imageView.setImageURI(thisSticker.getUri());


        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(stickerPack.getStickers().size()>3 || !WhitelistCheck.isWhitelisted(thisContext, stickerPack.getIdentifier())){
                    dialog.dismiss();
                    stickerPack.deleteSticker(thisSticker);
                    Activity thisActivity = ((Activity)thisContext);
                    thisActivity.finish();
                    thisActivity.startActivity(thisActivity.getIntent());
                    Toast.makeText(thisContext, "Sticker Pack deleted", Toast.LENGTH_SHORT).show();
                } else {
                    dialogAddWhatAppSticker(thisContext);
                }

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

    public void dialogAddWhatAppSticker(Context activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_add_to_whatapp);
        TextView tvOk = (TextView) dialog.findViewById(R.id.tvOk);
        TextView tvMessage = (TextView) dialog.findViewById(R.id.tvMessage);
String mes="A sticker pack that is already applied to WhatsApp cannot have less than 3 stickers. \" +\n" +
        "\"In order to remove additional stickers, please add more to the pack first or remove the pack from the WhatsApp app.\");\n" + "";
        tvMessage.setText(mes);
        TextView tvCancel = (TextView) dialog.findViewById(R.id.tvCancel);
        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
