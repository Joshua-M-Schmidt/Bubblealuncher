package source.nova.com.bubblelauncherfree.Util;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

import java.util.Random;

import source.nova.com.bubblelauncherfree.Clock.Clock;

import static source.nova.com.bubblelauncherfree.MainActivity.X_START_KEY;
import static source.nova.com.bubblelauncherfree.MainActivity.Y_START_KEY;
import static source.nova.com.bubblelauncherfree.Theme.APP_BACKGROUND_STYLE_CIRCLE;
import static source.nova.com.bubblelauncherfree.Theme.APP_BACKGROUND_STYLE_HEXAGON;
import static source.nova.com.bubblelauncherfree.Theme.BUBBLE_STYLE_KEY;
import static source.nova.com.bubblelauncherfree.Theme.CLOCKXPOSITION;
import static source.nova.com.bubblelauncherfree.Theme.RASTER_STYLE_CHESSBOARD;
import static source.nova.com.bubblelauncherfree.Theme.RASTER_STYLE_HONEYCOMB;
import static source.nova.com.bubblelauncherfree.Theme.RASTER_STYLE_KEY;

/**
 * Created by joshua on 07.07.16.
 */
public class Util {
    public static int getDominantColor(Bitmap bitmap, Context ctx) {
        if (null == bitmap) return Color.TRANSPARENT;

        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;

        boolean hasAlpha = bitmap.hasAlpha();
        int pixelCount = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[pixelCount];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int y = 0, h = bitmap.getHeight(); y < h; y++)
        {
            for (int x = 0, w = bitmap.getWidth(); x < w; x++)
            {
                int color = pixels[x + y * w]; // x + y * width
                redBucket += (color >> 16) & 0xFF; // Color.red
                greenBucket += (color >> 8) & 0xFF; // Color.greed
                blueBucket += (color & 0xFF); // Color.blue
            }
        }

        //redBucket *= 20;
        //greenBucket *=20;
        //blueBucket *= 20;

        float[] hsv = new float[3];

