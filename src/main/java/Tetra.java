import java.util.concurrent.*;

public class Tetra
{
    public static void main (String[] args) throws InterruptedException {
        CodeSet.initialize();

        long startTime = System.currentTimeMillis();

        //The numbers are just silly tune parameters. Refer to the API.
        //The important thing is, we are passing a bounded queue.
        ExecutorService consumer = Executors.newFixedThreadPool(8);

        //No need to bound the queue for this executor.
        //Use utility method instead of the complicated Constructor.
        ExecutorService producer = Executors.newSingleThreadExecutor();
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(consumer);

        Callable<Integer> produce = new Producer(completionService, consumer);
        Future<Integer> future = producer.submit(produce);

        int numberOfValidCodes = 0;

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
        System.out.println("Execution time: " + elapsedTime);


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