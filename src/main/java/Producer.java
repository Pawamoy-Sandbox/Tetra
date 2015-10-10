import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;
import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

public class Producer implements Callable<Integer>
{
    private final ExecutorService consumerExecutor;
    private final CompletionService<Integer> completionService;
    private final int codeLength;
    private final boolean writeResults;

    public Producer(CompletionService<Integer> completionService, ExecutorService consumerExecutor, int codeLength, boolean writeResults)
    {
        this.completionService = completionService;
        this.consumerExecutor = consumerExecutor;
        this.codeLength = codeLength;
        this.writeResults = writeResults;
    }

    @Override
    public Integer call() throws InterruptedException
    {
        int numberOfConsumers = 0;
        int count = 0;
        BitSet S = CodeSet.BS12;

        // NOTE: we can use apache combinatorics utils: binomialCoefficient
//        long totalCombinations = CombinatoricsUtils.binomialCoefficient(codeLength, S.cardinality());
        int window = 10000;

        Generator<Integer> gen = CodeSet.combine(S, codeLength);
        List<BitSet> l  = new ArrayList<>();

        for (ICombinatoricsVector<Integer> v : gen)
        {
            l.add(CodeSet.vectorToBitset(v));
            count++;

            if (count == window)
            {
                numberOfConsumers++;
                count = 0;
                launchConsumer("Thread"+numberOfConsumers, l);
                l = new ArrayList<>();
            }
        }

        // Last iteration (copy paste) (problem with l uninitialized)
        launchConsumer("Thread"+(numberOfConsumers+1), l);
        l = null;

        // Cumulative number of valid codes
        Integer total = 0;
        Integer res;

        try
        {
            for (int t = 0; t <= numberOfConsumers; t++)
            {
                res = completionService.take().get();

                if (res != null)
                    total += res;
            }
        }
        catch (InterruptedException | ExecutionException e)
        {
            e.printStackTrace();
        }
        finally
        {
            consumerExecutor.shutdown();
            consumerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        }

        return total;
    }

    private void launchConsumer(String threadName, List<BitSet> list)
    {
        Callable<Integer> consumer = new Consumer(threadName, list, codeLength, writeResults);
        boolean accepted = false;

        while (! accepted)
        {
            try
            {
                completionService.submit(consumer);
                accepted = true;
            }
            catch (RejectedExecutionException ignored) {}
        }
    }
}
