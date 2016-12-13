package com.mcucsie.flippedclass.group;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class HSVLayout extends LinearLayout {
    private Context mContext;

    public HSVLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public void setAdapter(PhotoGalleryAdapter adapter) {
        for (int i = 0; i < adapter.getCount(); i++) {
            
            View view = adapter.getView(i, null, null);
            view.setPadding(10, 0, 10, 0);
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
               
                    mContext.sendBroadcast(intent);
                }
            });
            this.setOrientation(HORIZONTAL);
            this.addView(view, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }
    }
}
