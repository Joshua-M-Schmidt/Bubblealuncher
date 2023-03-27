package source.nova.com.bubblelauncherfree.Wigets;

public class WidgetInfo {
    private int x;
    private int y;
    private String tag;
    private int w;
    private int h;

    public WidgetInfo(int x, int y, String tag, int w, int h) {
        this.x = x;
        this.y = y;
        this.tag = tag;
        this.w = w;
        this.h = h;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getH() {
        return h;
    }

    public void setH(int h) {
        this.h = h;
    }
}