        String colorType = PreferenceManager.getDefaultSharedPreferences(ctx).getString("app_icon_color_theme","neon (classic)");
        if(colorType.equals("neon (classic)")){
            Color.RGBToHSV(redBucket / pixelCount,greenBucket / pixelCount,blueBucket / pixelCount, hsv);
            hsv[1] = 1f;
            hsv[2] = 1f;
            return Color.HSVToColor(hsv);
        }else if(colorType.equals("pastel")){
            Color.RGBToHSV(redBucket / pixelCount,greenBucket / pixelCount,blueBucket / pixelCount, hsv);
            hsv[1] = 0.5f;
            hsv[2] = 1f;
            return Color.HSVToColor(hsv);
        }else{
            int Gray = (redBucket / pixelCount + greenBucket / pixelCount + blueBucket / pixelCount) / 3;
            return Color.rgb(Gray,Gray,Gray);
        }
    }

    public static int getDominantColorNeon(Bitmap bitmap) {
        if (null == bitmap) return Color.TRANSPARENT;

        int redBucket = 0;
        int greenBucket = 0;
        int blueBucket = 0;

        boolean hasAlpha = bitmap.hasAlpha();
        int pixelCount = bitmap.getWidth() * bitmap.getHeight();
        int[] pixels = new int[pixelCount];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        for (int y = 0, h = bitmap.getHeight(); y < h; y++)
        {
            for (int x = 0, w = bitmap.getWidth(); x < w; x++)
            {
                int color = pixels[x + y * w]; // x + y * width
                redBucket += (color >> 16) & 0xFF; // Color.red
                greenBucket += (color >> 8) & 0xFF; // Color.greed
                blueBucket += (color & 0xFF); // Color.blue
            }
        }

        //redBucket *= 20;
        //greenBucket *=20;
        //blueBucket *= 20;

        float[] hsv = new float[3];

        Color.RGBToHSV(redBucket / pixelCount,greenBucket / pixelCount,blueBucket / pixelCount, hsv);

        hsv[1] = 1;
        hsv[2] = 100;

        Color.rgb(
                redBucket / pixelCount,
                greenBucket / pixelCount,
                blueBucket / pixelCount);

        return Color.HSVToColor(hsv);
    }

    public static float dipToPixels(Context context, float dipValue) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, metrics);
    }

    public static int getRandomInt(int min, int max){
        Random r = new Random();
        return r.nextInt(max - min + 1) + min;
    }

    public static int getDiam(Context ctx){
        int ret = (int) dipToPixels(ctx,
                PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getInt("bubble_size", 70));
        if(ret < 10){
            ret = 10;
        }
        return ret;
    }

    public static int getPadding(Context ctx){
        return  (int) Util.dipToPixels(ctx,
                PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getInt("bubble_padding_key", 5));
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static Point pixelToRaster(Point pixelPoint, int diam, int padding, Context ctx){
        if(PreferenceManager.getDefaultSharedPreferences(ctx).getString("raster_style","honeycomb").equals("honeycomb")){



            int packoffset = diam - (int) Math.sqrt(Math.pow(diam,2)- Math.pow(.5*diam,2));
            int packoffsetX = 0;

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("square") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("square-outline")){
                packoffset = 0;
            }else if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                packoffset = (int) getHexOffsetY(diam/2);
            }

            int row = pixelPoint.y / ((diam+padding)-packoffset);

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                diam -= (2*getHexOffsetX(diam/2));
            }

            if(row % 2 == 0){
                int col = pixelPoint.x / (diam+padding);

                return new Point(col,row);
            }else{
                int col = (pixelPoint.x-(diam/2)) / (diam+padding);


                return new Point(col,row);
            }
        }else{
            int row = pixelPoint.y / (diam+padding);

            int col = pixelPoint.x / (diam+padding);

            return new Point(col,row);
        }


    }

    public static double getHexOffsetX(int size){
        double x1 = size + size * Math.cos(((4*(360/6)-30)* Math.PI / 180f));
        Log.i("x offset",x1+" off");
        return x1;
    }

    public static double getHexOffsetY(int size){
        double y1 = size + size * Math.sin(((0*(360/6)-30)* Math.PI / 180f));
        return y1;
    }

    public static boolean isNetworkAvailable(Context ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showNetworkNotAvailableWarning(Context ctx){
        androidx.appcompat.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new androidx.appcompat.app.AlertDialog.Builder(ctx, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new androidx.appcompat.app.AlertDialog.Builder(ctx);
        }
        builder.setTitle("No Internet Connection")
                .setMessage("The sorting works only with an active internet connection")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public static Point rasterToPixelArea(Point rasterPoint, int diam, int padding, Context ctx){

        if(PreferenceManager.getDefaultSharedPreferences(ctx).getString("raster_style","honeycomb").equals("honeycomb")){

            int packoffset = diam - (int) Math.sqrt(Math.pow(diam,2)- Math.pow(.5*diam,2));

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("square") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("square-outline")) {
                packoffset = 0;
            }else if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                packoffset = (int) getHexOffsetY(diam/2);
            }

            int y = rasterPoint.y*(((diam+padding)-packoffset));

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                diam -= (2*getHexOffsetX(diam/2));
            }

            int x = rasterPoint.x * (diam + padding);
            x -= padding;

            return new Point(x,y);
        }else{
            int y = rasterPoint.y*(diam+padding);

            y -= padding;

            int x = rasterPoint.x*(diam+padding);

            x -= padding;

            return new Point(x,y);
        }
    }

    public static Point rasterToPixel(Point rasterPoint, int diam, int padding, Context ctx){

        if(PreferenceManager.getDefaultSharedPreferences(ctx).getString("raster_style","honeycomb").equals("honeycomb")){

            int packoffset = diam - (int) Math.sqrt(Math.pow(diam,2)- Math.pow(.5*diam,2));

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("square") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("square-outline")) {
                packoffset = 0;
            }else if(PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getString("app_icon_style","circle").equals("hexagon") ||
                        PreferenceManager.getDefaultSharedPreferences(ctx)
                                .getString("app_icon_style","circle").equals("hexagon-outline")){
                    packoffset = (int) getHexOffsetY(diam/2);
            }

            int y = rasterPoint.y*(((diam+padding)-packoffset));

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                diam -= (2*getHexOffsetX(diam/2));
            }

            if(rasterPoint.y % 2 == 0){

                int x = rasterPoint.x*(diam+padding);

                return new Point(x,y);
            }else{

                int x = rasterPoint.x*(diam+padding) + (diam/2) +(padding/2);

                return new Point(x,y);
            }
        }else{
            int y = rasterPoint.y*(diam+padding);

            int x = rasterPoint.x*(diam+padding);

            return new Point(x,y);
        }
    }

    public static int[] getScreenDimension(Context ctx){
        WindowManager wm = (WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        return new int[]{width,height};
    }

    public static void centerPointInScreen(Context ctx, Point pasdf){
        if(PreferenceManager.getDefaultSharedPreferences(ctx).getString(BUBBLE_STYLE_KEY,APP_BACKGROUND_STYLE_CIRCLE).equals(APP_BACKGROUND_STYLE_HEXAGON)){
            Point clockSize = Clock.getClockSize(ctx);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            Point newP = rasterToPixel(pasdf,getDiam(ctx),getPadding(ctx),ctx);

            //newP.x += getDiam(ctx)/2;

            int[] scrD = getScreenDimension(ctx);

            int x = newP.x - (scrD[0]/2);


            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(X_START_KEY,x);
            editor.putInt(Y_START_KEY,0);
            editor.commit();
        }else{
            Point clockSize = Clock.getClockSize(ctx);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
            Point p = new Point(preferences.getInt(CLOCKXPOSITION,0),0);
            Point newP = rasterToPixel(p,getDiam(ctx),getPadding(ctx),ctx);
            newP.x += (clockSize.x/2);

            if(!preferences.getString(RASTER_STYLE_KEY,RASTER_STYLE_HONEYCOMB).equals(RASTER_STYLE_CHESSBOARD)){
                //newP.x += getDiam(ctx)/2;
            }

            int[] scrD = getScreenDimension(ctx);

            int x = newP.x - (scrD[0]/2);


            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(X_START_KEY,x);
            editor.putInt(Y_START_KEY,0);
            editor.commit();
        }
    }
}
