package chapter1;

import java.util.Comparator;
//alternate way to implement comparator

public class ComparatorregNumber implements Comparator<Focus> {
    @Override
    public int compare(Focus o1, Focus o2) {
        return Math.toIntExact(o1.regNumber-o2.regNumber);
    }
}
