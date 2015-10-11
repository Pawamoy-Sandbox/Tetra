import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class Tetra
{
    public static void main (String[] args) {
        CodeSet.initialize();

        System.out.print(String.format("%1$18s", "Code length | "));
        System.out.print(String.format("%1$35s", "Number of valid codes | "));
        System.out.print(String.format("%1$23s", "Size of wrong array | "));
        System.out.print(String.format("%1$18s", "Execution time |"));
        System.out.println();
        System.out.print("----------------|-");
        System.out.print("---------------------------------|-");
        System.out.print("---------------------|-");
        System.out.print("-----------------|");
        System.out.println();

        try
        {
            launchLoop(1, 60);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static void launchLoop(int start, int end) throws InterruptedException
    {
        for (int l = start; l < end; l++)
        {
            File LDir = new File("Results/L" + l);
            LDir.mkdirs();

            // Queue size has to be related to window size of combinations splits:
            // we can have a 100-length queue of 1000-graph-sized threads,
            // or a 1000-length queue of 100-graph-sized threads...
            ExecutorService consumer = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(10));

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
//            long total = CombinatoricsUtils.binomialCoefficient(256, l);
//            float percent = (float) numberOfValidCodes / (float) total * 100;

            System.out.print(String.format("%1$18s", l + " | "));
//            System.out.print(String.format("%1$35s", numberOfValidCodes + "/" + total + " (" + percent + "%) | "));
            System.out.print(String.format("%1$35s", numberOfValidCodes + " | "));
            System.out.print(String.format("%1$23s", CodeSet.BSWrong.size() + " | "));
            System.out.println(String.format("%1$18s", new SimpleDateFormat("mm:ss:SSS").format(new Date(elapsedTime)) + " |"));
        }
    }
}