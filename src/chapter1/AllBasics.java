package chapter1;

import java.util.*;

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
        System.out.println("Printing list sort by reg number ");
        Collections.sort(list1); // uses the comparable implementation
        list1.forEach(l-> System.out.println(l)); //implements Consumer
        System.out.println("Simply printing list: ");
        System.out.println(list1);
        Collections.sort(list1,new ComparatorregNumber()); // uses the comparator implementation
        System.out.println(list1);
        Collections.sort(list1,Focus.comparatorColour); // uses the comparator implementation
        System.out.println(list1);

        //Alternate ways to loop through list
        Iterator i = list1.iterator();
       while(i.hasNext()){
           //blahb lah
       }

       for(Focus f : list1){
           //blah blah
       }

       Map<String, Focus> focusMap = new HashMap<String,Focus>(); //implements BiConsumer
       focusMap.put("white",focus1);
       focusMap.put("black",focus2);

       //Java 8 way to iterate
        System.out.println("Printing map values using forEach: ");
       focusMap.forEach((k,v)-> {System.out.println( k + v.color);});

       //using EntrySet
        System.out.println("Printing map values using EntrySet:");
        for (Map.Entry<String, Focus> entry : focusMap.entrySet()){
            System.out.println(entry.getKey()+entry.getValue().color);

        }

        Map<Focus, Focus> focusMap2 = new HashMap<Focus,Focus>(); //implements BiConsumer
        focusMap2.put(focus2,focus2);
        focusMap2.put(focus2,focus2);

        //Anonymous inner class for abstract class Cars

        methodTakesCarsImpl(new Cars(){ //anonymous implemention of the abstract class, all teh abstract methods have to be implemented
            public String carColour(){
                return "anonymous";
            }
            public String carColour1(){
                return "blue";
            }

        });


//Normally collections can take heterogenous object types except TreeSet and TreeMap
        HashMap heteroMap = new HashMap();
        heteroMap.put(1,"dad");
        heteroMap.put("hello",2);
        heteroMap.put("hai","world");
        Set values = (Set)heteroMap.values(); //gives a collection, can be cast to List or Set
        List keyList = (List)heteroMap.keySet(); //casting set to list
        List entryList = (List)heteroMap.entrySet(); //casting set to list

        List heteroAL= new ArrayList();
        heteroAL.add(1);
        heteroAL.add('a');

        Collections.emptyMap();




    }


    private static void methodTakesCarsImpl(Cars cars){
        System.out.println("Prinitng from private method"+cars.carColour());

    }




}
