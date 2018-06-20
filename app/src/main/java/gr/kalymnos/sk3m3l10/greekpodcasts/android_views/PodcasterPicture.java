package gr.kalymnos.sk3m3l10.greekpodcasts.android_views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class PodcasterPicture extends AppCompatImageView {
    public PodcasterPicture(Context context) {
        super(context);
    }

    public PodcasterPicture(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PodcasterPicture(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // This view has aspect ratio of 6:5.
        int preferedHeight = MeasureSpec.getSize(widthMeasureSpec) * 5/6;
        int preferedHeightSpec = MeasureSpec.makeMeasureSpec(preferedHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, preferedHeightSpec);
    }
}
