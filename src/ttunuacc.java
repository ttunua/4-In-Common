import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.*;
import static java.lang.System.out;

public class ttunuacc {
    private static final int consecInCommon = 4;
    private static long startTime, endTime;
    private static final String file_name = "sowpods.txt";

    private static String[] subStringsFrom(
            String toPartition,
            int consecInCommon)
    {
        int numSubStrings = toPartition.length() - (consecInCommon - 1);
        return IntStream.range(0,numSubStrings)
                        .boxed()
                        .map(index -> toPartition.substring(index, index+consecInCommon))
                        .toArray(String[]::new);
    }

    private static String[] fourInCommon(
            String source,
            String[] dictionary)
    {
        //String.contains() will brute force search with a pattern(subStringsFrom)
        //but is still very quick for short strings and short patterns
        //so things like tries would become potentially more useful later
        startTime = System.currentTimeMillis();
        //make the substrings to look for
        String[] subStrings = subStringsFrom(source, consecInCommon);
        assert dictionary != null;
        //find any words containing occurrences of the substring(s) and put them into a new array to return
        return Stream.of(dictionary)
                     .parallel()
                     .filter(word -> Stream.of(subStrings).anyMatch(word::contains))
                     .toArray(String[]::new);
    }

    private static String inputWord()
    {
        print(
                "enter a word. You will see how many words from\n" +
                "the dictionary have " + consecInCommon + " or more consecutive letters\n" +
                "in common with the word you enter.\n" +
                "(Words with length less than " + consecInCommon + " will not be read)\n" +
                "Source word = "
        );
        Scanner input = new Scanner(System.in);
        String sourceWord = input.next();
        while (sourceWord.length() < consecInCommon){
            print("need a string greater than " + (consecInCommon-1) + " in length");
            sourceWord = input.next();
        }
        return sourceWord.toUpperCase();
    }

    private static String[] stringsFrom(File toRead)
    {
        String[] strings;
        try {
            strings = Files.lines(toRead.toPath())
                           .parallel()
                           .filter(word -> word.length() >= consecInCommon)
                           .toArray(String[]::new);
        }catch (IOException e){
            e.printStackTrace();
            return new String[0];   //return empty array, there are worse issues at this point
        }
        print(strings.length + " lines read");
        return strings;
    }

    private static void print(String toPrint)
    {
        out.println(toPrint);
    }

    public static void main(String[]args) throws IOException
    {
        //assumes the text file is in the same directory
        String filePath = new File(".").getCanonicalFile().getAbsolutePath();
        File dictFile = new File(filePath + "/" + file_name);
        //also ignores words < numConsec in length (4 currently)
        String[] common = fourInCommon(inputWord(), stringsFrom(dictFile));
        endTime = System.currentTimeMillis();
        Stream.of(common).forEach(out::println);
        print(common.length + " matches");
        print("time taken: " + (endTime-startTime) + " milliseconds");
    }
}