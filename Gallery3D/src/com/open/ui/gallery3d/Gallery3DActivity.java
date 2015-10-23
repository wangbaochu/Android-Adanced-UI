package com.open.ui.gallery3d;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;

public class Gallery3DActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        updateGallery1();
        updateGallery2();
    }

    private void updateGallery1() {
        Integer[] images = { R.drawable.a, R.drawable.b, R.drawable.c, R.drawable.d, R.drawable.e, R.drawable.f, R.drawable.g, R.drawable.h, R.drawable.i };
        ImageAdapter adapter = new ImageAdapter(this, images, 110, 184);
        adapter.createReflectedImages();
        GalleryFlow galleryFlow = (GalleryFlow) this.findViewById(R.id.image_gallery_1);
        galleryFlow.setFadingEdgeLength(0);
        galleryFlow.setSpacing(50);
        galleryFlow.setBackgroundColor(Color.GRAY);
        galleryFlow.setGravity(Gravity.CENTER_VERTICAL);
        galleryFlow.setAdapter(adapter);
        galleryFlow.setSelection(4);
    }
    
    private void updateGallery2() {
        Integer[] images = { R.raw.a, R.raw.b, R.raw.c, R.raw.d, R.raw.e, R.raw.a, R.raw.b, R.raw.c, R.raw.d, R.raw.e  };
        ImageAdapter adapter = new ImageAdapter(this, images, 200, 400);
        adapter.createReflectedImages();
        GalleryFlow galleryFlow = (GalleryFlow) this.findViewById(R.id.image_gallery_2);
        galleryFlow.setFadingEdgeLength(0);
        galleryFlow.setSpacing(-50);
        galleryFlow.setAdapter(adapter);
        galleryFlow.setSelection(4);
    }
}

