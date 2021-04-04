import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PoligSolver p1 = new PoligSolver(60,30);
        //PoligSolver p1 = new PoligSolver(in);
        System.out.println(p1.details());
        
        MyPoint[] original = p1.state;

        p1.ACO2(1000,10, 1, 5, 500, 0.7, true);
        //taxas de evaporação baixas e alfa baixos parecem ser mais beneficos
        //aumentar o numero kAnts n parece mudar muito

        System.out.println(p1.details());
        System.out.println(p1.perimeterCount() + " " + p1.interCount());
        p1.showGraph(1366,768,10);

        p1.state = original;
        p1.ACO2(10,60, 1, 5, 500, 0.7, false);

        System.out.println(p1.details());
        System.out.println(p1.perimeterCount() + " " + p1.interCount());
        p1.showGraph(1366,768,10);

    }
}