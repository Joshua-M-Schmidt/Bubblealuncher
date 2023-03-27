package source.nova.com.bubblelauncherfree;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity.CLOCK_COLOR_KEY;
import static source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity.CLOCK_FONT_KEY;
import static source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity.SET_CLOCK_FONT_KEY;
import static source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity.SHOW_CLOCK_KEY;

public class Theme {
    public static final String APP_BACKGROUND_STYLE_CIRCLE = "circle";
    public static final String APP_BACKGROUND_STYLE_HEXAGON = "hexagon";
    public static final String APP_BACKGROUND_STYLE_SQUARE = "square";
    public static final String APP_BACKGROUND_STYLE_CIRCLE_OUTLINE = "circle-outline";
    public static final String APP_BACKGROUND_STYLE_HEXAGON_OUTLINE = "hexagon-outline";
    public static final String APP_BACKGROUND_STYLE_SQUARE_OUTLINE = "square-outline";

    public static final String PATTERN_BIG_ISLAND = "big_island_pattern";
    public static final String PATTERN_BIG_RECTANGLE = "big_rectangle_pattern";
    public static final String PATTERN_SCATTERED = "scattered_pattern";
    public static final String PATTERN_TWO_ISLANDS = "two_islands";

    public static final String RASTER_STYLE_CHESSBOARD = "chessboard";
    public static final String RASTER_STYLE_HONEYCOMB = "honeycomb";

    public static final String PATTERN_TAG = "pattern_tag";

    public static final String BUBBLE_SIZE_KEY = "bubble_size";
    public static final String BUBBLE_PADDING_KEY = "bubble_padding_key";
    public static final String DISPLAY_BUBBLE_BACKGROUND_KEY = "disable_app_background";
    public static final String CUSTOM_BUBBLE_BACKGROUND_KEY = "custom_app_background";
    public static final String BUBBLE_BACKGROUND_COLOR_KEY = "app_background_color";
    public static final String RASTER_STYLE_KEY = "raster_style";
    public static final String BUBBLE_STYLE_KEY = "app_icon_style";
    public static final String BUBBLE_RATIO_KEY = "icon_background_ratio";
    public static final String CUSTOM_BACK_KEY = "wallpaper_key";
    public static final String BACKGROUND_COLOR_KEY = "background_color";

    // clock

    public static final String CLOCKXPOSITION = "____clockx";
    public static final String CLOCKYPOSITION = "____clocky";

    private String app_background_style;
    private String raster_style;
    private String pattern_type;
    private int back_ratio;
    private int back_color;
    private boolean disable_back_color;
    private boolean use_custom_backround_color;
    private int bubblepadding;
    private int bubblesize;
    private boolean custom_back;
    private int background_color;
    private int clockx;
    private int clocky;
    private int clockFont;
    private int clockColor;
    private boolean showClock;
    private boolean clockCustomFont;

    public Theme(String app_background_style,
                 String raster_style,
                 String pattern_type,
                 int back_ratio,
                 int back_color,
                 boolean disable_back_color,
                 boolean use_custom_backround_color,
                 int bubblepadding,
                 int bubblesize,
                 boolean custom_back,
                 int background_color,
                 int clockx,
                 int clocky,
                 int clockFont,
                 int clockColor,
                 boolean showClock,
                 boolean clockCustomFont) {

        this.background_color = background_color;
        this.custom_back = custom_back;
        this.app_background_style = app_background_style;
        this.pattern_type = pattern_type;
        this.raster_style = raster_style;
        this.back_ratio = back_ratio;
        this.back_color = back_color;
        this.disable_back_color = disable_back_color;
        this.use_custom_backround_color = use_custom_backround_color;
        this.bubblepadding = bubblepadding;
        this.bubblesize = bubblesize;
        this.clockx = clockx;
        this.clocky = clocky;
        this.clockFont = clockFont;
        this.clockColor = clockColor;
        this.showClock = showClock;
        this.clockCustomFont = clockCustomFont;
    }

