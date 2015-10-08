import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class Producer implements Runnable
{
    private final ExecutorService consumerExecutor;

    public Producer(ExecutorService consumerExecutor)
    {
        this.consumerExecutor = consumerExecutor;
    }

    @Override
    public void run()
    {
        Generator<Integer> gen = CodeSet.combine(CodeSet.BS126, 5);

        int i = 0;
        int count = 0;

        List<ICombinatoricsVector<Integer>> l = new ArrayList<>();

        for (ICombinatoricsVector<Integer> v : gen)
        {
            l.add(v);
            count++;

            if (count >= 1000)
            {
                Runnable consumer = new Consumer("Thread"+i, l);
                i++;
                count = 0;
                consumerExecutor.submit(consumer);
                l.clear();
            }
        }

        consumerExecutor.shutdown();
        try {
            consumerExecutor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
