package source.nova.com.bubblelauncherfree.CustomViews;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.updateFolderPosition;

/**
 * Created by joshua on 17.10.17.
 */

public class FolderView extends androidx.appcompat.widget.AppCompatImageView{

    public String folderName;
    private Point position;
    private Float displayScale;
    private RotateAnimation shake_anim;
    private Random r = new Random();
    public ArrayList<String> appsContained;

    public FolderView(Context context, String folderName, Point position, ArrayList<String> appsContained) {
        super(context);

        this.folderName = folderName;
        this.position = position;
        this.appsContained = appsContained;

        setLeft(position.y);
        setTop(position.x);

        shake_anim = new RotateAnimation(
                -2.5f,
                2.5f,
                MainActivity.asize/4,MainActivity.asize/4
        );

        shake_anim.setDuration(100);
        shake_anim.setStartOffset((long)r.nextInt(50));
        shake_anim.setRepeatCount(Animation.INFINITE);
        shake_anim.setRepeatMode(Animation.REVERSE);
    }

    public ArrayList<String> getAppsContained(){
        return appsContained;
    }

    public String getFolderName() {
        return folderName;
    }

    public void shake(){
        this.startAnimation(shake_anim);
    }

    public void stopShake(){
        this.shake_anim.cancel();
    }

    public Point getPosition(){
        return this.position;
    }

    public void setPosition(Point position, Context ctx){
        this.position = position;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MainActivity.asize,MainActivity.asize);

        Point newPxP = Util.rasterToPixel(position,MainActivity.asize,MainActivity.padding,ctx);

        params.leftMargin = newPxP.x;
        params.topMargin = newPxP.y;

        setLayoutParams(
                params
        );

        updateFolderPosition(position,getContext(),getFolderName());

    }

    public void setAppScale(Float appScale) {
        this.displayScale = appScale;
    }
}
