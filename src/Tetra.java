import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.*;

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

        CycleExample();
        TetraToGraphExample();
        SortedSetExample();
    }

    public static void TetraToGraphExample()
    {
        DirectedGraph<String, DefaultEdge> g =
                new DefaultDirectedGraph<String, DefaultEdge>
                        (DefaultEdge.class);

        String tetra1 = "ACGT";
        String tetra2 = "CGTA";

        for (int i = 0; i < 3; i++)
        {
            String firstElement = tetra1.substring(0, i+1);
            String secondElement = tetra1.substring(i+1);

            g.addVertex(firstElement);
            g.addVertex(secondElement);

            g.addEdge(firstElement, secondElement);
        }


        for (int i = 0; i < 3; i++)
        {
            String firstElement = tetra2.substring(0, i+1);
            String secondElement = tetra2.substring(i + 1);

            g.addVertex(firstElement);
            g.addVertex(secondElement);

            g.addEdge(firstElement, secondElement);
        }


        System.out.println(g.toString());


        // Are there cycles in the dependencies.
        CycleDetector<String, DefaultEdge> cycleDetector = new CycleDetector<String, DefaultEdge>(g);
        // Cycle(s) detected.
        if (cycleDetector.detectCycles()) {
            System.out.println("cycle detected. Aborting");

        }


    }

    public static void CycleExample()
    {
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

        //cycle
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
            System.out.println("cycle detected. Aborting");

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

    public static void SortedSetExample() {

        BitSet bytes = new BitSet();

        bytes.set(3);
        bytes.set(7);
        bytes.set(1);
        bytes.set(0);
        bytes.set(11);
        bytes.set(11);
        bytes.set(1000);

        System.out.println("Set: " + bytes); // {3, 7, 11, 1000}
        System.out.println("Set cardinality: " + bytes.cardinality()); // 4
        System.out.println("Is 10 in the set? " + bytes.get(10)); // false
        System.out.println("Iterating on set: ");
        for (int i = -1; (i = bytes.nextSetBit(i + 1)) != -1; ) {
            byte b = (byte) i;
            System.out.println(b);
        }
    }
}