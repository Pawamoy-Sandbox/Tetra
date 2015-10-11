import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;

public class Producer implements Callable<Integer>
{
    private final ExecutorService consumerExecutor;
    private final CompletionService<Integer> completionService;
    private final int codeLength;
    private List<BitSet> buffer;
    private int count = 0;
    private int numberOfConsumers= 0;
    private int window = 10000;

    public Producer(CompletionService<Integer> completionService, ExecutorService consumerExecutor, int codeLength)
    {
        this.completionService = completionService;
        this.consumerExecutor = consumerExecutor;
        this.codeLength = codeLength;
    }

    @Override
    public Integer call() throws InterruptedException
    {
        // NOTE: we can use apache combinatorics utils: binomialCoefficient
//        long totalCombinations = CombinatoricsUtils.binomialCoefficient(codeLength, S.cardinality());
        int BS12_choices;

        if (codeLength <= 6)
            BS12_choices = codeLength;
        else if (codeLength % 2 == 0)
            BS12_choices = 6;
        else
            BS12_choices = 5;

        buffer = new ArrayList<>();
        List<BitSet> validS12;

        for (int i = BS12_choices; i >= 0; i -= 2)
        {
            int spaceLeft = codeLength - i;

            for (ICombinatoricsVector<Integer> v : CodeSet.combine(CodeSet.BS114, spaceLeft / 2))
            {
                if (i > 0)
                {
                    validS12 = CodeSet.ValidBS12.get(i-1);

                    for (BitSet bs12 : validS12)
                    {
                        BitSet b = new BitSet();
                        b.or(bs12);
                        addInBuffer(b, v.getVector());
                    }
                }
                else
                {
                    BitSet b = new BitSet();
                    addInBuffer(b, v.getVector());
                }
            }
        }

        // Last iteration (copy paste) (problem with l uninitialized)
        launchConsumer("Results/L" + codeLength + "/Thread"+(numberOfConsumers+1) + ".txt", buffer);
        buffer = null;

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
        Callable<Integer> consumer = new Consumer(threadName, list);
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

    private void addInBuffer(BitSet b, List<Integer> vector)
    {
        for (Integer bit : vector)
        {
            b.set(bit);
            b.set(CodeSet.compl(bit));
        }

        buffer.add(b);
        count++;

        if (count == window)
        {
            numberOfConsumers++;
            count = 0;
            launchConsumer("Results/L" + codeLength + "/Thread" + numberOfConsumers + ".txt", buffer);
            buffer = new ArrayList<>();
        }
    }
}
