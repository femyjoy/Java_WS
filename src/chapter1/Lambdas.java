package chapter1;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Lambdas {


    public static void main(String[] args) {
        MyFunctionalInterface mYFI = (a, b) -> (a + b);
        System.out.println(mYFI.doMaths(2, 3));

/*

        MyFunctionalInterface sample = new MyFunctionalInterface() {
            @Override
            public int doMaths(int i, int j) {
                return i + j;
            }
        };

        System.out.println(sample.doMaths(3, 4));
*/


    }
}




interface MyFunctionalInterface{

    default void printThis (){
        System.out.println("Enter 2 numbers: ");
    }

    int doMaths(int i, int j);

    static void show(){
        BufferedInputStream bis = new BufferedInputStream(System.in);

    }



        }
