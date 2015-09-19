import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

public class CodeSet {

    public static List<Integer> SByteCompl;
    public static List<String> S256;
    public static List<String> S240;
    public static List<String> S228;
    public static List<String> S12;
    public static List<String> S16;
    public static BitSet BS12 = new BitSet();
    public static BitSet BS16 = new BitSet();

    // Singleton Stuff
    private static class SingletonHolder
    {
        public static final CodeSet INSTANCE = new CodeSet();
    }

    public static CodeSet initialize()
    {
        return SingletonHolder.INSTANCE;
    }
    // End Singleton Stuff

    private CodeSet()
    {
        readTetra256();
        readTetra16();
        readByteCompl();

        S240 = new ArrayList<>();
        S240.addAll(S256.stream().collect(Collectors.toList()));
        S16.forEach(S240::remove);

        S228 = new ArrayList<>();
        S228.addAll(S240.stream().collect(Collectors.toList()));
        S12.forEach(S228::remove);
    }

    private static void readTetra256()
    {
        S256 = new ArrayList<>();
        S12 = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("tetra256.txt")))
        {
            String line;

            int i = 0;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                S256.add(line);

                if (isAutoCompl(line))
                {
                    S12.add(line);
                    BS12.set(i);
                }

                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readTetra16()
    {
        S16 = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("tetra16.txt")))
        {
            String line;

            int i = 0;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty())
                    continue;

                S16.add(line);
                BS16.set(i);

                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readByteCompl()
    {
        SByteCompl = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("byteCompl.txt")))
        {
            String line;

            while ((line = br.readLine()) != null) {
                SByteCompl.add(Integer.parseInt(line));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String compl(String tetra)
    {
        StringBuilder res = new StringBuilder();

        for (char ch: tetra.toCharArray()) {
            char chCompl = 'A';

            if (ch == 'A')
                chCompl ='T';
            if (ch == 'C')
                chCompl ='G';
            if (ch == 'G')
                chCompl ='C';
            if (ch == 'T')
                chCompl ='A';

            res.insert(0,chCompl);
        }

        return res.toString();
    }

    public static int compl(int tetra)
    {
        return SByteCompl.get(tetra);
    }

    public static boolean isAutoCompl(String tetra)
    {
        return tetra.equals(compl(tetra));
    }

    public static boolean isAutoCompl(int tetra)
    {
        return tetra == compl(tetra);
    }

    public static String byteToString(int t)
    {
        return S256.get(t);
    }

    public static int stringToByte(String t)
    {
        return S256.indexOf(t);
    }
}