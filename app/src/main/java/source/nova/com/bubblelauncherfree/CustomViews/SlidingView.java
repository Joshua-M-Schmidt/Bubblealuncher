package source.nova.com.bubblelauncherfree.CustomViews;

import android.content.Context;
import androidx.slidingpanelayout.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class SlidingView extends SlidingPaneLayout {
    // ===========================================================
// Constructors
// ===========================================================
    public SlidingView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public SlidingView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public SlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    // ===========================================================
// Methods for/from SuperClass/Interfaces
// ===========================================================
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.i("MySlidingPanelLayout", "onTouch:");
        if (this.isOpen()) {
            this.closePane();
        }
        return false; // here it returns false so that another event's listener
        // should be called, in your case the MapFragment
        // listener
    }
}

