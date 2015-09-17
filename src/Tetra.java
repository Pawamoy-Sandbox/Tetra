import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.Iterator;
import java.util.Set;

public class Tetra
{

    public static void main (String[] args) {
        ICombinatoricsVector<String> allTetra = Factory.createVector(new String[]{"A", "C", "G", "T"});
        ICombinatoricsVector<String> allTetraCompl = Factory.createVector(new String[]{"T", "G", "C", "A"});

        // Create the permutation generator by calling the appropriate method in the Factory class
        Generator<String> gen = Factory.createPermutationWithRepetitionGenerator(allTetra, 4);

        // Print the result
        for (ICombinatoricsVector<String> perm : gen)
            System.out.println(perm);

        String tetra1 = "ACGT";
        String tetra2 = "CGCT";

        System.out.println("tetra: " + tetra1 + "\tcompl:" + compl(tetra1) + "\tAutoCompl: " + isAutoCompl(tetra1));
        System.out.println("tetra: " + tetra2 + "\tcompl:" + compl(tetra2) + "\tAutoCompl: " + isAutoCompl(tetra2));

        tetraToGraphExample();
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

    public static void tetraToGraphExample()
    {
        DirectedGraph<String, DefaultEdge> g =
                new DefaultDirectedGraph<String, DefaultEdge>
                        (DefaultEdge.class);

        String tetra1 = "ACGT";
        String tetra2 = "CGTA";

        addTetraToGraph(tetra1, g);
        addTetraToGraph(tetra2, g);


        System.out.println(g.toString());

        // Are there cycles in the dependencies.
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(g);
        // Cycle(s) detected.
        if (cycleDetector.detectCycles()) {
            System.out.println("cycle detected. Aborting");

        }
    }                                                                                                                                                                                                                                                                                                                          

    public static String compl(String tetra)
    {
        StringBuilder res = new StringBuilder();

        for (char ch: tetra.toCharArray()) {
            char chCompl = 'A';

            if (ch == 'A')
                chCompl ='T';
            if (ch == 'C')
                chCompl ='G';
            if (ch == 'G')
                chCompl ='C';
            if (ch == 'T')
                chCompl ='A';

            res.insert(0,chCompl);
        }

        return res.toString();
    }

    public static boolean isAutoCompl(String tetra)
    {
        return tetra.equals(compl(tetra));
    }
}