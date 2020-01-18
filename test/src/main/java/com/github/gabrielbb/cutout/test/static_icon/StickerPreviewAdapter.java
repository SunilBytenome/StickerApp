/*
 * Copyright (c) WhatsApp Inc. and its affiliates.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.github.gabrielbb.cutout.test.static_icon;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.gabrielbb.cutout.test.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class StickerPreviewAdapter extends RecyclerView.Adapter<StickerPreviewViewHolder> {

    @NonNull
    private final StickerPack stickerPack;

    private final int cellSize;
    private final int cellLimit;
    private final int cellPadding;
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
        View itemView = layoutInflater.inflate(R.layout.sticker_image_item, viewGroup, false);
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
        Context thisContext = stickerPreviewViewHolder.stickerPreviewView.getContext();
        Sticker thisSticker = stickerPack.getStickers().get(i);
        stickerPreviewViewHolder.stickerPreviewView.setImageResource(errorResource);
        stickerPreviewViewHolder.stickerPreviewView.setImageURI(StickerPackLoader.getStickerAssetUri(stickerPack.identifier, stickerPack.getStickers().get(i).imageFileName));
        Uri uri=StickerPackLoader.getStickerAssetUri(stickerPack.identifier, stickerPack.getStickers().get(i).imageFileName);
        stickerPreviewViewHolder.stickerPreviewView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSticker(thisContext,uri);
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



    public void dialogSticker(Context activity, Uri uri){
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

        dialog.dismiss();
        tvCancel.setVisibility(View.GONE);
        tvText.setVisibility(View.GONE);
        tvTitle.setText("STICKER");
        imageView.setImageURI(uri);


        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });


        dialog.show();

    }


}
