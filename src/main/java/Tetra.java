import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class Tetra
{
    public static void main (String[] args) {
        CodeSet.initialize();

        System.out.print(String.format("%1$18s", "Code length | "));
        System.out.print(String.format("%1$35s", "Number of valid codes | "));
        System.out.print(String.format("%1$23s", "Size of hashmap | "));
        System.out.print(String.format("%1$18s", "Execution time |"));
        System.out.println();
        System.out.print("----------------|-");
        System.out.print("---------------------------------|-");
        System.out.print("---------------------|-");
        System.out.print("-----------------|");
        System.out.println();

        try
        {
            launchLoop(2, 20);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static void launchLoop(int start, int end) throws InterruptedException
    {
        int numThreads = 8;
        int queueSize = 12;

        for (int l = start; l < end; l++)
        {
            File LDir = new File("Results/L" + l);
            LDir.mkdirs();

            // Queue size has to be related to window size of combinations splits:
            // we can have a 100-length queue of 1000-graph-sized threads,
            // or a 1000-length queue of 100-graph-sized threads...
            ExecutorService consumer = new ThreadPoolExecutor(
                    numThreads, numThreads, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(queueSize));

            //No need to bound the queue for this executor.
            //Use utility method instead of the complicated Constructor.
            CompletionService<Integer> completionService = new ExecutorCompletionService<>(consumer);

            ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
            Callable<Integer> producer = new Producer(completionService, consumer, l);

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

            // Too high value with 256, generates exception
            BigDecimal total = BigDecimal.valueOf(CombinatoricsUtils.binomialCoefficient(256, l));
//            float percent = BigDecimal.valueOf(numberOfValidCodes).divide(total, 3, RoundingMode.CEILING).floatValue() * 100;

            System.out.print(String.format("%1$18s", l + " | "));
            System.out.print(String.format("%1$35s", numberOfValidCodes + "/" + total + /*" (" + percent + "%)" + */" | "));
            System.out.print(String.format("%1$23s", CodeSet.BSWrong.size() + " | "));
            System.out.println(String.format("%1$18s", new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(elapsedTime-1000*3600)) + " |"));
        }
    }
}