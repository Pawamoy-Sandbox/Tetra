import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;

public class Consumer implements Callable<Integer>
{
    private final List<BitSet> bitsetList;
    private final String threadName;
    // Using a final tells the Java Compiler to use only one condition-branch (-> faster execution)
    private static final boolean writeResults = false;

    public Consumer(String threadName, List<BitSet> bitsetList)
    {
        this.threadName = threadName;
        this.bitsetList = bitsetList;
    }

    @Override
    public Integer call() throws Exception
    {
        int validCodes = 0;
        BufferedWriter bw = null;

        try
        {
            if (writeResults)
            {
                bw = new BufferedWriter(new FileWriter(this.threadName));
                if (bw == null) {
                    System.out.println(this.threadName + ": problem while opening FileWriter; Stopping thread.");
                    return 0;
                }
            }

            for (BitSet bitset : this.bitsetList)
            {
                List<Integer> tetraList = new ArrayList<>();

                if (! CodeSet.isValidCode(bitset))
                    continue;

                for (int b = -1; (b = bitset.nextSetBit(b + 1)) != -1; )
                    tetraList.add(b);

                if (!checkLoopsInTetraGraph(tetraList))
                {
                    validCodes++;

                    if (writeResults)
                    {
                        bw.write(CodeSet.byteListToString(tetraList));
                        bw.write("\n");
                    }
                }
                else
                {
                    CodeSet.addWrongCode(bitset);
                }
            }

            if (writeResults)
            {
                bw.flush();
                bw.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

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

    public static boolean checkLoopsInTetraGraph(List<Integer> tetraList)
    {
        DirectedGraph<String, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (Integer tetra : tetraList)
            addTetraToGraph(CodeSet.byteToString(tetra), g);

        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<>(g);

        return cycleDetector.detectCycles();
    }
}
