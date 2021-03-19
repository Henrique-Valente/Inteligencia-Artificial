import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.List;

public class Testing {
    public static void perm(Point[] arr) {     // Função que devolve uma permutação de Array de Pontos
        Point[] save = new Point[arr.length];
        save = arr;
        List<Point> pointList = Arrays.asList(save);
        Collections.shuffle(pointList);
        pointList.toArray(save);

        for (int i = 0; i < arr.length; i++)
            System.out.print(save[i].id + " ");

        System.out.println();
    }

    public static Point[] toArrayPoint(Point[] points, int n, Scanner in){
        points = new Point[n]; // Array de pontos 
        for (int i = 0; i < n; i++) { // Inserir os novos pontos no Array
            Point point = new Point(in.nextInt(), in.nextInt(), in.next());
            points[i] = point;
        }
        return points;
    }

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Point[] point = new Point[n];
        point = toArrayPoint(point, n, in);

        for (int i = 0; i < n; i++)
            System.out.println(point[i]);

        perm(point); // 2 a)
    }
}