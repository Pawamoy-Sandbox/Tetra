import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class Tetra
{
    public static void main (String[] args) throws InterruptedException {
        CodeSet.initialize();

        long startTime = System.currentTimeMillis();

        // Queue size has to be related to window size of combinations splits:
        // we can have a 100-length queue of 1000-graph-sized threads,
        // or a 1000-length queue of 100-graph-sized threads...
        ExecutorService consumer = new ThreadPoolExecutor(8, 8, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(10));

        //No need to bound the queue for this executor.
        //Use utility method instead of the complicated Constructor.
        ExecutorService producer = Executors.newSingleThreadExecutor();
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(consumer);

        Callable<Integer> produce = new Producer(completionService, consumer);
        Future<Integer> future = producer.submit(produce);

        Integer numberOfValidCodes = 0;

        try
        {
            numberOfValidCodes = future.get();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }

        producer.shutdown();
        producer.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        System.out.println("Number of valid codes: " + numberOfValidCodes);
        System.out.println("Execution time: " + new SimpleDateFormat("mm:ss:SSS").format(new Date(elapsedTime)));


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