import org.apache.commons.math3.util.CombinatoricsUtils;

import java.util.BitSet;
import java.util.Iterator;

public class Combiner implements Iterable<BitSet>
{
    private final int length;

    public Combiner(int length)
    {
        this.length = length;
    }

    private class BitSetCombinerIterator implements Iterator<BitSet>
    {
        private int _current_index = 0;
        private int _count = 0;
        private final long _total;
        private int _last_bit;

        public BitSetCombinerIterator(long total)
        {
            _total = total;
        }

        @Override
        public boolean hasNext()
        {
            return (_count < _total);
        }

        @Override
        public BitSet next()
        {
            // pour l=1
            int next = CodeSet.BS12.nextSetBit(_current_index);
            _current_index = next + 1;
            _count++;

            BitSet result = new BitSet();
            result.set(next);
            return result;

            // pour l=2

            // 2 dans BS12
        }
    }

    @Override
    public Iterator<BitSet> iterator()
    {
        long totalCombinations = 0;

        int BS12_choices;
        int BS114_choices = length / 2;

        if (length <= 12)
            BS12_choices = length;
        else if (length == 13)
            BS12_choices = 11;
        else
            BS12_choices = 12;

        for (int l = BS12_choices; l > 0; l -= 2)
            totalCombinations += CombinatoricsUtils.binomialCoefficient(12, l);

        for (int l = BS114_choices; l > 0; l--)
            totalCombinations += CombinatoricsUtils.binomialCoefficient(114, l);

        return new BitSetCombinerIterator(totalCombinations);
    }

//    public void generate_suffix(BitSet b, int choicesLeft, int pos) {
//        int i;
//        double lim = Math.pow(10, choicesLeft);
//
//        if (choicesLeft != 1)
//        {
//            for (i=0; i<b.cardinality(); i++)
//            {
//                suffix[pos] = CodeSet.stringToByte()[i];
//                generate_suffix(choicesLeft-1, pos+1);
//                suffix[pos] = '\0';
//            }
//        }
//        else
//        {
//            for (i=0; i<alp_length; i++)
//            {
//                suffix[pos] = alphabet[i];
//                printf("%s%s%s", base?base:"", suffix, separator);
//            }
//        }
//    }

//    public BitSet recursiveNext(BitSet b, int choicesLeft, int lastBit)
//    {
//        if (choicesLeft > 0)
//        {
//            return b;
//        }
//        else
//        {
//
//        }
//    }
}