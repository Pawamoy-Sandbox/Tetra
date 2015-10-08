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
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;

public class Consumer implements Callable<Integer> {

    private final List<BitSet> bitsetList;

    public Consumer(List<BitSet> bitsetList) {
        this.bitsetList = bitsetList;
    }

    @Override
    public Integer call() throws Exception
    {
//        try {
//            BufferedWriter bw = new BufferedWriter(new FileWriter(this.threadName));

//            System.out.println("Calling " + Thread.currentThread().getName());

            int validCodes = 0;

            for (BitSet bitset : this.bitsetList)
            {
                List<Integer> tetraList = new ArrayList<>();

                for (int b = -1; (b = bitset.nextSetBit(b + 1)) != -1; )
                {
                    tetraList.add(b);
                }

                if (!checkLoopsInTetraGraph(tetraList))
                {
//                    bw.write(tetraList + "\n");
                    validCodes++;
//                    System.out.println(tetraList);
                }
            }

//            System.out.println("    Closing " + Thread.currentThread().getName() + " " + validCodes);
//            System.out.flush();

//            bw.flush();
//            bw.close();

//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return validCodes;
    }

    public static void addTetraToGraph(String tetra, DirectedGraph g)
    {
        for (int i = 0; i < 3; i++)
        {
            String firstElement = tetra.substring(0, i+1);
            String secondElement = tetra.substring(i+1);

            g.addVertex(firstElement);
            g.addVertex(secondElement);

            g.addEdge(firstElement, secondElement);
        }
    }

//    public static boolean checkLoopsInTetraGraph(List<String> tetraList)
//    {
//        DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);
//
//        for (String tetra : tetraList)
//        {
//            addTetraToGraph(tetra, g);
//        }
//
//        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(g);
//
//        if (cycleDetector.detectCycles())
//        {
////            System.out.println(g.toString());
//            return true;
//        }
//
//        return false;
//    }

    public static boolean checkLoopsInTetraGraph(List<Integer> tetraList)
    {
        DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Integer tetra : tetraList)
        {
            addTetraToGraph(CodeSet.byteToString(tetra), g);
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
