import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PoligSolver p1 = new PoligSolver(40,20);
        System.out.println(p1.details());
        MyPoint[] save = p1.state.clone();
        
        p1.simulatedAnnealing(100000, 0.995);
        System.out.println(p1.interCount());
        
        p1.state = save;
        p1.ACO(100, 20, 1, 5, 500, 0.5, true);
        System.out.println(p1.interCount());

    }
}

/*
PARA RESULTADOS
Para ajudar a mostrar que quando maxIter aumenta a função converge
public static void main(String[] args) {
        double avg = 0;
        for(int i=0;i<40;i++){
            PoligSolver p1 = new PoligSolver(40,20);
            MyPoint[] save = p1.state;
            p1.ACO2(100, 10, 1, 5, 500, 0.5, true);
            int perimeter1 = p1.perimeterCount();
            p1.state = save;
            p1.ACO2(10, 10, 1, 5, 500, 0.5, true);
            int perimeter2 = p1.perimeterCount();
            avg += (perimeter1 - perimeter2) / ((double)perimeter1+perimeter2);
        }
        System.out.println((avg/20)*100);
    } 
*/