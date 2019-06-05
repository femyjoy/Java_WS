package chapter1;


import com.sun.xml.internal.ws.util.StringUtils;

import java.util.Set;
import java.util.TreeSet;

public class Two {
    public static void main(String[] args) {
        int lenght = solution(". ? !");
        System.out.println(lenght);
    }


    // class Solution {
    public static int solution(String S) {

        Set<Integer> sizeOfSentence = new TreeSet<Integer>(); //using treeset as its sorted


        if (S == null || S.isEmpty() || S.trim().isEmpty()) {
            return 0;
        } // covers null and "" and " "

        if (S.matches(". .")) {
            return 0;
        }
        //covers ". ."

        String[] sentences = S.toString().split("\\.|\\?|!");
        if (sentences.length == 0) {  //to cover ".."
            return 0;
        }
        for (String sentence : sentences) {
            String[] words = sentence.trim().split("\\s+"); // get all words in each sentence split by space

            sizeOfSentence.add(new Integer(words.length));
        }
        return ((TreeSet<Integer>) sizeOfSentence).last().intValue();


    }


}
