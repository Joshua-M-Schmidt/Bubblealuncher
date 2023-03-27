package source.nova.com.bubblelauncherfree.Clock;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.widget.TextViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static androidx.core.widget.TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM;
import static source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity.CLOCK_SHADOW_RADIUS_KEY;
import static source.nova.com.bubblelauncherfree.Theme.APP_BACKGROUND_STYLE_CIRCLE;
import static source.nova.com.bubblelauncherfree.Theme.APP_BACKGROUND_STYLE_HEXAGON;
import static source.nova.com.bubblelauncherfree.Theme.BUBBLE_STYLE_KEY;

public class Clock extends LinearLayout {

    public static final String CLOCK_POSITION_KEY_X = "clock_position_key_x";
    public static final String CLOCK_POSITION_KEY_Y = "clock_position_key_y";

    private Calendar sCalendar;
    private TextClock clockText;
    private TextView clockDate;
    private String clockSize;
    private int asize;
    private int padding;
    private SimpleDateFormat sdf;

    private RotateAnimation shake_anim;
    private Random r = new Random();

    public Clock(Context context, int asize, int padding) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.clock, this);

        shake_anim = new RotateAnimation(
                -2f,
                2f,
                getWidth()/2,getHeight()/2
        );
        shake_anim.setDuration(100);
        shake_anim.setStartOffset((long)r.nextInt(50));
        shake_anim.setRepeatCount(Animation.INFINITE);
        shake_anim.setRepeatMode(Animation.REVERSE);

        this.asize = asize;
        this.padding = padding;
        this.clockSize = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("clock_sizes","large 4x3");
        Log.i("clock","new "+this.clockSize);
        this.clockText = findViewById(R.id.clock);
        this.clockDate = findViewById(R.id.clock_date);
        this.sCalendar = Calendar.getInstance();
        this.sdf = new SimpleDateFormat("EEEE, MMM dd, yyyy");

        setClockSize();
        setText();
        initShadow();
        initClockColor();
        initClockFont();

        clockDate.setText(sdf.format(sCalendar.getTime()));

        TextViewCompat.setAutoSizeTextTypeWithDefaults(clockText, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        TextViewCompat.setAutoSizeTextTypeWithDefaults(clockDate, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);

        //clockText.setTextSize(TypedValue.COMPLEX_UNIT_PX,asize*2.5f);

        this.bringToFront();
    }

    public void shake(){
        this.startAnimation(shake_anim);
    }

    public void stopShake(){
        this.shake_anim.cancel();
    }


    public static void setClockPosition(Point position,Context ctx){
        SharedPreferences sp;
        SharedPreferences.Editor editor;
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        editor = sp.edit();
        editor.putInt(CLOCK_POSITION_KEY_X, position.x);
        editor.putInt(CLOCK_POSITION_KEY_Y, position.y);
        editor.commit();
    }

    public static Point getClockPosition(Context ctx){
        SharedPreferences sp;
        sp = PreferenceManager.getDefaultSharedPreferences(ctx);
        Point position = new Point(sp.getInt(CLOCK_POSITION_KEY_X,0),sp.getInt(CLOCK_POSITION_KEY_Y,0));
        return position;
    }

    private void initClockFont(){
        boolean font = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(ClockSettingActivity.SET_CLOCK_FONT_KEY,false);
        if(font){
            int clock_font = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(ClockSettingActivity.CLOCK_FONT_KEY, R.font.black_street);
            Typeface tface = ResourcesCompat.getFont(getContext(), clock_font);
            clockText.setTypeface( tface );
            clockDate.setTypeface( tface );
        }
    }

    private void initClockColor(){
        int clock_color = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(ClockSettingActivity.CLOCK_COLOR_KEY,0xffffffff);
        clockText.setTextColor(clock_color);
        clockDate.setTextColor(clock_color);
    }

    private void initShadow(){
        boolean shadow = PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(ClockSettingActivity.CLOCKSHADOWKEY,true);
        float shadow_Radius = PreferenceManager.getDefaultSharedPreferences(getContext()).getFloat(CLOCK_SHADOW_RADIUS_KEY,10f);
        int shadow_color = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(ClockSettingActivity.CLOCKSHADOWCOLORKEY,0xff000000);
        if(shadow){
            clockText.setShadowLayer(shadow_Radius, 0, 0, shadow_color);
            clockDate.setShadowLayer(shadow_Radius/4, 0, 0, shadow_color);
        }
    }

    public void setText(){

        Point clocksize = getClockSize(getContext());
        final int w = clocksize.x;

        clockDate.setText(sdf.format(sCalendar.getTime()));
        clockText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                clockDate.setText(sdf.format(sCalendar.getTime()));

                TextViewCompat.setAutoSizeTextTypeWithDefaults(clockText, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
                TextViewCompat.setAutoSizeTextTypeWithDefaults(clockDate, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void setSize(int w, int h){
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w,h);
        this.setPadding(0,0,0,0);
        this.setLayoutParams(
                params
        );
    }

    public String getClockSizeString(){
        String clockSize = PreferenceManager.getDefaultSharedPreferences(getContext()).getString("clock_sizes","large 4x3");
        return clockSize;
    }

    public static Point getClockSize(Context ctx){

        int asize = Util.getDiam(ctx);
        int padding = Util.getPadding(ctx);
        String clockSize = PreferenceManager.getDefaultSharedPreferences(ctx).getString("clock_sizes","large 4x3");

        Point size3;

        int w;
        int h;

        if(clockSize.equals("large 4x3")){
            size3 = Util.rasterToPixel(new Point(3,3),asize, padding, ctx);
            w = size3.x-padding+asize/2;
            h = size3.y-padding*2;
            if(!PreferenceManager.getDefaultSharedPreferences(ctx).getString("raster_style","honeycomb").equals("honeycomb")){
                w = size3.x-padding+asize;
            }


        }else if(clockSize.equals("small 1x2")){
            size3 = Util.rasterToPixel(new Point(2,1),asize, padding, ctx);
            w = size3.x+padding;
            h = size3.y-padding;
        }else{
            size3 = Util.rasterToPixel(new Point(1,1),asize, padding, ctx);
            Log.i("clock size",size3.toString());
            w = size3.x;
            h = size3.y-padding;
        }

        return new Point(w,h);
    }

    public void setClockSize(){
        Point size3;

        int w;
        int h;

        boolean small = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(BUBBLE_STYLE_KEY,APP_BACKGROUND_STYLE_CIRCLE).equals(APP_BACKGROUND_STYLE_HEXAGON);

        if(clockSize.equals("large 4x3")){
            size3 = Util.rasterToPixel(new Point(3,3),asize, padding, getContext());
            w = size3.x-padding+asize/2;
            h = size3.y-padding*2;
            if(!PreferenceManager.getDefaultSharedPreferences(getContext()).getString("raster_style","honeycomb").equals("honeycomb")){
                w = size3.x-padding+asize;
            }
            if(small){
                //clockText.setTextSize(TypedValue.COMPLEX_UNIT_PX,asize*1f);
                //clockDate.setTextSize(TypedValue.COMPLEX_UNIT_PX,asize*0.3f);
            }else{
                //clockText.setTextSize(TypedValue.COMPLEX_UNIT_PX,asize*1.5f);
                //clockDate.setTextSize(TypedValue.COMPLEX_UNIT_PX,asize*0.3f);
            }

        }else if(clockSize.equals("small 1x2")){
            size3 = Util.rasterToPixel(new Point(2,1),asize, padding, getContext());
            w = size3.x+padding;
            h = size3.y-padding;
            //clockText.setTextSize(TypedValue.COMPLEX_UNIT_PX,asize*0.7f);
            clockDate.setVisibility(GONE);
        }else{
            size3 = Util.rasterToPixel(new Point(1,1),asize, padding, getContext());
            Log.i("clock size",size3.toString());
            w = size3.x;
            h = size3.y-padding;
            //clockText.setTextSize(TypedValue.COMPLEX_UNIT_PX,asize*0.35f);
            clockDate.setVisibility(GONE);
        }


        setSize(w,h);
    }
}