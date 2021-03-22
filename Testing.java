import java.util.*;

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

    // Cross product of 2 2d points or determinante of 2 vectors
    private static int crossProduct2d(Point a, Point b){ return a.x*b.y - b.x*a.y; }

    // Returns if c is within the "box" formed by a and b
    private static boolean inBox(Point a, Point b, Point c){
        return Math.min(a.x, b.x) <= c.x && c.x <= Math.max(a.x,b.x) 
            && Math.min(a.y, b.y) <= c.y && c.y <= Math.max(a.y,b.y);
    }

    private static int mod(int k, int m){
        return (k < 0) ? (m - (Math.abs(k) % m) ) %m : (k % m);
    }

    // Receives 2 line segments and returns true if they intersect
    private static boolean segIntersect (Point A, Point B, Point C, Point D){
        int dAB_C, dAB_D, dCD_A, dCD_B;
        dAB_C = crossProduct2d( Point.subPoint(C,A) , Point.subPoint(B,A) );
        dAB_D = crossProduct2d( Point.subPoint(D,A) , Point.subPoint(B,A) );
        dCD_A = crossProduct2d( Point.subPoint(A,C) , Point.subPoint(D,C) );
        dCD_B = crossProduct2d( Point.subPoint(B,C) , Point.subPoint(D,C) );
        if( dAB_C*dAB_D  < 0 && dCD_A* dCD_B < 0 ) return true;
        else if( dAB_C == 0 && inBox(A, B, C) ) return true;
        else if( dAB_D == 0 && inBox(A, B, D) ) return true;
        else if( dCD_A == 0 && inBox(C, D, A) ) return true;
        else if( dCD_B == 0 && inBox(C, D, B) ) return true;
        return false;
    }
    // counts number of intersections in a 2d graph of line segments and saves the points in choices
    // complexidade temporal: O(n^2)
    private static int interCount(Point[] set, ArrayList<Integer> choices){
        int count=0;
        boolean[] visited = new boolean[n];
        for(int i=0;i<n;i++){
            for(int j=mod(i+2,n); mod(j,n)!= mod(i-1,n) ;j++){
                //PERGUNTAR ESTE IF teoricamente daqui para frente todos as retas ja estao verificadas
                if (visited[mod(j,n)] && visited[mod(j+1,n)]) return count;
                if(segIntersect(set[i], set[mod(i+1,n)], set[mod(j,n)], set[mod(j+1,n)]) ) {
                    if(choices != null){
                        choices.add(i);
                        choices.add(mod(j,n));
                    }
                    ++count;
                }
                //boolean v = segIntersect(set[i], set[mod(i+1,n)], set[mod(j,n)], set[mod(j+1,n)]);
                //System.out.println(v + " "+"|"+ set[i].id + ", " +set[mod(i+1,n)].id + ", " + set[mod(j,n)].id + ", " +set[mod(j+1,n)].id);
            }
            visited[i] = true;
            visited[mod(i+1,n)] = true;
        }
        return count;
    }

    //switches 2 points 
    private static void swap(Point[] set, int p1, int p2){
        Point temp;
        // switch points to try and fix the intersection
        temp = set[p1];
        set[p1] = set[p2];
        set[p2] = temp;
    }

    // Hill climbing best improvement step
    private static int hillBestStep(Point[] set){
        ArrayList<Integer> choices = new ArrayList<>();
        Point temp;
        int intersects = interCount(set,choices);
        int p1=0, p2=0, candidate=0,bestChoice = Integer.MAX_VALUE;
        for(int i=0;i<choices.size();i+=2){
            //checking possible candidate
            swap(set, mod(choices.get(i)+1,n), choices.get(i+1));
            candidate = interCount(set, null);
            //restauring original set
            swap(set, mod(choices.get(i)+1,n), choices.get(i+1));
            if(bestChoice > candidate){
                bestChoice = candidate;
                p1 = mod(choices.get(i)+1,n);
                p2 = choices.get(i+1);
            }
        }
        if(bestChoice < intersects){
            swap(set, p1, p2);
            if(bestChoice != 0) return hillBestStep(set);
            return 0;
        }
        // got stuck no choice is better 
        return -1;
    }

    // Hill climbing first improvement step
    private static int hillFirstStep(Point[] set){
        ArrayList<Integer> choices = new ArrayList<>();
        Point temp;
        int intersects = interCount(set,choices);
        int p1=0, p2=0, candidate=0;
        for(int i=0;i<choices.size();i+=2){
            //checking possible candidate
            swap(set, mod(choices.get(i)+1,n), choices.get(i+1));
            candidate = interCount(set, null);
            if(candidate == 0) return 0;
            if(candidate < intersects) return hillFirstStep(set); //achived a better state
            //restauring original set
            swap(set, mod(choices.get(i)+1,n), choices.get(i+1));
        }
        // got stuck no choice is better
        return -1;
    }

    public static void hillClimbing(Point[] set, char mode){
        int result;
        switch(mode){
            case 'a':
                while(hillBestStep(set) == -1) //restart
                    Collections.shuffle(Arrays.asList(set));
                break;
            case 'b':
                while(hillFirstStep(set) == -1) //restart
                    Collections.shuffle(Arrays.asList(set));
                break;
            default:
                return;
        }

    }


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        point = new Point[n];
        point = toArrayPoint(point, n, in);
         
         

        for(int i = 0; i<n ; i++) System.out.print(point[i].id + " ");
        System.out.println();
        
        point = perm(point);
        hillClimbing(point,'b');
        
        
        for(int i = 0; i<n ; i++) System.out.print(point[i].id + " ");
        System.out.println();
    }
}