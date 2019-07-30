package jdk.stream;

import jdk.stream.day2.Employee;

public class FilterEmployeeForAge implements MyPredicate<Employee> {

//	@Override
	public boolean test(Employee t) {
		return t.getAge() <= 35;
	}

}
