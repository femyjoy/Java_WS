package chapter1;

public class GoodnessOfStrings {

    final String iAmFinal = "how dare";

    GoodnessOfStrings(String changeYou){
        String iAmFinal = changeYou; // if the String decalartion is removed, compiler complains that variable is final. Now this ia new string in memory pool
    }


    public static void main(String[] args) {
        String s1 = "Hello" ;
        String s2 = "Hello" ; //also created in memo pool as s1 and points to same as  above
        System.out.println(s1==s2); // true as when strings are created in memo pool, reference points to object if it already exist
        String s3 = new String ("Hello") ;//created in heap
        String s4 = new String ("Hello"); // created in heap, new instance
        System.out.println("== compares references : "+s3==s4); // false as 2 different instances
        System.out.println("Equals: " +s3.equals(s4)); // true as comparing meaningfulness
        String s5 = s3.intern();
        System.out.println(s5==s1); // true  as s3.intern now points to string in memo pool
        System.out.println(s5==s3); // false  as s3 still points to heap

    }

}
