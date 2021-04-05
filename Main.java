import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PoligSolver p1 = new PoligSolver(in);
        //PoligSolver p1 = new PoligSolver(60, 20);
        MyPoint[] save = p1.state;

        System.out.println(p1.details());
        p1.ACO2(1000, 5, 1, 5, 500, 0.5, true);
        System.out.println(p1.details());
        System.out.println(p1.interCount()+" "+p1.perimeterCount());
        //p1.showGraph(1920, 1080, 25);

        p1.state = save;
        p1.ACO2(1000,5,1,5,500,0.5,false);
        System.out.println(p1.details());
        System.out.println(p1.interCount()+" "+p1.perimeterCount());
        //p1.showGraph(1920, 1080, 25);
    }
}
/*
public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        ArrayList<Integer> intersections = new ArrayList<>();
        System.out.println("Exemplo 1");
        PoligSolver Exemplo1 = new PoligSolver(in);
        System.out.println(Exemplo1.interCount(intersections));
        System.out.print("Intersections Points: ");
        for(int i=0;i<intersections.size();i+=2)
            System.out.print( (intersections.get(i)+1) +" "+ (intersections.get(i+1)+1)+"|");
        System.out.println();

        intersections.clear();
        System.out.println("Exemplo 2");
        PoligSolver Exemplo2 = new PoligSolver(in);
        System.out.println(Exemplo2.interCount(intersections));
        System.out.print("Intersections Points: ");
        for(int i=0;i<intersections.size();i+=2)
            System.out.print( (intersections.get(i)+1) +" "+ (intersections.get(i+1)+1)+"|");
        System.out.println();
    }
*/