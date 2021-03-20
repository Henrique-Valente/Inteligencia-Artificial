import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.List;
import java.util.Random;

public class Testing {
    static boolean visited[];
    static int n;
    static int count = 0;
    static Point[] point;

    public static Point[] perm(Point[] arr) {     // Função que devolve uma permutação de Array de Pontos
        Point[] save = new Point[arr.length];
        save = arr.clone();
        List<Point> pointList = Arrays.asList(save);
        Collections.shuffle(pointList);
        pointList.toArray(save);

        return save;
    }

    public static Point[] toArrayPoint(Point[] points, int n, Scanner in){
        points = new Point[n];        // Array de pontos 
        for (int i = 0; i < n; i++) { // Inserir os novos pontos no Array
            Point point = new Point(in.nextInt(), in.nextInt(), in.next());
            points[i] = point;
        }
        return points;
    }


    // Nearest neighbour daqui para a frente
    public static int selectRandom(int length){
        Random r = new Random();                        //Definir número aleatório
        return r.nextInt(length);                       //Obter número aleatório e devolver
    }

    private static boolean check(){                     //Verifica se ainda há pontos por visitar
        for(int i=0; i<n;i++){
            if(visited[i] == false) return true;
        }

        return false;
    }

    private static int search(int v){                  //Procura o ponto mais próximo de V
        double closest = 0;
        int entered = 0;  //Serve para definir o primeiro visitado
        int tovisit = 0;
        visited[v] = true;
        for(int i=0; i<n; i++){
            if(!visited[i]){

                if(entered == 0){
                    entered = 1;
                    closest = point[v].distance(point[i]);
                    tovisit = i;
                }

                else if(closest > point[v].distance(point[i])){
                    closest = point[v].distance(point[i]);
                    tovisit = i;
                }
            }
        }

        return tovisit;
    }

    public static Point[] nearestNeib(Point[] arr) {    //Cria um array de Point que contém o output   
        Point[] save = new Point[n];                    //do NN (E devolve)
        visited = new boolean[n];
        int start = selectRandom(n);
        save[0] = arr[start];
        count++;
         
        while(check()){
            if(count == n) break;
            start = search(start);
            save[count] = arr[start];
            count++;
        }
        
        return save;
    } 


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        point = new Point[n];
        point = toArrayPoint(point, n, in);
         
        point = nearestNeib(point); 

        for(int i = 0; i<n ; i++) System.out.print(point[i].id + " ");
        System.out.println();
    }
}