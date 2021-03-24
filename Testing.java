import java.util.*;

public class Testing {
    static boolean visited[];
    static int n;
    static int m; 
    static int genCounter = 0;
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

    private static Point pointGen(int m){
        Random rand = new Random();
        Point ponto = new Point(-m + rand.nextInt(m*2+1),-m + rand.nextInt(m*2+1),"P" + genCounter);
        return ponto;
    }

    public static Point[] toArrayPoint(Point[] points){
        points = new Point[n];        // Array de pontos 
        Point toInsert;
        List<Point> pointList = new ArrayList<>();
        for (int i = 0; i < n; i++) { // Inserir os novos pontos no Array
            genCounter++;
            toInsert = pointGen(m);
            if(!pointList.contains(toInsert)){
                pointList.add(toInsert);
                points[i] = toInsert;
            } 
            else toInsert = pointGen(m);
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
        for(int i=0;i<n;i++){
            for(int j=i+2;j<n;j++){
                if(segIntersect(set[i], set[i+1], set[j], set[mod(j+1,n)]) && i!=mod(j+1,n)) {
                    if(choices != null){
                        choices.add(i);
                        choices.add(j);
                    }
                    ++count;
                }
            }
        }
        return count;
    }

    // given the state ABCD will return AB^2 + BC^2 + CD^2 + DA^2
    private static int perimeterCount(Point[] set){
        int perimeter=0;
        for(int i=1;i<n;i++){
            perimeter += set[i].distance(set[i-1]);
        }
        perimeter += set[n-1].distance(set[0]);
        return perimeter;
    }

    // switches 2 points 
    private static void swap(Point[] set, int p1, int p2){
        Point temp;
        // switch points to try and fix the intersection
        temp = set[p1];
        set[p1] = set[p2];
        set[p2] = temp;
    }

    // Hill climbing best improvement step
    private static int hillBestIntersect(Point[] set, ArrayList<Integer> choices){
        ArrayList<Integer> nextChoiceList = new ArrayList<>();
        Point temp;
        int intersects = choices.size();
        int p1=0, p2=0, candidate=0,bestChoice = Integer.MAX_VALUE;
        for(int i=0;i<choices.size();i+=2){
            //checking possible candidate
            swap(set, mod(choices.get(i)+1,n), choices.get(i+1));

            ArrayList<Integer> candidateList = new ArrayList<>();
            candidate = interCount(set, candidateList);

            //restauring original set
            swap(set, mod(choices.get(i)+1,n), choices.get(i+1));
            // if we reached a local maximum (possibly global) save
            if(bestChoice > candidate){
                bestChoice = candidate;
                nextChoiceList = candidateList;
                p1 = mod(choices.get(i)+1,n);
                p2 = choices.get(i+1);
            }
        }
        if(bestChoice < intersects){
            swap(set, p1, p2);
            if(bestChoice != 0) return hillBestIntersect(set,nextChoiceList);
            return 0;
        }
        // got stuck no choice is better 
        return -1;
    }

    // Hill climbing first improvement step
    private static int hillFirstStep(Point[] set, int perimeter){
        int candidate=0;
        for(int i=0;i<n;i++){
            for(int j=i+2;j<n;j++){
                swap(set, i+1, j);
                candidate = perimeterCount(set);
                if(candidate > perimeter)
                    return hillFirstStep(set, candidate);
                swap(set, i+1, j);
            }
        }
        // got stuck no choice is better
        return -1;
    }

    private static int hillBestPerimeter(Point[] set, int perimeter){
        int bestChoice=Integer.MAX_VALUE;
        int candidate=0, p1=0, p2=0;
        for(int i=0;i<n;i++){
            for(int j=i+2;j<n;j++){
                swap(set, i+1, j);
                candidate = perimeterCount(set);
                if(bestChoice > perimeter && bestChoice > candidate){
                    bestChoice = candidate;
                    p1 = i+1;
                    p2 = j;
                }
                swap(set, i+1, j);
            }
        }
        if(bestChoice < perimeter){
            swap(set, p1, p2);
            return hillBestPerimeter(set,bestChoice);
        }
        return 0;
    }

    public static void hillClimbing(Point[] set, char mode){
        ArrayList<Integer> choices = new ArrayList<Integer>();
        int perimeter, count=0;
        switch(mode){
            case 'a':
                perimeter = perimeterCount(set);
                hillBestPerimeter(set, perimeter);
                while( (count = interCount(set, null)) != 0){
                    Collections.shuffle(Arrays.asList(set));
                    perimeter = perimeterCount(set);
                    hillBestPerimeter(set, perimeter);
                }
                break;
            
            case 'b':
                perimeter = perimeterCount(set);
                hillBestPerimeter(set, perimeter);
                while( (count = interCount(set, null)) != 0){
                    Collections.shuffle(Arrays.asList(set));
                    perimeter = perimeterCount(set);
                    hillBestPerimeter(set, perimeter);
                }
                break;
            case 'c':
                interCount(set,choices);
                // while unsolved restart problem
                while(hillBestIntersect(set, choices) == -1){
                    Collections.shuffle(Arrays.asList(set));
                    interCount(set,choices);
                }
                break;
            default:
                return;
        }
    }


    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        n = in.nextInt();
        m = in.nextInt();
        point = new Point[n];
        point = toArrayPoint(point);
         
        System.out.println(Arrays.toString(point));
        

        
    }
}