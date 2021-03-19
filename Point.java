import java.util.Scanner;
public class Point{
    int x;
    int y;
    String id;

    Point(int x1, int y1, String id1){
        x = x1;
        y = y1;
        id = id1.toUpperCase();
    }

    public void toArrayPoint(Point[] points, int n){
        Scanner in = new Scanner(System.in);
        points = new Point[n]; // Array de pontos 

        for (int i = 0; i < n; i++) { // Inserir os novos pontos no Array
            Point point = new Point(in.nextInt(), in.nextInt(), in.next());
            points[i] = point;
        }
    }

    public String toString(){
        return id + ": " + "(" + x + "," + y + ")";
    }
}