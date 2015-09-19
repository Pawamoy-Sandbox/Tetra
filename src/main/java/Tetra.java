import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.paukov.combinatorics.ICombinatoricsVector;

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

        bitSetExample();

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

//        try {
//            FileWriter fw_l2 = new FileWriter("result_l2.txt");
//            FileWriter fw_l2_autocompl = new FileWriter("result_l2_autocompl.txt");
//
//            List<String> resultl2 = checkingLoopsForl2(CodeSet.S126);
//            List<String> autoComplList = new ArrayList<>();
//
//            fw_l2.write(resultl2.size() + " valid on " + CodeSet.S126.size()* CodeSet.S126.size() + " elements"+ '\n');
//
//            for (String tetraPair : resultl2)
//            {
//                fw_l2.write(tetraPair);
//                fw_l2.write('\n');
//
//                String firstElement = tetraPair.split("-")[0];
//                if (CodeSet.isAutoCompl(firstElement) && !autoComplList.contains(firstElement)) {
//                    autoComplList.add(firstElement);
//                    fw_l2_autocompl.write(firstElement);
//                    fw_l2_autocompl.write('\n');
//                }
//            }
//
//            fw_l2.close();
//            fw_l2_autocompl.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            FileWriter fw_l3 = new FileWriter("result_l3.txt");
//
//            List<String> resultl3 = checkingLoopsForl3(CodeSet.S126);
//
//            fw_l3.write(resultl3.size() + " valid on " + CodeSet.S126.size() * CodeSet.S126.size() * CodeSet.S126.size() + " elements" + '\n');
//
//            for (String tetraPair : resultl3)
//            {
//                fw_l3.write(tetraPair);
//                fw_l3.write('\n');
//            }
//
//            fw_l3.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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

    public static List<String> checkingLoopsForl3(List<String> tetraTreatedList)
    {
        int nbLoop = 0;

        List<String> res = new ArrayList<>();

        for (int i = 0; i < tetraTreatedList.size(); i++)
        {
            for (int j = 0; j < tetraTreatedList.size(); j++)
            {
                for (int k = 0; k < tetraTreatedList.size(); k++){
                    List<String> tetraList = new ArrayList<>();

                    String firstElement = tetraTreatedList.get(i);
                    String secondElement = tetraTreatedList.get(j);
                    String thirdElement = tetraTreatedList.get(k);
                    tetraList.add(firstElement);
                    tetraList.add(secondElement);
                    tetraList.add(thirdElement);

                    if(checkLoopsInTetraGraph(tetraList))
                    {
                        nbLoop++;
                    }
                    else
                    {
                        res.add(firstElement + '-' + secondElement + '-' + thirdElement);
                    }
                }
            }
        }

        System.out.println(nbLoop + " loops in " + tetraTreatedList.size()*tetraTreatedList.size()*tetraTreatedList.size() + " elements");

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
        bytes2.set(0);
        bytes2.set(4);

//        bytes.and(bytes2); // intersection
//        bytes.andNot(bytes2); // subtraction
//        bytes.or(bytes2); // addition
        System.out.println("Set1: " + bytes);
        System.out.println("Set2: " + bytes2);
        System.out.println("Intersects: " + bytes.intersects(bytes2));


        BitSet b3 = new BitSet();
        b3.set(2);
        b3.set(1);
        System.out.println("Set3: " + b3);

        List<BitSet> list = new ArrayList<>();
        list.add(bytes2);
        list.add(b3);

        System.out.println("Is first set composed of one of the two other set? " + CodeSet.containsNotValidSubset(bytes, list));

//        System.out.println("============================================================");
//        System.out.println("Bit couples with BS126");
//        for (ICombinatoricsVector<Integer> v : CodeSet.combine(CodeSet.BS126, 2, 0, 0))
//            System.out.println(v.getVector());

        System.out.println("============================================================");
        System.out.println("Trying with l=60 to see exec time (BS126)");
        int i = 0, limit = 1000000000;
        for (ICombinatoricsVector<Integer> v : CodeSet.combine(CodeSet.BS126, 60, 0, 0))
        {
            System.out.println(v.getVector());
            i++;
            if (i == limit)
                break;
        }
    }
}