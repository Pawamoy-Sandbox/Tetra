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
    public static List<List<List<Integer>>> Splits = new ArrayList<>();

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

    public static int getIntersectionSize(BitSet bits1, BitSet bits2) {
        BitSet copy = (BitSet) bits1.clone();
        copy.and(bits2);
        return copy.cardinality();
    }

    public static boolean containsSubset(BitSet bitset, ConcurrentHashMap<BitSet, Boolean> bitsetMap)
    {
        /*
        for (BitSet nonvalidSet : bitsetMap.keySet()) {
            if (getIntersectionSize(bitset, nonvalidSet) >= nonvalidSet.cardinality())
                return true;
        }
        */

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

//    public static boolean containsSubset(BitSet bitset, ConcurrentHashMap<BitSet, Boolean> bitsetMap)
//    {
//        for (BitSet key : bitsetMap.keySet())
//        {
//            BitSet copy = (BitSet) bitset.clone();
//
//            copy.and(key);
//            if (copy == key)
//                return true;
//        }
//
//        return false;
//    }

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

    public static int aminoAcidValue(char c) {
        if (c == 'A') {
            return 1;
        }
        if (c == 'C') {
            return 2;
        }
        if (c == 'G') {
            return 3;
        }
        if (c == 'T') {
            return 4;
        }

        return 0;
    }

    public static int stringToTetraSubset(String subTetra, int start, int end)
    {
        int result = 0;

        int exp = 0;
        for (int i = start; i < end; i++) {
            result += aminoAcidValue(subTetra.charAt(i)) * (Math.pow(10, exp));
            exp++;
        }

        return result;
    }

    public static String byteListToString(List<Integer> list)
    {
        // FIXME: great loss of performance here, use StringBuilder or something
        String result = "";

        for (Integer i : list)
            result += byteToString(i) + "\t";

        return result;
    }

    public static void printSplittedTetra()
    {
        for (String tetra : CodeSet.S256)
        {
            System.out.print(tetra + "\t");
            for (int i = 0; i < 3; i++)
            {
                System.out.print(CodeSet.toInt(tetra.substring(0, i + 1)) + "\t");
                System.out.print(CodeSet.toInt(tetra.substring(i + 1)) + "\t");
            }
            System.out.println();
        }
    }

    public static int toInt(String tetraPart)
    {
        if (tetraPart.equals("A")) return 0;
        if (tetraPart.equals("C")) return 1;
        if (tetraPart.equals("G")) return 2;
        if (tetraPart.equals("T")) return 3;
        if (tetraPart.equals("AA")) return 4;
        if (tetraPart.equals("AC")) return 5;
        if (tetraPart.equals("AG")) return 6;
        if (tetraPart.equals("AT")) return 7;
        if (tetraPart.equals("CA")) return 8;
        if (tetraPart.equals("CC")) return 9;
        if (tetraPart.equals("CG")) return 10;
        if (tetraPart.equals("CT")) return 11;
        if (tetraPart.equals("GA")) return 12;
        if (tetraPart.equals("GC")) return 13;
        if (tetraPart.equals("GG")) return 14;
        if (tetraPart.equals("GT")) return 15;
        if (tetraPart.equals("TA")) return 16;
        if (tetraPart.equals("TC")) return 17;
        if (tetraPart.equals("TG")) return 18;
        if (tetraPart.equals("TT")) return 19;
        if (tetraPart.equals("AAA")) return 20;
        if (tetraPart.equals("AAC")) return 21;
        if (tetraPart.equals("AAG")) return 22;
        if (tetraPart.equals("AAT")) return 23;
        if (tetraPart.equals("ACA")) return 24;
        if (tetraPart.equals("ACC")) return 25;
        if (tetraPart.equals("ACG")) return 26;
        if (tetraPart.equals("ACT")) return 27;
        if (tetraPart.equals("AGA")) return 28;
        if (tetraPart.equals("AGC")) return 29;
        if (tetraPart.equals("AGG")) return 30;
        if (tetraPart.equals("AGT")) return 31;
        if (tetraPart.equals("ATA")) return 32;
        if (tetraPart.equals("ATC")) return 33;
        if (tetraPart.equals("ATG")) return 34;
        if (tetraPart.equals("ATT")) return 35;
        if (tetraPart.equals("CAA")) return 36;
        if (tetraPart.equals("CAC")) return 37;
        if (tetraPart.equals("CAG")) return 38;
        if (tetraPart.equals("CAT")) return 39;
        if (tetraPart.equals("CCA")) return 40;
        if (tetraPart.equals("CCC")) return 41;
        if (tetraPart.equals("CCG")) return 42;
        if (tetraPart.equals("CCT")) return 43;
        if (tetraPart.equals("CGA")) return 44;
        if (tetraPart.equals("CGC")) return 45;
        if (tetraPart.equals("CGG")) return 46;
        if (tetraPart.equals("CGT")) return 47;
        if (tetraPart.equals("CTA")) return 48;
        if (tetraPart.equals("CTC")) return 49;
        if (tetraPart.equals("CTG")) return 50;
        if (tetraPart.equals("CTT")) return 51;
        if (tetraPart.equals("GAA")) return 52;
        if (tetraPart.equals("GAC")) return 53;
        if (tetraPart.equals("GAG")) return 54;
        if (tetraPart.equals("GAT")) return 55;
        if (tetraPart.equals("GCA")) return 56;
        if (tetraPart.equals("GCC")) return 57;
        if (tetraPart.equals("GCG")) return 58;
        if (tetraPart.equals("GCT")) return 59;
        if (tetraPart.equals("GGA")) return 60;
        if (tetraPart.equals("GGC")) return 61;
        if (tetraPart.equals("GGG")) return 62;
        if (tetraPart.equals("GGT")) return 63;
        if (tetraPart.equals("GTA")) return 64;
        if (tetraPart.equals("GTC")) return 65;
        if (tetraPart.equals("GTG")) return 66;
        if (tetraPart.equals("GTT")) return 67;
        if (tetraPart.equals("TAA")) return 68;
        if (tetraPart.equals("TAC")) return 69;
        if (tetraPart.equals("TAG")) return 70;
        if (tetraPart.equals("TAT")) return 71;
        if (tetraPart.equals("TCA")) return 72;
        if (tetraPart.equals("TCC")) return 73;
        if (tetraPart.equals("TCG")) return 74;
        if (tetraPart.equals("TCT")) return 75;
        if (tetraPart.equals("TGA")) return 76;
        if (tetraPart.equals("TGC")) return 77;
        if (tetraPart.equals("TGG")) return 78;
        if (tetraPart.equals("TGT")) return 79;
        if (tetraPart.equals("TTA")) return 80;
        if (tetraPart.equals("TTC")) return 81;
        if (tetraPart.equals("TTG")) return 82;
        if (tetraPart.equals("TTT")) return 83;
        return -1;
    }
}