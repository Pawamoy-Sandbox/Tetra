import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.io.*;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.Callable;

public class Consumer implements Callable<Integer>
{
    private final List<BitSet> bitsetList;
    private final String threadName;
    private final int codeLength;
    private final boolean writeResults;

    public Consumer(String threadName, List<BitSet> bitsetList, int codeLength, boolean writeResults)
    {
        this.threadName = threadName;
        this.bitsetList = bitsetList;
        this.codeLength = codeLength;
        this.writeResults = writeResults;
    }

    @Override
    public Integer call() throws Exception
    {
        int validCodes = 0;
        BufferedWriter bw = null;

        try
        {
            if (this.writeResults)
                bw = new BufferedWriter(new FileWriter(this.threadName));

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

                    // FIXME: could be useful to get rid of this condition when not writing results
                    if (bw != null)
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

            if (bw != null)
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
