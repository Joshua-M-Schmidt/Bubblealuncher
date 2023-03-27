package source.nova.com.bubblelauncherfree.Wigets;

import android.appwidget.AppWidgetHostView;
import android.content.Context;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import java.util.Random;

import source.nova.com.bubblelauncherfree.MainActivity;

public class WidgetView extends AppWidgetHostView {
    private OnTouchListener _onTouchListener;
    private OnLongClickListener _longClick;
    private RotateAnimation shake_anim;
    private Random r = new Random();
    private long _down;

    public WidgetView(Context context) {
        super(context);

        shake_anim = new RotateAnimation(
                -2f,
                2f,
                getWidth()/2,getHeight()/2
        );
        shake_anim.setDuration(100);
        shake_anim.setStartOffset((long)r.nextInt(50));
        shake_anim.setRepeatCount(Animation.INFINITE);
        shake_anim.setRepeatMode(Animation.REVERSE);
    }

    public void shake(){
        this.startAnimation(shake_anim);
    }

    public void stopShake(){
        this.shake_anim.cancel();
    }

    @Override
    public void setOnTouchListener(OnTouchListener onTouchListener) {
        _onTouchListener = onTouchListener;
    }

    @Override
    public void setOnLongClickListener(OnLongClickListener l) {
        _longClick = l;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (_onTouchListener != null)
            _onTouchListener.onTouch(this, ev);
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                _down = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_MOVE:
                boolean upVal = System.currentTimeMillis() - _down > 500L;
                if (upVal) {
                    _longClick.onLongClick(this);
                }
                break;
        }
        return false;
    }
}
