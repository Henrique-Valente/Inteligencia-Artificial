import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PoligSolver p1 = new PoligSolver(60,20);
        //PoligSolver p1 = new PoligSolver(in);
        System.out.println(p1.details());
        //p1.hillClimbing('b');
        p1.ACO2(100,10, 1, 5, 500, 0.3);
        //taxas de evaporação baixas e alfa baixos parecem ser mais beneficos
        //aumentar o numero kAnts n parece mudar muito
        System.out.println(p1.details());
        p1.showGraph(1366,768,10);
    }
}