import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Tetra
{
    static List<String> tetra256 = new ArrayList<>();
    static List<String> tetraAutoCompl12 = new ArrayList<>();

    public static void main (String[] args) {
        readTetra256();

        System.out.println("=================");
        for (String tetra : tetra256)
        {
            System.out.println("tetra: " + tetra + "\tcompl:" + compl(tetra) + "\tAutoCompl: " + isAutoCompl(tetra));
        }
        System.out.println("=================");
        System.out.println("=================");
        for (String tetra : tetraAutoCompl12)
        {
            System.out.println("tetra: " + tetra + "\tcompl:" + compl(tetra) + "\tAutoCompl: " + isAutoCompl(tetra));
        }
        System.out.println("=================");

        bitSetExample();
        System.out.println("=================");

        List<String> tetraTreatedList = tetra256;
        tetraTreatedList.removeAll(tetraAutoCompl12);

        for (String tetraPair : checkingLoopsForl2(tetraTreatedList))
        {
            System.out.println("tetra pair without cycle: " + tetraPair);
        }
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

    public static List<String> checkingLoopsForl2(List<String> tetraTreatedList)
    {
        int nbLoop = 0;

        List<String> res = new ArrayList<>();

        for (int i = 0; i < tetraTreatedList.size(); i++)
        {
            for (int j = 0; j < tetraTreatedList.size(); j++)
            {
                List<String> tetraList = new ArrayList<>();

                String firstElement = tetraTreatedList.get(i);
                String secondElement = tetraTreatedList.get(j);
                tetraList.add(firstElement);
                tetraList.add(secondElement);

                if(checkLoopsInTetraGraph(tetraList))
                {
                    nbLoop++;
                }
                else
                {
                    res.add(firstElement + '-' + secondElement);
                }
            }
        }

        System.out.println(nbLoop + " loops in " + tetraTreatedList.size()*tetraTreatedList.size() + " elements");

        return res;
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
            System.out.println(g.toString());
            return true;
        }

        return false;
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