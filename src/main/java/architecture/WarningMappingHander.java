package architecture;



public class WarningMappingHander extends AbstractMappingHander {

    @Override
    public String hander(String name){
        System.out.println("WarningMappingHander");
        return "WarningMappingHander";
    }

}