    public void apply(Context ctx){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = pref.edit();

        editor.putInt(BUBBLE_SIZE_KEY,bubblesize);
        editor.putInt(BUBBLE_PADDING_KEY,bubblepadding);
        editor.putInt(BUBBLE_RATIO_KEY,back_ratio);

        editor.putString(RASTER_STYLE_KEY,raster_style);
        editor.putString(BUBBLE_STYLE_KEY,app_background_style);
        editor.putString(PATTERN_TAG,pattern_type);
        editor.putInt(BUBBLE_BACKGROUND_COLOR_KEY,back_color);

        editor.putBoolean(DISPLAY_BUBBLE_BACKGROUND_KEY,use_custom_backround_color);
        editor.putBoolean(CUSTOM_BUBBLE_BACKGROUND_KEY,disable_back_color);
        editor.putBoolean(CUSTOM_BACK_KEY,custom_back);

        editor.putInt(CLOCKXPOSITION,clockx);
        editor.putInt(CLOCKYPOSITION,clocky);

        editor.putInt(BACKGROUND_COLOR_KEY,background_color);

        editor.putInt(CLOCK_FONT_KEY,clockFont);
        editor.putInt(CLOCK_COLOR_KEY,clockColor);
        editor.putBoolean(SHOW_CLOCK_KEY,showClock);
        editor.putBoolean(SET_CLOCK_FONT_KEY,clockCustomFont);

        editor.commit();
    }

    public static Theme getTheme1(){
        return new Theme(APP_BACKGROUND_STYLE_SQUARE,
                RASTER_STYLE_CHESSBOARD,
                PATTERN_SCATTERED,
                70,
                0x99ffffff,
                true,
                true,
                0,
                50,
                true,
                0,4,1,
                R.font.kimberley,
                0xff444444,
                true,
                false);
    }

    public static Theme getTheme2(){
        return new Theme(APP_BACKGROUND_STYLE_HEXAGON,
                RASTER_STYLE_HONEYCOMB,
                PATTERN_SCATTERED,
                50,
                0,
                false,
                true,
                0,
                75,
                false,0xffffffff,4,1,
                R.font.kimberley,
                0xff000000,
                true,
                false);
    }

    public static Theme getClassicTheme(){
        return new Theme(APP_BACKGROUND_STYLE_CIRCLE,
                RASTER_STYLE_HONEYCOMB,
                PATTERN_BIG_ISLAND,
                70,
                0,
                false,
                true,
                5,
                70,
                false,
                0xff000000,4,0,
                R.font.kimberley,
                0xffffffff,
                true,
                false);
    }

    public static Theme getTheme3(){
        return new Theme(APP_BACKGROUND_STYLE_SQUARE,
                RASTER_STYLE_CHESSBOARD,
                PATTERN_BIG_RECTANGLE,
                70,
                0x4286f4,
                false,
                true,
                5,
                70,
                false,
                0xff000000,3,0,
                R.font.kimberley,
                0xffffffff,
                true,
                true);
    }

    public static Theme getTheme4(){
        return new Theme(APP_BACKGROUND_STYLE_SQUARE,
                RASTER_STYLE_CHESSBOARD,
                PATTERN_TWO_ISLANDS,
                70,
                0xff2d89ef,
                true,
                true,
                5,
                60,
                false,
                0xff1d1d1d,4,0,
                R.font.kimberley,
                0xffffffff,
                true,
                false);
    }

    public static Theme getTheme5(){
        return new Theme(APP_BACKGROUND_STYLE_HEXAGON,
                RASTER_STYLE_HONEYCOMB,
                PATTERN_TWO_ISLANDS,
                50,
                0x99ffffff,
                true,
                true,
                0,
                60,
                true,
                0xff333333,4,1,
                R.font.black_street,
                0xff444444,
                true,
                true);
    }

    public String getApp_background_style() {
        return app_background_style;
    }

    public void setApp_background_style(String app_background_style) {
        this.app_background_style = app_background_style;
    }

    public String getRaster_style() {
        return raster_style;
    }

    public void setRaster_style(String raster_style) {
        this.raster_style = raster_style;
    }

    public int getBack_ratio() {
        return back_ratio;
    }

    public void setBack_ratio(int back_ration) {
        this.back_ratio = back_ration;
    }

    public int getBack_color() {
        return back_color;
    }

    public void setBack_color(int back_color) {
        this.back_color = back_color;
    }

    public boolean isDisable_back_color() {
        return disable_back_color;
    }

    public void setDisable_back_color(boolean disable_back_color) {
        this.disable_back_color = disable_back_color;
    }

    public boolean isUse_custom_backround_color() {
        return use_custom_backround_color;
    }

    public void setUse_custom_backround_color(boolean use_custom_backround_color) {
        this.use_custom_backround_color = use_custom_backround_color;
    }

    public int getBubblepadding() {
        return bubblepadding;
    }

    public void setBubblepadding(int bubblepadding) {
        this.bubblepadding = bubblepadding;
    }

    public int getBubblesize() {
        return bubblesize;
    }

    public void setBubblesize(int bubblesize) {
        this.bubblesize = bubblesize;
    }
}
