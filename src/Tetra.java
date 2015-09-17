import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;
import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.*;

public class Tetra
{
    static List<String> tetra256 = new ArrayList<>();
    static List<String> tetraAutoCompl12 = new ArrayList<>();

    public static void main (String[] args) {
        readTetra256();

        for (String tetra : tetra256)
        {
            System.out.println("tetra: " + tetra + "\tcompl:" + compl(tetra) + "\tAutoCompl: " + isAutoCompl(tetra));
        }

        tetraToGraphExample();
        bitSetExample();
    }

    public static void readTetra256()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("tetra256.txt")))
        {
            String line;

            while ((line = br.readLine()) != null) {
                tetra256.add(line);

                if (isAutoCompl(line))
                    tetraAutoCompl12.add(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void bitSetExample() {

        BitSet bytes = new BitSet();

        bytes.set(3);
        bytes.set(7);
        bytes.set(1);
        bytes.set(0);
        bytes.set(11);
        bytes.set(11);
        bytes.set(1000);

        System.out.println("Set: " + bytes); // {0, 1, 3, 7, 11, 1000}
        System.out.println("Set cardinality: " + bytes.cardinality()); // 4
        System.out.println("Is 10 in the set? " + bytes.get(10)); // false
        System.out.println("Iterating on set: ");
        for (int i = -1; (i = bytes.nextSetBit(i + 1)) != -1; ) {
            byte b = (byte) i;
            System.out.print(b + " ");
        }
        System.out.println();

        BitSet bytes2 = new BitSet();
        bytes2.set(0, 4);

        bytes.and(bytes2); // intersection
//        bytes.andNot(bytes2); // subtraction
//        bytes.or(bytes2); // addition
        System.out.println("Set1: " + bytes);
        System.out.println("Set2: " + bytes2);
        System.out.println("Intersects: " + bytes.intersects(bytes2));
    }
}