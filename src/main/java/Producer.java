import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;

public class Producer implements Callable<BigInteger>
{
    private final ExecutorService consumerExecutor;
    private final CompletionService<Integer> completionService;
    private final int codeLength;
    private List<BitSet> buffer;
    private int count = 0;
    private int numberOfConsumers = 0;
    private int bufferSize;
    private String withoutS12 = "";

    public Producer(CompletionService<Integer> completionService, ExecutorService consumerExecutor, int codeLength)
    {
        this.completionService = completionService;
        this.consumerExecutor = consumerExecutor;
        this.codeLength = codeLength;
        this.bufferSize = CodeSet.threadBuffer;
    }

    @Override
    public BigInteger call() throws InterruptedException
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

        final Pattern p = Pattern.compile("^W.*");
        final FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                return p.matcher(file.getName()).matches();
            }
        };

        // TODO: work in progress
        if (CodeSet.resume)
        {
            BitSet lastCode = null;

            File resultDir = new File("Results/L" + (codeLength));

            File[] list = resultDir.listFiles(filter);
            Arrays.sort(list);

            // Get the last file (last thread output)
            File lastFile = list[list.length-1];

            // Get the last line, and the line before in case of incompleteness
            try (BufferedReader br = new BufferedReader(new FileReader(lastFile)))
            {
                String line;
                String lastLine = null;

                // TODO: beforeLastLine not necessary if we implement an interruption handler
                // that terminates correctly the last threads, and only then, exit
                String beforeLastLine = null;

                while ((line = br.readLine()) != null)
                {
                    if (line.isEmpty())
                        continue;

                    beforeLastLine = lastLine;
                    lastLine = line;
                }

                if (lastLine != null)
                {
                    try
                    {
                        lastCode = CodeSet.readTetraLine(lastLine, codeLength);
                    }
                    catch (IndexOutOfBoundsException e)
                    {
                        lastCode = CodeSet.readTetraLine(beforeLastLine, codeLength);
                    }
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            if (lastCode != null)
            {
                // Determine where we were in the loop based on number of S12 in the code
                BitSet andS12 = new BitSet();
                andS12.or(lastCode);
                andS12.and(CodeSet.BS12);

                int numberOfS12 = andS12.cardinality();
            }
        }

        // With tetras from S12
        for (int i = BS12_choices; i > 0; i -= 2)
        {
            List<BitSet> validS12 = CodeSet.ValidBS12.get(i-1);

            // The following loop does not depend on previous results, therefore it must not be run by workers
            if (codeLength - i == 2 && CodeSet.master)
            {
                for (int bs108 = -1; (bs108 = CodeSet.BS108.nextSetBit(bs108 + 1)) != -1; )
                {
                    for (BitSet bs12 : validS12)
                    {
                        BitSet code = new BitSet();
                        code.or(bs12);
                        code.set(bs108);
                        code.set(CodeSet.compl(bs108));
                        addInBuffer(code);
                    }
                }
            }
            // The following loop does not depend on previous results, therefore it must not be run by workers
            else if (codeLength == i && CodeSet.master)
            {
                for (BitSet bs12 : validS12)
                {
                    BitSet code = new BitSet();
                    code.or(bs12);
                    addInBuffer(code);
                }
            }
            else
            {
                File resultDir = new File("Results/L" + (codeLength - i));

                File[] list = resultDir.listFiles(filter);

                // NOTE: is this really necessary to sort the array
                // since each iteration is independent from the previous ones?
                // BUT! it is mandatory for resuming
                Arrays.sort(list);

                for (File file : list)
                {
                    try (BufferedReader br = new BufferedReader(new FileReader(file)))
                    {
                        String line;

                        while ((line = br.readLine()) != null)
                        {
                            if (line.isEmpty())
                                continue;

                            BitSet rb = CodeSet.lineToBitSet(line, (codeLength - i));

                            for (BitSet bs12 : validS12)
                            {
                                BitSet b = new BitSet();
                                b.or(bs12);
                                b.or(rb);

                                addInBuffer(b);
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
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

            // The following loop does not depend on previous results, therefore it must not be run by workers
            if (codeLength == 2 && CodeSet.master)
            {
                for (int bit = -1; (bit = CodeSet.BS108.nextSetBit(bit + 1)) != -1; )
                {
                    BitSet b = new BitSet();
                    b.set(bit);
                    b.set(CodeSet.compl(bit));
                    addInBuffer(b);
                }
            }
            else
            {
                File resultDir = new File("Results/L" + (codeLength - 2));

                File[] list = resultDir.listFiles(filter);
                // NOTE: is this really necessary to sort the array
                // since each iteration is independent from the previous ones?
                // BUT! it is mandatory for resuming
                Arrays.sort(list);

                for (File file : list)
                {
                    try (BufferedReader br = new BufferedReader(new FileReader(file)))
                    {
                        String line;

                        while ((line = br.readLine()) != null)
                        {
                            if (line.isEmpty())
                                continue;

                            BitSet rb = CodeSet.lineToBitSet(line, codeLength - 2);
                            BitSet choices = new BitSet();
                            BitSet rbNoCompl = new BitSet();

                            rbNoCompl.or(rb);
                            rbNoCompl.andNot(CodeSet.BSC108);
                            choices.or(CodeSet.BS108);
                            choices.clear(0, rbNoCompl.length());

                            for (int bit = -1; (bit = choices.nextSetBit(bit + 1)) != -1; )
                            {
                                BitSet bitset = new BitSet();
                                bitset.or(rb);
                                bitset.set(bit);
                                bitset.set(CodeSet.compl(bit));
                                addInBuffer(bitset);
                            }
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }


            // Last "Without S12" iteration
            numberOfConsumers++;
            launchConsumer(buffer);
            buffer = null;
        }

        // Cumulative number of valid codes
        BigInteger total = BigInteger.ZERO;
        BigInteger res;

        try
        {
            for (int t = 0; t < numberOfConsumers; t++)
            {
                // Get result as soon as it comes
                res = BigInteger.valueOf(completionService.take().get());

                if (res != null)
                    total = total.add(res);
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

    private void addInBuffer(BitSet b)
    {
        buffer.add(b);
        count++;

        if (count == bufferSize)
        {
            numberOfConsumers++;
            count = 0;
            launchConsumer(buffer);
            buffer = new ArrayList<>();
        }
    }
}
