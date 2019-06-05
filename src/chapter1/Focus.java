package chapter1;

import java.util.Comparator;

public class Focus extends Cars implements Comparable<Focus>{

    int bulbs = 6;
    long regNumber;


    Focus(){

    }

    Focus(String colour,long regNumber){
        super(colour); //if this is not here then it by default calls the non arg superclass constructor
        color = colour;
        this.regNumber = regNumber;

    }

    static{
        System.out.println("static in sub class");
    }

    public String carColour(){
        return "blue";
    }
    public String carColour1(){
        return "colour1";
    }


    public void wheels(){
        System.out.println("wheels : " +4);
    }

    static void getGears(){ // hides the static method in the super class, does not override
        System.out.println("gears : " +10);
    }

    public int compareTo(Focus focus){

        return Math.toIntExact(this.regNumber - focus.regNumber);

    }

    //one way to implement comparator

    static Comparator<Focus> comparatorColour = new Comparator<Focus>(){
        @Override
        public int compare(Focus focus1, Focus focus2){
            return focus1.color.compareTo(focus2.color);

        }
    };

    @Override
    public String toString() {
        return this.color +""+ this.regNumber;
    }

    @Override
    public boolean equals(Object o){
        return true;
    }
}
