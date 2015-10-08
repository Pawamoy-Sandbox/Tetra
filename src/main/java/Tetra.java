import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.paukov.combinatorics.Generator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

public class Tetra
{
    public static void main (String[] args) throws InterruptedException {
        CodeSet.initialize();

        long startTime = System.currentTimeMillis();

        //The numbers are just silly tune parameters. Refer to the API.
        //The important thing is, we are passing a bounded queue.
        ExecutorService consumer = new ThreadPoolExecutor(8,8,30,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(20));

        //No need to bound the queue for this executor.
        //Use utility method instead of the complicated Constructor.
        ExecutorService producer = Executors.newSingleThreadExecutor();

        Runnable produce = new Producer(consumer);
        producer.submit(produce);

        producer.shutdown();
        producer.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        //System.out.println("Number of valid codes: " + validCodes);
        System.out.println("Execution time: " + elapsedTime);

//        bitSetExample();

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

//    public static void bitSetExample()
//    {
//
//        bytes.and(bytes2); // intersection
//        bytes.andNot(bytes2); // subtraction
//        bytes.or(bytes2); // addition
//
//    }
}