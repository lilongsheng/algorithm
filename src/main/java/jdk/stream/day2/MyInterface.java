package jdk.stream.day2;

public interface MyInterface {
	
	default String getName(){
		return "呵呵呵";
	}
	
	static void show(){
		System.out.println("接口中的静态方法");
	}

}
