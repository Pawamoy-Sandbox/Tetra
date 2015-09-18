import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CodeSet {

    private static ArrayList<String> S256;
    private static List<String> S12 = new ArrayList<>();

    // Singleton Stuff
    private static class SingletonHolder
    {
        public static final CodeSet INSTANCE = new CodeSet();
    }

    public static CodeSet getInstance()
    {
        return SingletonHolder.INSTANCE;
    }
    // End Singleton Stuff

    private CodeSet()
    {
        readTetra256();
    }

    private static void readTetra256()
    {
        try (BufferedReader br = new BufferedReader(new FileReader("tetra256.txt")))
        {
            String line;

            while ((line = br.readLine()) != null) {
                S256.add(line);

                if (isAutoCompl(line))
                    S12.add(line);
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

    public static boolean isAutoCompl(String tetra)
    {
        return tetra.equals(compl(tetra));
    }
}