import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PoligSolver p1 = new PoligSolver(40,20);
        //PoligSolver p1 = new PoligSolver(in);
        System.out.println(p1);
        System.out.println(p1.details());
        p1.hillClimbing('a');
        System.out.println(p1.details());
        p1.showGraph(1366,768,10);
        //p1.ACO(4, 1, 5, 500, 0.5);
    }

}
