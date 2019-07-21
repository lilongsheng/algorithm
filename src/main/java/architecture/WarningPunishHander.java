package main.java.architecture;



public class WarningPunishHander extends AbstractPunishHander {

    @Override
    public String hander(String name){
        System.out.println("WarningPunishHander");
        return "WarningPunishHander";
    }

}
