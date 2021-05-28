package com.barber.app.adaptor;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class OverlapDecorationForWorkerListSlider extends RecyclerView.ItemDecoration {

    private final static int vertOverlap = 40;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int itemPosition = parent.getChildAdapterPosition(view);
        outRect.set(0, 0, vertOverlap, 0);
    }

}
