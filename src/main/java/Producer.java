import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

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
    public Integer call() throws InterruptedException {
        Generator<Integer> gen = CodeSet.combine(CodeSet.BS126, 3);

        int i = 0;
        int count = 0;
        int window = 10000;

        int j = 0;

        Integer total = 0;
        Integer res;

        List<BitSet> l  = new ArrayList<>();

        for (ICombinatoricsVector<Integer> v : gen)
        {
            l.add(CodeSet.vectorToBitset(v));
            count++;
            j++;

            if (count == window)
            {
                Callable<Integer> consumer = new Consumer(l);
                i++;
                System.out.println("Number of combinations generated: " + j);
                count = 0;

                boolean accepted = false;

                while (! accepted)
                {
                    try {
                        completionService.submit(consumer);
                        accepted = true;
                    } catch (RejectedExecutionException e) {
//                        e.printStackTrace();
                    }
                }

                l = new ArrayList<>();
            }
        }

        // Last iteration (copy paste) (problem with l uninitialized)
        System.out.println("Number of combinations generated: " + j);
        System.out.println("Last iteration list length: " + l.size() + " " + l);
//        Callable<Integer> consumer = new Consumer("Thread"+i, l);
//        i++;
//        completionService.submit(consumer);

        // Cumulative number of valid codes
        try {
            for (int t = 0; t < i; t++)
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
}
