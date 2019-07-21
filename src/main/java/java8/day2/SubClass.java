package main.java.java8.day2;

public class SubClass /*extends MyClass*/ implements MyFun, MyInterface{

	@Override
	public String getName() {
		return MyInterface.super.getName();
	}

}
