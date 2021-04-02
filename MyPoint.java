import java.util.Random;
public class MyPoint{
    int x;
    int y;
    String id;

    public static MyPoint random(int m, int tag){
        Random rand = new Random();
        MyPoint ponto = new MyPoint(-m + rand.nextInt(m*2+1),-m + rand.nextInt(m*2+1),"P" + tag);
        return ponto;
    }

    // subtract 2 points and returns a new Point
    public static MyPoint subPoint(MyPoint a, MyPoint b){
        int x = a.x - b.x;
        int y = a.y - b.y;
        return new MyPoint(x,y,"");
    }
    
    MyPoint(int x1, int y1, String id1){
        x = x1;
        y = y1;
        id = id1.toUpperCase();
    }

    public int distance(MyPoint b){
        return (b.x - this.x)*(b.x - this.x) + (b.y - this.y)*(b.y - this.y);
    }

    public String toString(){
        return id + ":" + "(" + x + "," + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof MyPoint)) return false;
        MyPoint c = (MyPoint) obj;
        return this.x == c.x && this.y == c.y;
    }
}