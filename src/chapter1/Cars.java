package chapter1;

public abstract class Cars {

    String color;
    int bulbs = 4;

    static{
        System.out.println("static block");
    }

     Cars(){
        color = "Red";
        System.out.println("in super class constructor");


    }

    Cars(String color){
        this.color = color;
    }

    public String typeOfCar(CarTypes type){

        switch(type){
            case HATCHBACK : return CarTypes.HATCHBACK.getCarType();
            case MPV : return CarTypes.MPV.getCarType();
            case SUV : return CarTypes.SUV.getCarType();
            default : return "No matching car type found";
        }

    }

    public abstract String carColour();

    public abstract String carColour1();

    public  void wheels(){
        System.out.println("wheels : " +4);
    }

    static void getGears(){
        System.out.println("gears : " +5);

    }

    //var arg method, param is considered as an array
    void seats(int...seats){
        for (int seat : seats)
        System.out.println("Seats : "+seat);
    }
}
