package main.java.architecture;


public abstract class AbstractPunishHander implements IPunishHander{

    @Override
    public String hander(String name){
        System.out.println("AbstractPunishHander");
        return "AbstractPunishHander";
    }

}
