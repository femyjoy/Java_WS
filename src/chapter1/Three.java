package chapter1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Three {
    public static void main(String[] args) {

        solution("my.song.mp3 11b\n" +
                "greatSong.flac 1000b\n" +
                "not3.txt 5b\n" +
                "video.mp4 200b\n" +
                "game.exe 100b\n" +
                "mov!e.mkv 10000b");
    }

        public static String solution(String S) {

            Map<String,String> nameSize = new HashMap<String, String>();
//
            String[] sentences = S.toString().split("\\r?\\n+");
            for(String sentence : sentences){
               /* Pattern p = Pattern.compile("(.{30})\\s(.)");
                Matcher m = p.matcher(sentence);
                String file = m.group(1);
                String size = m.group(2);*/


               String [] fileAndSize = sentence.split("\\s+");

                nameSize.put(fileAndSize[0],fileAndSize[1]);


            }
            long numberofAudioFiles = nameSize.keySet().stream().filter(k -> k.contains(".mp3")||k.contains(".aac")||k.contains(".flac")).count();

        return "blah";
            // write your code in Java SE 8
        }



}
