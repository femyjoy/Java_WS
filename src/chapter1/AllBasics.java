package chapter1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class AllBasics {
    public static void main(String[] args) {
        if(args.length>0){
         for(String arg:args)
             System.out.println(arg);



        }
        GoodnessOfStrings good = new GoodnessOfStrings("Yay");
        System.out.println(good.iAmFinal); //final instance variable in the class has not been effected, the one in the constructor is a new variable
        // Cars cars = new Cars(); // abstract cannot be instantiated
        Cars focus = new Focus("Black",34567);
        System.out.println("Focus is a " +focus.typeOfCar(CarTypes.HATCHBACK));
        focus.getGears(); //calls superclass method even though its hidden by subclass
        focus.wheels(); //calls subclass method as its overrides
        System.out.println(focus.bulbs); //calls superclass variable as subclass one only hides
        focus.seats(new int [] {2,3}); // calling method with car arg

        List<Focus> list1 = new ArrayList<Focus>();
        Focus focus1 = new Focus("White",12345);
        Focus focus2 = new Focus("Black",2345);
        list1.add(focus1);
        list1.add(focus2);
        Collections.sort(list1);
        list1.forEach(l-> System.out.println(l));
        System.out.println(list1); // uses the comparable implementation
        Collections.sort(list1,new ComparatorregNumber());
        System.out.println(list1); // uses the comparator implementation
        // Collections.sort(list1,comparatorColour);
        System.out.println(list1); // uses the comparator implementation






    }





}
