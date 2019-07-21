package architecture;


public abstract class AbstractMappingHander implements IMappingHander{

    @Override
    public String hander(String name){
        System.out.println("AbstractMappingHander");
        return "AbstractMappingHander";
    }

}
