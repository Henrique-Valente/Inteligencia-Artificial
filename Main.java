import java.util.*;
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PoligSolver p1 = new PoligSolver(30,10);
        //PoligSolver p1 = new PoligSolver(in);
        System.out.println(p1);
        System.out.println(p1.details());
        p1.hillClimbing('c');
        System.out.println(p1.details());
        p1.showGraph(1920,1080,100);
    }
    
}
