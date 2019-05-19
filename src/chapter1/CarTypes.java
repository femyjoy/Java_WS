package chapter1;

import com.sun.xml.internal.ws.config.management.policy.ManagementPolicyValidator;

public enum CarTypes {

    //all enums extends Enum class and so inherits static valueOf() method by default. pass in string and get the enum
    //Type of Enum constants is the type of which they are created.
    HATCHBACK("Hatch"),  MPV ("Mpv"), SUV ("suv"); //the values is passed to the constructor

    private String carType;

    CarTypes(String type){
        this.carType = type;

    }

    String getCarType(){
        return carType;
    }




}
