package gr.kalymnos.sk3m3l10.greekpodcasts.android_views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;

public class CardView_5_4 extends CardView {
    public CardView_5_4(Context context) {
        super(context);
    }

    public CardView_5_4(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardView_5_4(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int preferedHeight = MeasureSpec.getSize(widthMeasureSpec) * 4/5;
        int preferedHeightSpec = MeasureSpec.makeMeasureSpec(preferedHeight,MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, preferedHeightSpec);
    }
}
