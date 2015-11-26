import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

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
    public static List<BitSet> ValidL2BS114 = new ArrayList<>();
    public static List<Integer> SByteCompl = new ArrayList<>();
    public static List<List<List<Integer>>> Splits = new ArrayList<>();

    public static final int max_bs114 = 1;

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
        readValidS12();
        readSplits();
        readValidL2S114();

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
                SC114.add(CodeSet.compl(s));
                S114.add(s);
            }
        }

        S126 = new ArrayList<>(S114);
        S126.addAll(S12);
        Collections.sort(S126);

        S108 = new ArrayList<>(S114);
        S108.remove("ATCG");
        S108.remove("ATGC");
        S108.remove("ATTA");
        S108.remove("CGGC");
        S108.remove("CGTA");
        S108.remove("GCTA");

        SC108 = new ArrayList<>(SC114);
        SC108.remove(compl("ATCG"));
        SC108.remove(compl("ATGC"));
        SC108.remove(compl("ATTA"));
        SC108.remove(compl("CGGC"));
        SC108.remove(compl("CGTA"));
        SC108.remove(compl("GCTA"));

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

        BS108.or(BS114);
        BS108.clear(stringToByte("ATCG"));
        BS108.clear(stringToByte("ATGC"));
        BS108.clear(stringToByte("ATTA"));
        BS108.clear(stringToByte("CGGC"));
        BS108.clear(stringToByte("CGTA"));
        BS108.clear(stringToByte("GCTA"));

        BSC108.or(BSC114);
        BSC108.clear(compl(stringToByte("ATCG")));
        BSC108.clear(compl(stringToByte("ATGC")));
        BSC108.clear(compl(stringToByte("ATTA")));
        BSC108.clear(compl(stringToByte("CGGC")));
        BSC108.clear(compl(stringToByte("CGTA")));
        BSC108.clear(compl(stringToByte("GCTA")));
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

    public static BitSet lineToBitSet(String line, int l)
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

                    validCodes.add(lineToBitSet(line, i));
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            ValidBS12.add(validCodes);
        }
    }

    private static void readValidL2S114()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("data/L2ValidS114.txt")))
        {
            String line;

            while ((line = br.readLine()) != null)
            {
                if (line.isEmpty())
                    continue;

                ValidL2BS114.add(lineToBitSet(line, 2));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
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

    public static String byteToString(int t)
    {
        return S256.get(t);
    }

    public static int stringToByte(String t)
    {
        return S256.indexOf(t);
    }

    public static Generator<Integer> combine(BitSet source, int l)
    {
        // FIXME: maybe write our own generator that works with BitSet
        Integer[] int_set = new Integer[source.cardinality()];

        int i = 0;
        for (int b = -1; (b = source.nextSetBit(b + 1)) != -1; )
            int_set[i++] = b;

        // Create the initial vector
        ICombinatoricsVector<Integer> initialVector = Factory.createVector(int_set);

        // Create a simple combination generator to generate 4-combinations of the initial vector
        return Factory.createSimpleCombinationGenerator(initialVector, l);
    }

    public static String byteListToString(List<Integer> list)
    {
        // FIXME: great loss of performance here, use StringBuilder or something
        String result = "";

        for (Integer i : list)
            result += byteToString(i) + "\t";

        return result;
    }
}