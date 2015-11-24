import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class Tetra
{
    public static void main (String[] args) {
        CodeSet.initialize();

        System.out.print(String.format("%1$18s", "Code length | "));
        System.out.print(String.format("%1$18s", "Valid codes | "));
        System.out.print(String.format("%1$18s", "Execution time |"));
        System.out.println();
        System.out.print("----------------|-");
        System.out.print("----------------|-");
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
            ThreadPoolExecutor consumer = new ThreadPoolExecutor(
                    numThreads, numThreads, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(queueSize));

            // this will block if the queue is full as opposed to throwing exception
            consumer.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

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

            System.out.print(String.format("%1$18s", l + " | "));
            System.out.print(String.format("%1$18s", numberOfValidCodes + " | "));
            System.out.println(String.format("%1$18s", new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(elapsedTime-1000*3600)) + " |"));
        }
    }
}