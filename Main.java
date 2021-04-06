import java.util.*;
 
public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PoligSolver Exemplo1 = new PoligSolver(in);
        PoligSolver Exemplo2 = new PoligSolver(in);

        Exemplo1.ACO(10000, 5, 1, 5, 500, 0.5, true);
        Exemplo2.simulatedAnnealing(100000, 0.995);

        System.out.println(Exemplo1.interCount() + " " + Exemplo1.perimeterCount());
        
        ArrayList<Integer> intersectionsPoints = new ArrayList<>();
        System.out.println(Exemplo2.interCount(intersectionsPoints) + " "+ Exemplo2.perimeterCount());

        int xres=1366, yres=768, scale = 10;
        Exemplo1.showGraph(xres, yres, scale);
        Exemplo2.showGraph(xres, yres, scale);
    }
}