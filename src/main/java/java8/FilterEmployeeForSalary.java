package java8;

import java8.day2.Employee;

public class FilterEmployeeForSalary implements MyPredicate<Employee> {

	public boolean test(Employee t) {
		return t.getSalary() >= 5000;
	}

}
