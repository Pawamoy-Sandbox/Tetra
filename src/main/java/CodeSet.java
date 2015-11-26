import java.io.*;
import java.util.*;

public class CodeSet {

    public static List<String> S256 = new ArrayList<>();
    public static List<String> S240 = new ArrayList<>();
    public static List<String> S228 = new ArrayList<>();
    public static List<String> S126 = new ArrayList<>();
    public static List<String> S114 = new ArrayList<>();
    public static List<String> SC114 = new ArrayList<>();
    public static List<String> S108 = new ArrayList<>();
    public static List<String> SC108 = new ArrayList<>();
    public static List<String> S16 = new ArrayList<>();
    public static List<String> S12 = new ArrayList<>();
    public static BitSet BS256 = new BitSet();
    public static BitSet BS240 = new BitSet();
    public static BitSet BS228 = new BitSet();
    public static BitSet BS126 = new BitSet();
    public static BitSet BS114 = new BitSet();
    public static BitSet BSC114 = new BitSet();
    public static BitSet BS108 = new BitSet();
    public static BitSet BSC108 = new BitSet();
    public static BitSet BS16 = new BitSet();
    public static BitSet BS12 = new BitSet();
    public static List<List<BitSet>> ValidBS12 = new ArrayList<>();
    public static List<Integer> SByteCompl = new ArrayList<>();
    public static List<List<List<Integer>>> Splits = new ArrayList<>();

    public static int threadBuffer = 10000;
    public static int thread = 8;
    public static int threadQueue = 12;
    public static int startL = 2;
    public static int endL = 60;
    public static final boolean writeBytesNoTetra = true;

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
        readTetra108();
        readValidS12();
        readSplits();

        S12.removeAll(S16);
        BS12.andNot(BS16);

        // String sets
        S240 = new ArrayList<>(S256);
        S240.removeAll(S16);

        S228 = new ArrayList<>(S240);
        S228.removeAll(S12);

        S114 = new ArrayList<>();

        for (String s : S228){
            if (!SC114.contains(s)){
                SC114.add(compl(s));
                S114.add(s);
            }
        }

        S126 = new ArrayList<>(S114);
        S126.addAll(S12);
        Collections.sort(S126);

        // Bit sets
        BS240.or(BS256);
        BS240.andNot(BS16);

        BS228.or(BS240);
        BS228.andNot(BS12);

        for (String s : S114)
            BS114.set(stringToByte(s));
        BSC114.or(BS228);
        BSC114.andNot(BS114);

        BS126.or(BS114);
        BS126.or(BS12);
    }

    private static void readTetra256()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("data/S256.txt")))
        {
            String line;

            int i = 0;
            while ((line = br.readLine()) != null)
            {
                if (line.isEmpty())
                    continue;

                S256.add(line);
                BS256.set(i);

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
        try (BufferedReader br = new BufferedReader(new FileReader("data/S16.txt")))
        {
            String line;

            while ((line = br.readLine()) != null)
            {
                if (line.isEmpty())
                    continue;

                S16.add(line);
                BS16.set(stringToByte(line));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void readTetra108()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("data/S108.txt")))
        {
            String line;

            while ((line = br.readLine()) != null)
            {
                if (line.isEmpty())
                    continue;

                S108.add(line);
                BS108.set(stringToByte(line));

                SC108.add(compl(line));
                BSC108.set(compl(stringToByte(line)));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static BitSet lineToBitSet(String line, int l)
    {
        String[] tetras = line.split("\t");

        BitSet code = new BitSet();

        if (writeBytesNoTetra)
            for (int i = 0; i < l; i++)
                code.set(Integer.parseInt(tetras[i]));
        else
            for (int i = 0; i < l; i++)
                code.set(stringToByte(tetras[i]));

        return code;
    }

    public static BitSet readTetraLine(String line, int l)
    {
        String[] tetras = line.split("\t");

        BitSet code = new BitSet();

        for (int i = 0; i < l; i++)
            code.set(stringToByte(tetras[i]));

        return code;
    }

    private static void readValidS12()
    {
        for (int i = 1; i <= 6; i++)
        {
            ArrayList<BitSet> validCodes = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader("data/L" + i + "ValidS12.txt")))
            {
                String line;

                while ((line = br.readLine()) != null)
                {
                    if (line.isEmpty())
                        continue;

                    validCodes.add(readTetraLine(line, i));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            ValidBS12.add(validCodes);
        }
    }

    private static void readByteCompl()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("data/BS256Complement.txt")))
        {
            String line;

            while ((line = br.readLine()) != null)
                SByteCompl.add(Integer.parseInt(line));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void readSplits()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("data/TetraSplit.txt")))
        {
            String line;

            while ((line = br.readLine()) != null)
            {
                String[] parts = line.split("\t");

                List<List<Integer>> list = new ArrayList<>();

                for (int i = 0; i < 3; i++)
                {
                    List<Integer> subList = new ArrayList<>();

                    subList.add(Integer.valueOf(parts[i*2 + 1]));
                    subList.add(Integer.valueOf(parts[i*2 + 2]));

                    list.add(subList);
                }

                Splits.add(list);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String compl(String tetra)
    {
        StringBuilder res = new StringBuilder();

        for (char ch: tetra.toCharArray())
        {
            char chCompl = 'A';

            if (ch == 'A')
                chCompl ='T';
            if (ch == 'C')
                chCompl ='G';
            if (ch == 'G')
                chCompl ='C';
            if (ch == 'T')
                chCompl ='A';

            res.insert(0, chCompl);
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

    public static String byteToString(int t)
    {
        return S256.get(t);
    }

    public static int stringToByte(String t)
    {
        return S256.indexOf(t);
    }

    public static String bitsetToString(BitSet bitset)
    {
        if (writeBytesNoTetra)
        {
            // FIXME: great loss of performance here, use StringBuilder or something
            String result = "";

            for (int b = -1; (b = bitset.nextSetBit(b + 1)) != -1; )
                result += b + "\t";

            return result;
        }
        else
        {
            // FIXME: great loss of performance here, use StringBuilder or something
            String result = "";

            for (int b = -1; (b = bitset.nextSetBit(b + 1)) != -1; )
                result += byteToString(b) + "\t";

            return result;
        }
    }
}