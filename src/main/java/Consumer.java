import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.*;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;

public class Consumer implements Callable<Integer>
{
    private final List<BitSet> bitsetList;
    private final String threadName;
    // Using a final tells the Java Compiler to use only one condition-branch (-> faster execution)
    private static final boolean writeResults = true;

    public Consumer(String threadName, List<BitSet> bitsetList)
    {
        this.threadName = threadName;
        this.bitsetList = bitsetList;
    }

    @Override
    public Integer call() throws Exception
    {
        int validCodes = 0;
        BufferedWriter bw;

        try
        {
            if (writeResults)
            {
                bw = new BufferedWriter(new FileWriter(this.threadName));
//                if (bw == null)
//                {
//                    System.out.println(this.threadName + ": problem while opening FileWriter; Stopping thread.");
//                    return 0;
//                }
            }

            for (BitSet bitset : this.bitsetList)
            {
                if (!checkLoopsInTetraGraph(bitset))
                {
                    validCodes++;

                    if (writeResults)
                    {
                        bw.write(CodeSet.bitsetToString(bitset));
                        bw.write("\n");
                    }
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

    public static void addTetraToGraph(Integer tetra, DirectedGraph g)
    {
        List<List<Integer>> split = CodeSet.Splits.get(tetra);

        // FIXME: why not hardcode the tetrasplit array here as conditions? would it be faster?
        for (int i = 0; i < 3; i++)
        {
            List<Integer> elems = split.get(i);

            Integer firstElement = elems.get(0);
            Integer secondElement = elems.get(1);

            g.addVertex(firstElement);
            g.addVertex(secondElement);

            g.addEdge(firstElement, secondElement);
        }
    }

    public static boolean checkLoopsInTetraGraph(BitSet bitset)
    {
        DirectedGraph<Integer, DefaultEdge> g = new DefaultDirectedGraph<>(DefaultEdge.class);

        for (int b = -1; (b = bitset.nextSetBit(b + 1)) != -1; )
            addTetraToGraph(b, g);

        CycleDetector<Integer, DefaultEdge> cycleDetector = new CycleDetector<>(g);

        return cycleDetector.detectCycles();
    }
}
