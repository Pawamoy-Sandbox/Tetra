import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CodeSet {

    public static List<String> S256 = new ArrayList<>();
    public static List<String> S240 = new ArrayList<>();
    public static List<String> S228 = new ArrayList<>();
    public static List<String> S126 = new ArrayList<>();
    public static List<String> S114 = new ArrayList<>();
    public static List<String> SC114 = new ArrayList<>();
    public static List<String> S16 = new ArrayList<>();
    public static List<String> S12 = new ArrayList<>();
    public static BitSet BS256 = new BitSet();
    public static BitSet BS240 = new BitSet();
    public static BitSet BS228 = new BitSet();
    public static BitSet BS126 = new BitSet();
    public static BitSet BS114 = new BitSet();
    public static BitSet BSC114 = new BitSet();
    public static BitSet BS16 = new BitSet();
    public static BitSet BS12 = new BitSet();
    public static List<List<BitSet>> ValidBS12 = new ArrayList<>();
    public static List<Integer> SByteCompl = new ArrayList<>();
    public static ConcurrentHashMap<BitSet, Boolean> BSWrong = new ConcurrentHashMap<>();

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

    private static void readValidS12()
    {
        for (int i = 1; i <= 6; i++)
        {
            ArrayList<BitSet> validCodes = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader("data/L" + i + "ValidS12.txt"))) {
                String line;

                while ((line = br.readLine()) != null) {
                    if (line.isEmpty())
                        continue;

                    String[] tetras = line.split("\t");

                    BitSet code = new BitSet();

                    for (int j = 0; j < i; j++)
                        code.set(stringToByte(tetras[j]));

                    validCodes.add(code);
                }
            } catch (IOException e) {
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

    public static boolean isAutoCompl(int tetra)
    {
        return tetra == compl(tetra);
    }

//    public static boolean isAutoComplCode(BitSet bitset)
//    {
//        for (int b = -1; (b = bitset.nextSetBit(b + 1)) != -1; )
//        {
//
//        }
//
//        return false;
//    }

    public static String byteToString(int t)
    {
        return S256.get(t);
    }

    public static int stringToByte(String t)
    {
        return S256.indexOf(t);
    }

    // FIXME: we really have to write our own generator method
    // that successively take tetras in S114/SC114 and S12

    // Try with this Python-like generator
//    public static io.herrmann.generator.Generator<Integer> combiner(int l)
//    {
//        io.herrmann.generator.Generator<Integer> g = new io.herrmann.generator.Generator<Integer>() {
//            public void run() throws InterruptedException {
//                // some logic here
//                yield(1);
//            }
//        };
//
//        return g;
//    }

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

    public static boolean containsSubset(BitSet bitset, ConcurrentHashMap<BitSet, Boolean> bitsetMap)
    {
        // FIXME: maybe write our own generator that works with BitSet
        Integer[] int_set = new Integer[bitset.cardinality()];

        int i = 0;
        for (int b = -1; (b = bitset.nextSetBit(b + 1)) != -1; )
            int_set[i++] = b;

        // Create an initial vector/set
        ICombinatoricsVector<Integer> initialSet = Factory.createVector(int_set);

        // Create an instance of the subset generator
        Generator<Integer> gen = Factory.createSubSetGenerator(initialSet);

        for (ICombinatoricsVector<Integer> subsetVector : gen) {
            // We only check subsets with size >= 2
            if (subsetVector.getSize() < 2)
                continue;

            // We do not check the bitset itself since we compute
            // wrong codes in ascending order. On length=3, we only
            // have codes of length=2 in the list.
            // FIXME: to avoid these 2 checks, we could write our own generator
            // that yields subsets of length >= 2 and < bitset's cardinality
            if (subsetVector.getSize() == bitset.cardinality())
                continue;

            // Convert vector to BitSet
            BitSet subset = new BitSet();
            for (Integer v : subsetVector.getVector())
                subset.set(v);

            // Check if subset is valid or not (contained in hashmap)
            if (bitsetMap.containsKey(subset))
                return true;
        }

        return false;
    }

    public static boolean isValidCode(BitSet bitset)
    {
        return ! containsSubset(bitset, BSWrong);
    }

    public static boolean addWrongCode(BitSet bitset)
    {
        if (! containsSubset(bitset, BSWrong))
        {
            BSWrong.put(bitset, true);
            return true;
        }

        return false;
    }

    public static BitSet vectorToBitset(ICombinatoricsVector<Integer> vector)
    {
        BitSet b = new BitSet();

        for (int i : vector.getVector())
            b.set(i);

        return b;
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