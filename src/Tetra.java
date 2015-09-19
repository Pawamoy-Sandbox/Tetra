import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

public class Tetra
{
    public static void main (String[] args)
    {
        CodeSet.initialize();

//        System.out.println("=================");
//        for (int i = 0; i < 256; i++)
//        {
//            System.out.println("tetra: " + CodeSet.byteToString(i)
//                    + "\tcompl:" + CodeSet.byteToString(CodeSet.compl(i))
//                    + "\tAutoCompl: " + CodeSet.isAutoCompl(i));
//        }
//        System.out.println("=================");
//        for (int i = -1; (i = CodeSet.BS12.nextSetBit(i + 1)) != -1; )
//        {
//            System.out.println("tetra: " + CodeSet.byteToString(i)
//                    + "\tcompl:" + CodeSet.byteToString(CodeSet.compl(i))
//                    + "\tAutoCompl: " + CodeSet.isAutoCompl(i));
//        }
//        System.out.println("=================");

//        for (String tetra : CodeSet.S126)
//        {
//            System.out.println(tetra
//                    + "\t" + CodeSet.compl(tetra)
//                    + "\t" + CodeSet.isAutoCompl(tetra));
//        }

        try {
            FileWriter fw = new FileWriter("result_l2.txt");

            List<String> resultl2 = checkingLoopsForl2(CodeSet.S126);

            fw.write(resultl2.size() + " valid on " + CodeSet.S126.size()*CodeSet.S126.size() + " elements"+ '\n');

            for (String tetraPair : resultl2)
            {
                fw.write(tetraPair);
                fw.write('\n');
            }

            fw.close();

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

    public static void bitSetExample()
    {
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