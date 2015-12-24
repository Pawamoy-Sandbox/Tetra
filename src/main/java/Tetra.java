import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

public class Tetra extends JFrame
{
    public static JTextArea text;
    private final static String newline = "\n";

    public Tetra()
    {
        initUI();
    }

    private void initUI()
    {
        final JButton startButton = new JButton("Start");
        text = new JTextArea(5, 20);
        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        text.setEditable(false);

        Font font = new Font("Courier", Font.PLAIN, 12);
        text.setFont(font);

        text.append("Threads:          " + CodeSet.thread + " active / " + CodeSet.threadQueue + " queued" + newline);
        text.append("Threads buffer:   " + CodeSet.threadBuffer + newline);
        if (CodeSet.startL == CodeSet.endL)
            text.append("Computed length:  " + CodeSet.startL + newline);
        else
            text.append("Computed length:  " + CodeSet.startL + " to " + CodeSet.endL + newline);
        if (CodeSet.writeBytesNoTetra)
            text.append("Output format:    tetra indexes" + newline);
        else
            text.append("Output format:    tetra strings" + newline);
        text.append("Even length only: " + CodeSet.evenOnly);
        if (CodeSet.master)
            text.append("Process status:   master" + newline);
        else
            text.append("Process status:   worker" + newline);
        text.append(newline);

        startButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent event)
            {
                startButton.setEnabled(false);

                new Thread()
                {
                    @Override
                    public void run()
                    {
                        CodeSet.initialize();

                        try
                        {
                            launchLoop(CodeSet.startL, CodeSet.endL);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        });

        createLayout(startButton, scrollPane);
        setTitle("Tetra");
        setSize(500, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void createLayout(JComponent jc1, JComponent jc2)
    {
        Container pane = getContentPane();
        GroupLayout gl = new GroupLayout(pane);
        pane.setLayout(gl);

        gl.setAutoCreateContainerGaps(true);
        gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(jc1).addComponent(jc2));
        gl.setVerticalGroup(gl.createSequentialGroup().addComponent(jc1).addComponent(jc2));
    }

    public static void main(String[] args)
    {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Tetra ex = new Tetra();
                ex.setVisible(true);
            }
        });

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
                case "-2":
                case "--even":
                    CodeSet.evenOnly = true;
                    break;
                // TODO: work in progress
//                case "-r":
//                case "--resume":
//                    CodeSet.resume = true;
//                    break;
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
//        System.out.println("Resume:           " + CodeSet.resume);
        if (CodeSet.master)
            System.out.println("Process status:   master");
        else
            System.out.println("Process status:   worker");
        System.out.println();
    }

    private static void launchLoop(int start, int end) throws InterruptedException
    {
        int increment = 1;

        if (CodeSet.evenOnly)
        {
            if (start % 2 != 0)
            {
                System.err.println("Starting length is not even (incompatible with -2 option)");
                System.exit(1);
            }

            increment = 2;
        }

        System.out.print(String.format("%1$18s", "Code length | "));
        System.out.print(String.format("%1$18s", "Valid codes | "));
        System.out.print(String.format("%1$18s", "Execution time |"));
        System.out.println();
        System.out.print("----------------|-");
        System.out.print("----------------|-");
        System.out.print("-----------------|");
        System.out.println();

        text.append(String.format("%1$18s", "Code length | "));
        text.append(String.format("%1$18s", "Valid codes | "));
        text.append(String.format("%1$18s", "Execution time |"));
        text.append(newline);
        text.append("----------------|-");
        text.append("----------------|-");
        text.append("-----------------|");
        text.append(newline);

        long startTime = System.currentTimeMillis();

        for (int l = start; l <= end; l+=increment)
        {
            System.out.print(String.format("%1$18s", l + " | "));
            System.out.flush();

            text.append(String.format("%1$18s", l + " | "));

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
            Callable<BigInteger> producer = new Producer(completionService, consumer, l);

            Future<BigInteger> future = producerExecutor.submit(producer);

            BigInteger numberOfValidCodes = BigInteger.ZERO;

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

            System.out.print(String.format("%1$18s", numberOfValidCodes.toString() + " | "));
//            System.out.print(String.format("%1$18s", numberOfValidCodes + " (" + (int)percent + "%)" + " | "));
            System.out.println(String.format("%1$18s", new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(elapsedTime-1000*3600)) + " |"));

            text.append(String.format("%1$18s", numberOfValidCodes.toString() + " | "));
//            System.out.print(String.format("%1$18s", numberOfValidCodes + " (" + (int)percent + "%)" + " | "));
            text.append(String.format("%1$18s", new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(elapsedTime-1000*3600)) + " |"));
            text.append(newline);
        }
    }

    private static void printHelp()
    {
        System.out.println("usage: java -jar tetra.jar [-t NUM_THREAD] [-q THREAD_QUEUE] [-b THREAD_BUFFER] [-s STARTING_LENGTH] [-e ENDING_LENGTH] [-m | -w] [-2] [-h]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -2, --even");
        System.out.println("    The process will only compute even lengths. It is possible because computation");
        System.out.println("    of the current length only rely on the three previous even length. In the same way,");
        System.out.println("    you cannot compute odd lengths only since they rely on the three previous even lengths too.");
        System.out.println("    Default to " + CodeSet.evenOnly + ".");
        System.out.println();
        System.out.println("  -b, --thread-buffer THREAD_BUFFER");
        System.out.println("    The number of codes each thread will check.");
        System.out.println("    Default to " + CodeSet.threadBuffer  +".");
        System.out.println();
        System.out.println("  -e, --end ENDING_LENGTH");
        System.out.println("    The desired length to end with (inclusive).");
        System.out.println("    Default to " + CodeSet.endL  +".");
        System.out.println();
        System.out.println("  -h, --help");
        System.out.println("    Print this help and exit.");
        System.out.println();
        System.out.println("  -m, --master");
        System.out.println("    This option is useful when you split the previous result in several parts,");
        System.out.println("    in order to launch the computation on several machines. The master process");
        System.out.println("    will check ALL the codes (depending on previous results or not).");
        System.out.println("    This is the default behavior, this option is implicit.");
        System.out.println();
        System.out.println("  -q, --thread-queue THREAD_QUEUE");
        System.out.println("    Size of the thread queue.");
        System.out.println("    With a queue of 6 and 2 active threads, there will be 4 waiting threads in the queue.");
        System.out.println("    Default to " + CodeSet.threadQueue  +".");
        System.out.println();
        // TODO: work in progress
//        System.out.println("  -r, --resume");
//        System.out.println("    The computation will restart at the last valid code computed.");
//        System.out.println("    Default to " + CodeSet.resume + ".");
//        System.out.println();
        System.out.println("  -s, --start STARTING_LENGTH");
        System.out.println("    The desired length to start with (inclusive).");
        System.out.println("    Default to " + CodeSet.startL  +".");
        System.out.println();
        System.out.println("  -t, --thread NUM_THREAD");
        System.out.println("    Number of threads (consumers) that will be active in parallel.");
        System.out.println("    The active threads are part of the thread queue (not an addition).");
        System.out.println("    Default to " + CodeSet.thread  +".");
        System.out.println();
        System.out.println("  -w, --worker");
        System.out.println("    This option is useful when you split the previous result in several parts,");
        System.out.println("    in order to launch the computation on several machines. The worker process");
        System.out.println("    will check ONLY the codes that depend on previous results. This avoids duplicated");
        System.out.println("    codes to be computed and written on disk.");
    }
}
