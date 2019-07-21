package main.java.java8;

import main.java.java8.day2.Employee;

public class FilterEmployeeForAge implements MyPredicate<Employee>{

//	@Override
	public boolean test(Employee t) {
		return t.getAge() <= 35;
	}

}
