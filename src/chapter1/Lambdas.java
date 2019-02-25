package chapter1;


import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Lambdas {


    public static void main(String[] args) {
        MyFunctionalInterface mYFI = (a, b) -> (a + b);
        System.out.println(mYFI.doMaths(2, 3));




    }
}


//methods by default in Interface are abstract, default methods have to be explicitly declared.

interface MyFunctionalInterface{

    default void printThis (){
        System.out.println("Enter 2 numbers: ");
    }

    int doMaths(int i, int j);

    static void show(){
        BufferedInputStream bis = new BufferedInputStream(System.in);

    }



        }
