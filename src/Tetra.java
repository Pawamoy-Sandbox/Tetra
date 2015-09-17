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

        System.out.println("tetra: " + tetra1 + "\tcompl:" + Compl(tetra1) + "\tAutoCompl: " + IsAutoCompl(tetra1));
        System.out.println("tetra: " + tetra2 + "\tcompl:" + Compl(tetra2) + "\tAutoCompl: " + IsAutoCompl(tetra2));


        DirectedGraph<String, DefaultEdge> g =
                new DefaultDirectedGraph<String, DefaultEdge>
                        (DefaultEdge.class);
        g.addVertex("A");
        g.addVertex("CGT");

        g.addVertex("AC");
        g.addVertex("GT");

        g.addVertex("ACG");
        g.addVertex("T");

        g.addVertex("CG");
        g.addVertex("TA");


        g.addEdge("A", "CGT");
        g.addEdge("CGT", "A");

        g.addEdge("AC", "GT");
        g.addEdge("ACG", "T");
        g.addEdge("CG", "TA");

        System.out.println(g.toString());

        // Are there cycles in the dependencies.
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(g);
        // Cycle(s) detected.
        if (cycleDetector.detectCycles()) {
            Iterator<String> iterator;
            Set<String> cycleVertices;
            Set<String> subCycle;
            String cycle;

            System.out.println("Cycles detected.");

            // Get all vertices involved in cycles.
            cycleVertices = cycleDetector.findCycles();

            // Loop through vertices trying to find disjoint cycles.
            while (! cycleVertices.isEmpty()) {
                System.out.println("Cycle:");

                // Get a vertex involved in a cycle.
                iterator = cycleVertices.iterator();
                cycle = iterator.next();

                // Get all vertices involved with this vertex.
                subCycle = cycleDetector.findCyclesContainingVertex(cycle);
                for (String sub : subCycle) {
                    System.out.println("   " + sub);
                    // Remove vertex so that this cycle is not encountered
                    // again.
                    cycleVertices.remove(sub);
                }
            }
        }

        // No cycles.  Just output properly ordered vertices.
        else {
            String v;
            TopologicalOrderIterator<String, DefaultEdge> orderIterator;

            orderIterator =
                    new TopologicalOrderIterator<String, DefaultEdge>(g);
            System.out.println("\nOrdering:");
            while (orderIterator.hasNext()) {
                v = orderIterator.next();
                System.out.println(v);
            }
        }

    }

    public static String Compl(String tetra)
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

    public static boolean IsAutoCompl(String tetra)
    {
        return tetra.equals(Compl(tetra));
    }
}