import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Date;
import java.util.concurrent.*;

public class Tetra
{
    public static void main (String[] args) {
        CodeSet.initialize();

//        BitSet hey = new BitSet();
//        hey.set(1);
//        hey.set(2);
//        hey.set(6);
//        hey.set(8);
//
//        System.out.println(hey.get(1, 3));

//        Combiner combiner = new Combiner(1);
//
//        for (BitSet b : combiner)
//        {
//            System.out.println(b);
//        }

        System.out.print(String.format("%1$23s", "Code length | "));
        System.out.print(String.format("%1$35s", "Number of valid codes | "));
        System.out.print(String.format("%1$23s", "Size of wrong array | "));
        System.out.println(String.format("%1$23s", "Execution time |"));
        System.out.print("------------------------------");
        System.out.print("------------------------------");
        System.out.print("------------------------------");
        System.out.println("------------------------------");

        try
        {
            launchLoop(1, 13);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        System.out.println(CodeSet.BSWrong);
    }

    private static void launchLoop(int start, int end) throws InterruptedException
    {
        for (int l = start; l < end; l++)
        {
            // Queue size has to be related to window size of combinations splits:
            // we can have a 100-length queue of 1000-graph-sized threads,
            // or a 1000-length queue of 100-graph-sized threads...
            ExecutorService consumer = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(10));

            //No need to bound the queue for this executor.
            //Use utility method instead of the complicated Constructor.
            CompletionService<Integer> completionService = new ExecutorCompletionService<>(consumer);

            ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
            Callable<Integer> producer = new Producer(completionService, consumer, l, false);

            long startTime = System.currentTimeMillis();
            Future<Integer> future = producerExecutor.submit(producer);

            Integer numberOfValidCodes = 0;

            try
            {
                numberOfValidCodes = future.get();
            }
            catch (ExecutionException e)
            {
                e.printStackTrace();
            }

            producerExecutor.shutdown();
            producerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

            long stopTime = System.currentTimeMillis();
            long elapsedTime = stopTime - startTime;

            long total = CombinatoricsUtils.binomialCoefficient(12, l);
            float percent = (float) numberOfValidCodes / (float) total * 100;

            System.out.print(String.format("%1$23s", l + " | "));
            System.out.print(String.format("%1$35s", numberOfValidCodes + "/" + total + " (" + percent + "%) | "));
            System.out.print(String.format("%1$23s", CodeSet.BSWrong.size() + " | "));
            System.out.println(String.format("%1$23s", new SimpleDateFormat("mm:ss:SSS").format(new Date(elapsedTime)) + " |"));
        }
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