import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Consumer implements Runnable {

    private final List<ICombinatoricsVector<Integer>> combinatoricsVectorList;
    private final String threadName;

    public Consumer(String name, List<ICombinatoricsVector<Integer>> combinatoricsVectorList) {
        this.threadName = name;
        this.combinatoricsVectorList = combinatoricsVectorList;
    }

    public void run()
    {
//        try {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(this.threadName));

            for (ICombinatoricsVector<Integer> v : combinatoricsVectorList)
            {
                List<String> tetraList = new ArrayList<>();
                for (Integer elem : v.getVector()) {
                    String stringElem = CodeSet.byteToString(elem);
                    tetraList.add(stringElem);
                }

                if (!checkLoopsInTetraGraph(tetraList))
                {
//                    bw.write(tetraList + "\n");
                    System.out.println(tetraList);
                }
            }

//            bw.flush();
//            bw.close();

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    public static void addTetraToGraph(String tetra, DirectedGraph g)
    {
        //simply shifting 3 times
        //ACGT for exemple
        //A CGT ; AC GT then ACG T
        for (int i = 0; i < 3; i++)
        {
            String firstElement = tetra.substring(0, i+1);
            String secondElement = tetra.substring(i+1);

            g.addVertex(firstElement);
            g.addVertex(secondElement);

            g.addEdge(firstElement, secondElement);
        }
    }

    public static boolean checkLoopsInTetraGraph(List<String> tetraList)
    {
        DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (String tetra : tetraList)
        {
            addTetraToGraph(tetra, g);
        }

        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(g);

        if (cycleDetector.detectCycles())
        {
//            System.out.println(g.toString());
            return true;
        }

        return false;
    }
}
