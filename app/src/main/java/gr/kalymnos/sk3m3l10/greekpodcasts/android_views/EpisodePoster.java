package gr.kalymnos.sk3m3l10.greekpodcasts.android_views;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class EpisodePoster extends AppCompatImageView {
    public EpisodePoster(Context context) {
        super(context);
    }

    public EpisodePoster(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EpisodePoster(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int threeTwoHeight = MeasureSpec.getSize(widthMeasureSpec) * 2 / 3;
        int threeTwoHeightSpec = MeasureSpec.makeMeasureSpec(threeTwoHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, threeTwoHeightSpec);
    }

}
