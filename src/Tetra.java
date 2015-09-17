import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;


public class Tetra {

    public static void main (String[] args) {
        ICombinatoricsVector<String> allTetra = Factory.createVector(new String[]{"A", "C", "G", "T"});
        ICombinatoricsVector<String> allTetraCompl = Factory.createVector(new String[]{"T", "G", "C", "A"});

        // Create the permutation generator by calling the appropriate method in the Factory class
        Generator<String> gen = Factory.createPermutationWithRepetitionGenerator(allTetra, 4);

        // Print the result
        for (ICombinatoricsVector<String> perm : gen)
            System.out.println(perm);

        String tetra1 = "ACGT";
        String tetra2 = "CGCT";

        System.out.println("tetra: " + tetra1 + "\tcompl:" + Compl(tetra1) + "\tAutoCompl: " + IsAutoCompl(tetra1));
        System.out.println("tetra: " + tetra2 + "\tcompl:" + Compl(tetra2) + "\tAutoCompl: " + IsAutoCompl(tetra2));

    }

    public static String Compl(String tetra)
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

    public static boolean IsAutoCompl(String tetra)
    {
        return tetra.equals(Compl(tetra));
    }
}