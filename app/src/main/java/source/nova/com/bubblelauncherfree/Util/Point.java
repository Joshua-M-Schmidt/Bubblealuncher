package source.nova.com.bubblelauncherfree.Util;

/**
 * Created by joshua on 07.09.16.
 */
public class Point{
    public int x;
    public int y;

    public Point(int x, int y){
        this.x = x;
        this.y = y;
    }


    public int distance(Point b){
        int dx = x - b.x;
        int dy = y - b.y;

        return (int) Math.sqrt( (dx * dx) + (dy * dy) );
    }

    public boolean equals(Point p){
        if(p.x == this.x && p.y == this.y){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public String toString(){
        return "("+this.x+","+this.y+")";
    }
}