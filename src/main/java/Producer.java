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
    private int numberOfConsumers = 0;
    private final int window = 10000;
    private String withoutS12 = "";

    public Producer(CompletionService<Integer> completionService, ExecutorService consumerExecutor, int codeLength)
    {
        this.completionService = completionService;
        this.consumerExecutor = consumerExecutor;
        this.codeLength = codeLength;
    }

    @Override
    public Integer call() throws InterruptedException
    {
        int BS12_choices;
        boolean even = (codeLength % 2 == 0);

        if (codeLength <= 6)
            BS12_choices = codeLength;
        else if (even)
            BS12_choices = 6;
        else
            BS12_choices = 5;

        buffer = new ArrayList<>();

        // With tetras from S12
        for (int i = BS12_choices; i > 0; i -= 2)
        {
            List<BitSet> validS12 = CodeSet.ValidBS12.get(i-1);

            for (ICombinatoricsVector<Integer> v : CodeSet.combine(CodeSet.BS114, (codeLength - i) / 2))
            {
                for (BitSet bs12 : validS12)
                {
                    BitSet b = new BitSet();
                    b.or(bs12);
                    addInBuffer(b, v.getVector());
                }
            }
        }

        // Last "With S12" iteration
        numberOfConsumers++;
        count = 0;
        launchConsumer(buffer);
        buffer = new ArrayList<>();

        // Without tetras from S12 (only when codeLength is even)
        if (even)
        {
            withoutS12 = "WithoutS12-";
            for (ICombinatoricsVector<Integer> v : CodeSet.combine(CodeSet.BS114, codeLength / 2))
            {
                BitSet b = new BitSet();
                addInBuffer(b, v.getVector());
            }
        }


        // Last "Without S12" iteration
        numberOfConsumers++;
        launchConsumer(buffer);
        buffer = null;

        // Cumulative number of valid codes
        Integer total = 0;
        Integer res;

        try
        {
            for (int t = 0; t < numberOfConsumers; t++)
            {
                // Get result as soon as it comes
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
        Callable<Integer> consumer = new Consumer("Results/L" + codeLength + "/" + withoutS12 + "Thread"+ numberOfConsumers + ".txt", list);
        completionService.submit(consumer);
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
            launchConsumer(buffer);
            buffer = new ArrayList<>();
        }
    }
}
