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

    public Producer(CompletionService<Integer> completionService, ExecutorService consumerExecutor)
    {
        this.completionService = completionService;
        this.consumerExecutor = consumerExecutor;
    }

    @Override
    public Integer call() throws InterruptedException
    {
        int numberOfConsumers = 0;
        int count = 0;
        int codeLength = 3;
        BitSet S = CodeSet.BS126;

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
                launchConsumer(l);
                l = new ArrayList<>();
            }
        }

        // Last iteration (copy paste) (problem with l uninitialized)
        launchConsumer(l);
        l = null;

        // Cumulative number of valid codes
        Integer total = 0;
        Integer res;

        try {
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

    private void launchConsumer(List<BitSet> list)
    {
        Callable<Integer> consumer = new Consumer(list);
        boolean accepted = false;

        while (! accepted)
        {
            try
            {
                completionService.submit(consumer);
                accepted = true;
            }
            catch (RejectedExecutionException e)
            {
//                e.printStackTrace();
            }
        }
    }
}