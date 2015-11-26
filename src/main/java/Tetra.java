import com.sun.org.apache.bcel.internal.classfile.Code;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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

        System.out.println("Number of active threads:         " + CodeSet.thread);
        System.out.println("Size of thread queue:             " + CodeSet.threadQueue);
        System.out.println("Size of threads buffer:           " + CodeSet.threadBuffer);
        System.out.println("Starting length:                  " + CodeSet.startL);
        System.out.println("Ending length:                    " + CodeSet.endL);
        System.out.println("Writing bytes instead of strings: " + CodeSet.writeBytesNoTetra);
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

        for (int l = start; l <= end; l++)
        {
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

            BigDecimal total = totalCombinations(l);
            float percent = BigDecimal.valueOf(numberOfValidCodes).divide(total, 2, RoundingMode.UP).floatValue() * 100;

            System.out.print(String.format("%1$18s", l + " | "));
            System.out.print(String.format("%1$18s", numberOfValidCodes + " (" + (int)percent + "%)" + " | "));
            System.out.println(String.format("%1$18s", new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(elapsedTime-1000*3600)) + " |"));
        }
    }

    private static BigDecimal totalCombinations(int l)
    {
        int BS12_choices;
        boolean even = (l % 2 == 0);

        if (l <= 6)
            BS12_choices = l;
        else if (even)
            BS12_choices = 6;
        else
            BS12_choices = 5;

        BigDecimal total = BigDecimal.ZERO;
        for (int i = BS12_choices; i > 0; i -= 2)
        {
            total = total.add(BigDecimal.valueOf(CombinatoricsUtils.binomialCoefficient(114, (l-i)/2) * CombinatoricsUtils.binomialCoefficient(12, i)));
        }

        if (even)
            total = total.add(BigDecimal.valueOf(CombinatoricsUtils.binomialCoefficient(114, l/2)));

        return total;
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
    }
}