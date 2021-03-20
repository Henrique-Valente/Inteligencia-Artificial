public class Point{
    int x;
    int y;
    String id;

    Point(int x1, int y1, String id1){
        x = x1;
        y = y1;
        id = id1.toUpperCase();
    }

    public double distance(Point b){
        return Math.pow((double)b.x - (double)this.x, 2) + Math.pow((double)b.y - (double)this.y, 2);
    }

    public String toString(){
        return id + ": " + "(" + x + "," + y + ")";
    }
}