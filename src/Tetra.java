import org.paukov.combinatorics.Factory;
import org.paukov.combinatorics.Generator;
import org.paukov.combinatorics.ICombinatoricsVector;


public class Tetra {

    public static void main (String[] args)
    {
        // Create the initial vector of 3 elements (apple, orange, cherry)
        ICombinatoricsVector<String> originalVector = Factory.createVector(new String[]{"A", "C", "G", "T"});

        // Create the permutation generator by calling the appropriate method in the Factory class
        Generator<String> gen = Factory.createPermutationWithRepetitionGenerator(originalVector, 4);

        // Print the result
        for (ICombinatoricsVector<String> perm : gen)
            System.out.println(perm);
    }
}