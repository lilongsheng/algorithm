package java8.practice;


import java8.FilterEmployeeForAge;
import java8.FilterEmployeeForSalary;
import java8.MyPredicate;
import java8.day2.Employee;

import java.util.*;


/**
 * 策略模式 提供一个统一认证接口 ，具体认证策略在具体实现类，SPRING 框架大量使用
 *
 * 逐渐优化 匿名内部类、lambda 表达式、stream api
 */
public class InnerStrategyLambdaTest1 {
	

	
	//现在的 Lambda 表达式
	public void test2(){
		Comparator<String> com = (x, y) -> Integer.compare(x.length(), y.length());
		TreeSet<String> ts = new TreeSet<>(com);
	}
	
	List<Employee> emps = Arrays.asList(
			new Employee(101, "张三", 18, 9999.99),
			new Employee(102, "李四", 59, 6666.66),
			new Employee(103, "王五", 28, 3333.33),
			new Employee(104, "赵六", 8, 7777.77),
			new Employee(105, "田七", 38, 5555.55)
	);

	//需求：获取公司中年龄小于 35 的员工信息
	public List<Employee> filterEmployeeAge(List<Employee> emps){
		List<Employee> list = new ArrayList<>();
		
		for (Employee emp : emps) {
			if(emp.getAge() <= 35){
				list.add(emp);
			}
		}
		
		return list;
	}
	
	public void test3(){
		List<Employee> list = filterEmployeeAge(emps);
		
		for (Employee employee : list) {
			System.out.println(employee);
		}
	}
	
	//需求：获取公司中工资大于 5000 的员工信息
	public List<Employee> filterEmployeeSalary(List<Employee> emps){
		List<Employee> list = new ArrayList<>();
		
		for (Employee emp : emps) {
			if(emp.getSalary() >= 5000){
				list.add(emp);
			}
		}
		
		return list;
	}
	
	//优化方式一：策略设计模式
	public List<Employee> filterEmployee(List<Employee> emps, MyPredicate<Employee> mp){
		List<Employee> list = new ArrayList<>();
		
		for (Employee employee : emps) {
			if(mp.test(employee)){
				list.add(employee);
			}
		}
		
		return list;
	}

    /**
     * 两种策略实现
	 */
	public void test4(){
		List<Employee> list = filterEmployee(emps, new FilterEmployeeForAge());
		for (Employee employee : list) {
			System.out.println(employee);
		}
		
		System.out.println("------------------------------------------");
		
		List<Employee> list2 = filterEmployee(emps, new FilterEmployeeForSalary());
		for (Employee employee : list2) {
			System.out.println(employee);
		}
	}

    /**
	 * 优化方式二：匿名内部类 来实现 策略接口
	 */

	public void test5(){
		List<Employee> list = filterEmployee(emps, new MyPredicate<Employee>() {
			@Override
			public boolean test(Employee t) {
				return t.getId() <= 103;
			}
		});
		
		for (Employee employee : list) {
			System.out.println(employee);
		}
	}
	
	/**
     * 优化方式三：Lambda 表达式 实现策略接口
	 */
	public void test6(){
		List<Employee> list = filterEmployee(emps, (t) -> t.getAge() <= 35);
		list.forEach(System.out::println);
		
		System.out.println("------------------------------------------");
		
		List<Employee> list2 = filterEmployee(emps, (e) -> e.getSalary() >= 5000);
		list2.forEach(System.out::println);
	}
	
	//优化方式四：Stream API
	public void test7(){
		emps.stream()
			.filter((e) -> e.getAge() <= 35)
			.forEach(System.out::println);
		
		System.out.println("----------------------------------------------");
		
		emps.stream()
			.map(Employee::getName)
			.limit(3)
			.sorted()
			.forEach(System.out::println);
	}

	public static void main(String[] args) {
		InnerStrategyLambdaTest1 test1 = new InnerStrategyLambdaTest1();
//		test1.filterEmployee()
	}
}
