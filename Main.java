import java.util.*;

public class Main {
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
}