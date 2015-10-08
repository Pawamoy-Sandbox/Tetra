import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.util.ArrayList;
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
    public Integer call()
    {
        Generator<Integer> gen = CodeSet.combine(CodeSet.BS126, 3);

        int i = 0;
        int count = 0;

//        Integer total = 0;
//        Integer res;

//        List<Future<Integer>> futureList = new ArrayList<>();

        List<ICombinatoricsVector<Integer>> l  = new ArrayList<>();

        for (ICombinatoricsVector<Integer> v : gen)
        {
            l.add(v);
            count++;

            if (count >= 10000)
            {
                Callable<Integer> consumer = new Consumer("Thread"+i, l);
                i++;
                count = 0;
                completionService.submit(consumer);
//                futureList.add(completionService.submit(consumer));
                l = new ArrayList<>();
            }
        }

        // Last iteration (copy paste) (problem with l uninitialized)
//        Callable<Integer> consumer = new Consumer("Thread"+i, l);
//        i++;
//        count = 0;
//        completionService.submit(consumer);
//        futureList.add(completionService.submit(consumer));

        // Trying to get results but get ConcurrentException on take()
        // Was the same without completionService and a list of Future
//        try {
//            for (int t = 0; t < i; t++)
//            {
//                res = completionService.take().get();
//
//                if (res != null)
//                    total += res;
//            }
//        }
//        catch (InterruptedException | ExecutionException e)
//        {
//            e.printStackTrace();
//        }
//        finally
//        {
            consumerExecutor.shutdown();
//        }

        return 0;
    }
}
