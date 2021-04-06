import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PoligSolver {
    int n;
    boolean visited[];
    MyPoint[] state;

    PoligSolver(Scanner in) {
        n = in.nextInt();
        in.nextInt(); // m not utilized
        state = new MyPoint[n];
        for (int i = 1; i <= n; i++) {
            MyPoint point = new MyPoint(in.nextInt(), in.nextInt(), "P" + (i-1));
            state[i - 1] = point;
        }
    }

    PoligSolver(MyPoint[] state) {
        this.state = state;
        n = state.length;
    }

    PoligSolver(int n, int m) {
        this.n = n;
        state = genRand(m);
    }

    public int interCount(){
        return PoligSolver.interCount(this.state, null);
    }

    public int interCount(ArrayList<Integer> list){
        return PoligSolver.interCount(this.state, list);
    }

    public int perimeterCount(){
        return PoligSolver.perimeterCount(this.state);
    }

    // Executes ACO with maxIter number of iternations
    public void ACO(int maxIter, int kAnts, final double alfa, final double beta, final double q, final double vRate, boolean useLocalSearch) {
        if (kAnts > n)
            return;
        this.ACOIter(maxIter, kAnts, alfa, beta, q, vRate, useLocalSearch);
    }

    public void ACO2(int kAnts, final double alfa, final double beta, final double q, final double vRate, boolean useLocalSearch) {
        // Because no int is greater than Integer.MAX_VALUE it will never end
        this.ACOIter(Integer.MAX_VALUE, kAnts, alfa, beta, q, vRate, useLocalSearch);
    }


    private int ACOIter(int maxIter, int kAnts, final double alfa, final double beta, final double q,
            final double vRate, boolean useLocalSearch) {
        int iter = 1;
        if (kAnts > n)
            return -1;
        // initializing theoretical array of distances and pheromones
        double[] dist = new double[(n * n - n) / 2];
        double[] pheromones = new double[(n * n - n) / 2];
        double[] pheromonesChange = new double [(n * n - n) / 2];
        // settings default values for pheromones and finding distances matrixes
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < i; j++) {
                int pos = mapto(i, j);
                pheromones[pos] = 1;
                dist[pos] = 1.0 / state[i].distance(state[j]);
            }
        }
        /*
         * Creates k paths (or k states) from random starting points and checks if the
         * path with the lowest perimiter is solution state
         */
        TreeSet<Integer> usedStartPoints = new TreeSet<>();
        Integer[] bestStatePos = null;
        Random rand = new Random();
        int intersections = interCount(state, null), bestperimeter=Integer.MAX_VALUE;
        MyPoint[] newState = new MyPoint[n];
        if(intersections == 0) return -1;
        while (intersections > 0 && iter <= maxIter) {
            Arrays.fill(pheromonesChange,0);
            usedStartPoints.clear();
            for (int k = 0; k < kAnts; k++) {
                bestperimeter = Integer.MAX_VALUE;
                // start at a random point
                int startPos = rand.nextInt(n);
                while (usedStartPoints.contains(startPos)) {
                    startPos = rand.nextInt(n);
                }
                usedStartPoints.add(startPos);

                // Construct new state
                Object[] path = this.antPath(startPos, alfa, beta, dist, pheromones);
                Integer[] newStatePos = ((Integer[]) path[0]);
                int perimeter = ((Integer) path[1]);
                
                if(useLocalSearch){
                    // Local search
                    for(int i=0;i<n;i++){
                        for(int j=i+2;j<n;j++){
                            // 2-exchange
                            int r1 = this.state[newStatePos[i]].distance(this.state[newStatePos[i+1]]);
                            int r2 = this.state[newStatePos[j]].distance(this.state[newStatePos[mod(j+1,n)]]);
                            swap(newStatePos, i+1, j);
                            int newr1 = this.state[newStatePos[i]].distance(this.state[newStatePos[i+1]]);
                            int newr2 = this.state[newStatePos[j]].distance(this.state[newStatePos[mod(j+1,n)]]);
                            if(r1+r2 < newr1+newr2) swap(newStatePos,i+1,j);
                        }
                    }
                }
                perimeter = perimeterCount(this.state, newStatePos);
                
                if (perimeter < bestperimeter) {
                    bestStatePos = newStatePos;
                }
                // Adding pheronomes to |delta|pheromones
                for (int i = 1; i < n; i++) {
                    int pos = mapto(newStatePos[i-1],newStatePos[i]);
                    pheromonesChange[pos] += (q / perimeter);
                }
                pheromonesChange[mapto(newStatePos[n - 1], newStatePos[0])] += q / perimeter;
            }

            // Updating pheromones
            for(int i=0;i<n;i++){
                for(int j=0;j<i;j++){
                    int pos = mapto(i, j);
                    pheromones[pos] = (1-vRate)*pheromones[pos]+ vRate*pheromonesChange[pos];
                }
            }
            
            // Constructing polygon with less area (if this one is not a solution then none are)
            for (int i = 0; i < n; i++)
                newState[i] = this.state[bestStatePos[i]];
            intersections = interCount(newState, null);
            ++iter;
        }
        this.state = newState;
        return bestperimeter;
    }

    // Returns the new state in position 0 and the perimeter in position 1
    private Object[] antPath(int startPos, final double alfa, final double beta, double[] dist, double[] pheromones) {
        Integer[] newStatePos = new Integer[n];
        // represents nodes that have not been visited yet
        LinkedList<Integer> notVis = new LinkedList<>();
        // pos is simply used to convert from the matrix (i,j) form to the array form using mapto
        int pos, newStateSize = 0, perimeter = 0;
        double sum = 0;
        newStatePos[0] = startPos;
        newStateSize = 1;
        for (int j = 0; j < n; j++) {
            if (startPos != j)
                notVis.add(j);
        }

        // Making the ant path
        Double bestProb, curProb, probSum;
        int bestPos, i = startPos;
        Iterator<Integer> it;
        // While there are still points not visited
        while (newStateSize < n) {
            bestProb = -1.0;
            bestPos = -1;
            curProb = 0.0;
            sum = probSum = Double.MIN_VALUE; // Can not be set to 0 or it can lead to NaN in division

            // Calculating the sum [Tij]^alfa + [Nij]^Beta, where j is not visited yet
            for (int j : notVis) {
                pos = mapto(i, j);
                sum += Math.pow(pheromones[pos], alfa) * Math.pow(dist[pos], beta);
            }
            // Determining next point
            it = notVis.listIterator();
            while (it.hasNext()) {
                int j = ((Integer) it.next()).intValue();
                pos = mapto(i, j);
                curProb = Math.pow(pheromones[pos], alfa) * Math.pow(dist[pos], beta) / sum;
                probSum += curProb;
                if (curProb > bestProb) {
                    bestProb = curProb;
                    bestPos = j;
                }

                // theoretically this conditions means there are no better choices from here on out
                if (bestProb > (1 - probSum)) {
                    break;
                }

            }
            // Adding the next point (and marking it as visited)
            notVis.remove(Integer.valueOf(bestPos));
            newStatePos[newStateSize] = bestPos;
            perimeter += this.state[i].distance(this.state[bestPos]);
            newStateSize++;
            i = bestPos;
        }
        perimeter += this.state[newStatePos[n - 1]].distance(this.state[newStatePos[0]]);
        Object[] output = { newStatePos, perimeter };
        return output;
    }

    /*
     * maps a n*n matrix where only the lower half is used (not counting with the
     * [i][i] entrances) returns sum (1..i-1) + j
     */
    private static int mapto(int i, int j) {
        if (i < j)
            return ((j - 1) * j) / 2 + i;
        return ((i - 1) * i) / 2 + j;
    }

    // Cross product of 2 2d points or determinante of 2 vectors
    private static int crossProduct2d(MyPoint a, MyPoint b) {
        return a.x * b.y - b.x * a.y;
    }

    // Returns if c is within the "box" formed by a and b
    private static boolean inBox(MyPoint a, MyPoint b, MyPoint c) {
        return Math.min(a.x, b.x) <= c.x && c.x <= Math.max(a.x, b.x) && Math.min(a.y, b.y) <= c.y
                && c.y <= Math.max(a.y, b.y);
    }

    private static int mod(int k, int m) {
        return (k < 0) ? (m - (Math.abs(k) % m)) % m : (k % m);
    }

    // Receives 2 line segments and returns true if they intersect
    private static boolean segIntersect(MyPoint A, MyPoint B, MyPoint C, MyPoint D) {
        int dAB_C, dAB_D, dCD_A, dCD_B;
        dAB_C = crossProduct2d(MyPoint.subPoint(C, A), MyPoint.subPoint(B, A));
        dAB_D = crossProduct2d(MyPoint.subPoint(D, A), MyPoint.subPoint(B, A));
        dCD_A = crossProduct2d(MyPoint.subPoint(A, C), MyPoint.subPoint(D, C));
        dCD_B = crossProduct2d(MyPoint.subPoint(B, C), MyPoint.subPoint(D, C));
        if (dAB_C * dAB_D < 0 && dCD_A * dCD_B < 0)
            return true;
        else if (dAB_C == 0 && inBox(A, B, C))
            return true;
        else if (dAB_D == 0 && inBox(A, B, D))
            return true;
        else if (dCD_A == 0 && inBox(C, D, A))
            return true;
        else if (dCD_B == 0 && inBox(C, D, B))
            return true;
        return false;
    }

    // counts number of intersections in a 2d graph of line segments and saves the points in choices 
    // complexidade temporal: O(n^2) spacial complexity: O(n) (because of the choices array)
    private static int interCount(MyPoint[] set, ArrayList<Integer> choices) {
        int count = 0;
        for (int i = 0; i < set.length; i++) {
            for (int j = i + 2; j < set.length; j++) {
                if (segIntersect(set[i], set[i + 1], set[j], set[mod(j + 1, set.length)])
                        && i != mod(j + 1, set.length)) {
                    if (choices != null) {
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
    private static int perimeterCount(MyPoint[] set) {
        int perimeter = 0;
        for (int i = 1; i < set.length; i++) {
            perimeter += set[i].distance(set[i - 1]);
        }
        perimeter += set[set.length - 1].distance(set[0]);
        return perimeter;
    }

    // perimeterCount adapted to suit local search in ACO algorithm
    private static int perimeterCount(MyPoint[] set, Integer[] pos) {
        int perimeter = 0;
        for (int i = 1; i < set.length; i++) {
            perimeter += set[pos[i]].distance(set[pos[i - 1]]);
        }
        perimeter += set[pos[set.length - 1]].distance(set[pos[0]]);
        return perimeter;
    }

    // switches 2 points
    private static void swap(MyPoint[] set, int p1, int p2) {
        MyPoint temp;
        temp = set[p1];
        set[p1] = set[p2];
        set[p2] = temp;
    }

    // switches 2 points (adapted to suit ACO local search)
    private static void swap(Integer pos[], int p1, int p2) {
        int temp;
        // switch points to try and fix the intersection
        temp = pos[p1];
        pos[p1] = pos[p2];
        pos[p2] = temp;
    }

    // Hill climbing best improvement step
    private static int hillBestIntersect(MyPoint[] set, ArrayList<Integer> choices) {
        int n = set.length;
        ArrayList<Integer> nextChoiceList = new ArrayList<>();
        int intersects = choices.size();
        int p1 = 0, p2 = 0, candidate = 0, bestChoice = Integer.MAX_VALUE;
        for (int i = 0; i < choices.size(); i += 2) {
            // checking possible candidate
            swap(set, mod(choices.get(i) + 1, n), choices.get(i + 1));

            ArrayList<Integer> candidateList = new ArrayList<>();
            candidate = interCount(set, candidateList);

            // restauring original set
            swap(set, mod(choices.get(i) + 1, n), choices.get(i + 1));
            // if we reached a local minimum (possibly global) save
            if (bestChoice > candidate) {
                bestChoice = candidate;
                nextChoiceList = candidateList;
                p1 = mod(choices.get(i) + 1, n);
                p2 = choices.get(i + 1);
            }
        }
        if (bestChoice < intersects) {
            swap(set, p1, p2);
            if (bestChoice != 0)
                return hillBestIntersect(set, nextChoiceList);
            return 0;
        }
        // got stuck no choice is better
        return -1;
    }

    private static int hillBestPerimeter(MyPoint[] set, int perimeter) {
        int n = set.length;
        int bestChoice = Integer.MAX_VALUE;
        int candidate = 0, p1 = 0, p2 = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 2; j < n; j++) {
                swap(set, i + 1, j);
                candidate = perimeterCount(set);
                if (bestChoice > perimeter && bestChoice > candidate) {
                    bestChoice = candidate;
                    p1 = i + 1;
                    p2 = j;
                }
                swap(set, i + 1, j);
            }
        }
        if (bestChoice < perimeter) {
            swap(set, p1, p2);
            return hillBestPerimeter(set, bestChoice);
        }
        return 0;
    }

    // Hill climbing first improvement step
    private static int hillFirstStep(MyPoint[] set, int perimeter) {
        int n = set.length;
        int candidate = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 2; j < n; j++) {
                swap(set, i + 1, j);
                candidate = perimeterCount(set);
                if (candidate < perimeter)
                    return hillFirstStep(set, candidate);
                swap(set, i + 1, j);
            }
        }
        // got stuck no choice is better
        return 0;
    }

    // Hill climbing random step improvement
    private static int hillRandomStep(MyPoint[] set, int perimeter) {
        int n = set.length;
        ArrayList<Integer[]> candidates = new ArrayList<>();
        int candidate = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i + 2; j < n; j++) {
                swap(set, i + 1, j);
                candidate = perimeterCount(set);
                if (candidate < perimeter) {
                    Integer[] choice = new Integer[3];
                    choice[0] = i + 1;
                    choice[1] = j;
                    choice[2] = candidate;
                    candidates.add(choice);
                }
                swap(set, i + 1, j);
            }
        }
        if (candidates.isEmpty())
            return 0;
        Integer[] chosen = candidates.get(ThreadLocalRandom.current().nextInt(0, candidates.size()));
        swap(set, chosen[0], chosen[1]);
        return hillRandomStep(set, chosen[2]);
    }

    private static void hillClimbing(MyPoint[] set, char mode) {
        ArrayList<Integer> choices = new ArrayList<Integer>();
        int perimeter, count = 0;
        switch (mode) {

        case 'a':
            perimeter = perimeterCount(set);
            hillBestPerimeter(set, perimeter);
            while ((count = interCount(set, null)) != 0) {
                Collections.shuffle(Arrays.asList(set));
                perimeter = perimeterCount(set);
                hillBestPerimeter(set, perimeter);
            }
            break;
        case 'b':
            perimeter = perimeterCount(set);
            hillFirstStep(set, perimeter);
            while ((count = interCount(set, null)) != 0) {
                Collections.shuffle(Arrays.asList(set));
                perimeter = perimeterCount(set);
                hillFirstStep(set, perimeter);
            }
            break;
        case 'c':
            interCount(set, choices);
            // while unsolved restart problem
            while (hillBestIntersect(set, choices) == -1) {
                Collections.shuffle(Arrays.asList(set));
                interCount(set, choices);
            }
            break;

        case 'd':
            perimeter = perimeterCount(set);
            hillRandomStep(set, perimeter);
            while ((count = interCount(set, null)) != 0) {
                Collections.shuffle(Arrays.asList(set));
                perimeter = perimeterCount(set);
                hillRandomStep(set, perimeter);
            }
            break;

        default:
            return;
        }
    }

    public void hillClimbing(char mode) {
        PoligSolver.hillClimbing(this.state, mode);
        return;
    }

    // Nearest neighbour daqui para a frente
    private boolean check() { // Verifica se ainda há pontos por visitar
        for (int i = 0; i < n; i++) {
            if (visited[i] == false)
                return true;
        }

        return false;
    }

    private int search(int v) { // Procura o ponto mais próximo de V
        int closest = 0;
        int entered = 0; // Serve para definir o primeiro visitado
        int tovisit = 0;
        visited[v] = true;
        for (int i = 0; i < n; i++) {
            if (!visited[i]) {

                if (entered == 0) {
                    entered = 1;
                    closest = state[v].distance(state[i]);
                    tovisit = i;
                }

                else if (closest > state[v].distance(state[i])) {
                    closest = state[v].distance(state[i]);
                    tovisit = i;
                }
            }
        }
        return tovisit;
    }

    public MyPoint[] nearestNeib() { // Cria um array de Point que contém o output
        MyPoint[] save = new MyPoint[n]; // do NN (E devolve)
        visited = new boolean[n];
        int start = new Random().nextInt(n), count = 0;
        save[0] = state[start];
        count++;

        while (check()) {
            if (count == n)
                break;
            start = search(start);
            save[count] = state[start];
            count++;
        }
        return save;
    }
    
    public void useNearestNeib(){         this.state = nearestNeib();     }
    
    public static double schedule(double t, double r){
        return t * r;
    }
    
    @SuppressWarnings("unchecked")
    public void simulatedAnnealing(double temp, double r){
        int curEnergy, nextEnergy, deltaEn; // Quantidade de interseções de cada permutação
        int p1, p2;
        Random rand = new Random();
        ArrayList<Integer> interceptionsCur = new ArrayList<>(), nextInterceptionsCur = new ArrayList<>();
        curEnergy = interCount(state,interceptionsCur);
   
        for(int i=1; i<Integer.MAX_VALUE; i++){
            nextInterceptionsCur.clear();
            if(curEnergy == 0) return; //Se não houver interseções, retornar
            temp = schedule(temp,r);
            if(temp < 0.001) return;

            int random = rand.nextInt(interceptionsCur.size()-1);
            p1 = interceptionsCur.remove(random);
            p2 = interceptionsCur.remove(random);
            swap(state,mod(p1+1, n),p2);  // 2 Exchange!
            nextEnergy = interCount(state,nextInterceptionsCur);
            deltaEn = nextEnergy - curEnergy;
            if(deltaEn < 0){
                curEnergy = nextEnergy;
                interceptionsCur = ((ArrayList<Integer>)nextInterceptionsCur.clone());
            }
            else if(Math.exp((-deltaEn)/temp) >= Math.random()){
                curEnergy = nextEnergy;
                interceptionsCur = ((ArrayList<Integer>)nextInterceptionsCur.clone());
            }
            else{
                swap(state,mod(p1+1, n),p2);
                interceptionsCur.add(p1);
                interceptionsCur.add(p2);
            }
            // chegando aqui significa q nao mudou de estado
        }
        return;
    }

    public MyPoint[] genRand(int m) {
        MyPoint[] points = new MyPoint[n]; // Array de pontos
        MyPoint toInsert;
        List<MyPoint> pointList = new ArrayList<>();
        for (int i = 0; i < n;) { // Inserir os novos pontos no Array
            toInsert = MyPoint.random(m, i + 1);
            if (!pointList.contains(toInsert)) {
                pointList.add(toInsert);
                points[i] = toInsert;
                i++;
            }
        }
        return points;
    }

    public MyPoint[] perm() { // Função que devolve uma permutação de Array de Pontos
        MyPoint[] save;
        save = state.clone();
        List<MyPoint> pointList = Arrays.asList(save);
        Collections.shuffle(pointList);
        pointList.toArray(save);
        return save;
    }

    public String details() {
        String out = "";
        for (int i = 0; i < n; i++)
            out += state[i] + " ";
        return out;
    }

    @Override
    public String toString() {
        String out = "";
        for (int i = 0; i < n; i++)
            out += state[i].id + " ";
        return out;
    }

    public void showGraph(int xres, int yres, int scale) {
        new Frame(state, xres, yres, scale);
    }
}
