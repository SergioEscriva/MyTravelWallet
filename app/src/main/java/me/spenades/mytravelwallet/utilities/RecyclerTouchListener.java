package me.spenades.mytravelwallet.utilities;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

    private final ClickListener clickListener;
    private final GestureDetector gestureDetector;


    public RecyclerTouchListener(Context context, final RecyclerView recyclerView,
                                 final ClickListener clickListener) {
        this.clickListener = clickListener;
        this.gestureDetector = new GestureDetector(context,
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onSingleTapUp(MotionEvent e) {
                        return true;
                    }


                    @Override
                    public void onLongPress(MotionEvent e) {
                        View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                        if (child != null && clickListener != null) {
                            clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                        }
                    }
                });
    }


    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView,
                                         @NonNull MotionEvent motionEvent) {
        View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(motionEvent)) {
            clickListener.onClick(child, recyclerView.getChildAdapterPosition(child));
        }
        return false;
    }


    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }


    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }


    public abstract void onClick(View view, int position);


    public interface ClickListener {

        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }

}
