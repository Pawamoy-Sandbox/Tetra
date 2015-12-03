import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class Tetra
{
    public static void main (String[] args)
    {
        CodeSet.initialize();

        for (int i = 0; i < args.length; i++)
        {
            switch (args[i]) {
                case "-b":
                case "--thread-buffer":
                    CodeSet.threadBuffer = Integer.parseInt(args[++i]);
                    break;
                case "-t":
                case "--thread":
                    CodeSet.thread = Integer.parseInt(args[++i]);
                    break;
                case "-q":
                case "--thread-queue":
                    CodeSet.threadQueue = Integer.parseInt(args[++i]);
                    break;
                case "-s":
                case "--start":
                    CodeSet.startL = Integer.parseInt(args[++i]);
                    break;
                case "-e":
                case "--end":
                    CodeSet.endL = Integer.parseInt(args[++i]);
                    break;
                case "-m":
                case "--master":
                    CodeSet.master = true; // Default behavior
                    break;
                case "-w":
                case "--worker":
                    CodeSet.master = false;
                    break;
                case "-v":
                case "--even":
                    CodeSet.evenOnly = true;
                    break;
                case "-h":
                case "--help":
                    printHelp();
                    System.exit(0);
                default:
                    System.err.println("Unknown argument " + args[i]);
                    printHelp();
                    System.exit(1);
            }
        }

        System.out.println("Threads:          " + CodeSet.thread + " active / " + CodeSet.threadQueue + " queued");
        System.out.println("Threads buffer:   " + CodeSet.threadBuffer);
        if (CodeSet.startL == CodeSet.endL)
            System.out.println("Computed length:  " + CodeSet.startL);
        else
            System.out.println("Computed length:  " + CodeSet.startL + " to " + CodeSet.endL);
        if (CodeSet.writeBytesNoTetra)
            System.out.println("Output format:    tetra indexes");
        else
            System.out.println("Output format:    tetra strings");
        System.out.println("Even length only: " + CodeSet.evenOnly);
        if (CodeSet.master)
            System.out.println("Process status:   master");
        else
            System.out.println("Process status:   worker");
        System.out.println();

        try
        {
            launchLoop(CodeSet.startL, CodeSet.endL);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    private static void launchLoop(int start, int end) throws InterruptedException
    {
        System.out.print(String.format("%1$18s", "Code length | "));
        System.out.print(String.format("%1$18s", "Valid codes | "));
        System.out.print(String.format("%1$18s", "Execution time |"));
        System.out.println();
        System.out.print("----------------|-");
        System.out.print("----------------|-");
        System.out.print("-----------------|");
        System.out.println();

        long startTime = System.currentTimeMillis();

        int increment = 1;

        if (CodeSet.evenOnly)
        {
            if (start % 2 != 0)
            {
                System.err.println("Starting length is not even");
                System.exit(1);
            }

            increment = 2;
        }

        for (int l = start; l <= end; l+=increment)
        {
            System.out.print(String.format("%1$18s", l + " | "));
            System.out.flush();

            File LDir = new File("Results/L" + l);
            LDir.mkdirs();

            // Queue size has to be related to window size of combinations splits:
            // we can have a 100-length queue of 1000-graph-sized threads,
            // or a 1000-length queue of 100-graph-sized threads...
            ThreadPoolExecutor consumer = new ThreadPoolExecutor(
                    CodeSet.thread, CodeSet.thread, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(CodeSet.threadQueue));

            // this will block if the queue is full as opposed to throwing exception
            consumer.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

            //No need to bound the queue for this executor.
            //Use utility method instead of the complicated Constructor.
            CompletionService<Integer> completionService = new ExecutorCompletionService<>(consumer);

            ExecutorService producerExecutor = Executors.newSingleThreadExecutor();
            Callable<Integer> producer = new Producer(completionService, consumer, l);

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

            // TODO: why bother computing that each time when we can hardcode it once and for all
//            BigDecimal total = totalCombinations(l);
//            float percent = BigDecimal.valueOf(numberOfValidCodes).divide(total, 2, RoundingMode.UP).floatValue() * 100;

            System.out.print(String.format("%1$18s", numberOfValidCodes + " | "));
//            System.out.print(String.format("%1$18s", numberOfValidCodes + " (" + (int)percent + "%)" + " | "));
            System.out.println(String.format("%1$18s", new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(elapsedTime-1000*3600)) + " |"));
        }
    }

    private static void printHelp()
    {
//        case "-b":
//        case "--thread-buffer":
//        CodeSet.threadBuffer = Integer.parseInt(args[++i]);
//        break;
//        case "-t":
//        case "--thread":
//        CodeSet.thread = Integer.parseInt(args[++i]);
//        break;
//        case "-q":
//        case "--thread-queue":
//        CodeSet.threadQueue = Integer.parseInt(args[++i]);
//        break;
//        case "-s":
//        case "--start":
//        CodeSet.startL = Integer.parseInt(args[++i]);
//        break;
//        case "-e":
//        case "--end":
//        CodeSet.endL = Integer.parseInt(args[++i]);
//        break;
//        case "-m":
//        case "--master":
//        CodeSet.master = true; // Default behavior
//        break;
//        case "-w":
//        case "--worker":
//        CodeSet.master = false;
//        break;
//        case "-h":
//        case "--help":
    }
}