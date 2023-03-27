package source.nova.com.bubblelauncherfree.StartConfigActivities;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.Util.Point;

public class Layout {
    private String name;
    private ArrayList<Point> appPositions;
    private int titleImageID;

    private boolean showClock;
    private Point clockPosition;
    private String ClockType;

    boolean selected;

    public Layout(String name, ArrayList<Point> appPositions, int titleImageID, boolean showClock, Point clockPosition, String clockType) {
        this.name = name;
        this.appPositions = appPositions;
        this.titleImageID = titleImageID;
        this.showClock = showClock;
        this.clockPosition = clockPosition;
        ClockType = clockType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Point> getAppPositions() {
        return appPositions;
    }

    public void setAppPositions(ArrayList<Point> appPositions) {
        this.appPositions = appPositions;
    }

    public int getTitleImage() {
        return titleImageID;
    }

    public void setTitleImageID(int titleImage) {
        this.titleImageID = titleImage;
    }

    public boolean isShowClock() {
        return showClock;
    }

    public void setShowClock(boolean showClock) {
        this.showClock = showClock;
    }

    public Point getClockPosition() {
        return clockPosition;
    }

    public void setClockPosition(Point clockPosition) {
        this.clockPosition = clockPosition;
    }

    public String getClockType() {
        return ClockType;
    }

    public void setClockType(String clockType) {
        ClockType = clockType;
    }
}
