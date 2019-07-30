package jdk.stream.practice;


import jdk.stream.day2.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.function.*;


/**
 *
 * 理解：对于已经有写好的方法我们可以直接拿过来用，不需要自己再去写lambda ，其实就是调用别人写好的方法，java8提供了一种新的方法 语法表示问题
 *  道理同普通方法实现没有啥区别
 *
 * 一、方法引用：若 Lambda 体中的功能，已经有方法提供了实现，可以使用方法引用
 * 			  （可以将方法引用理解为 Lambda 表达式的另外一种表现形式）
 * 
 * 1. 对象的引用 :: 实例方法名
 * 
 * 2. 类名 :: 静态方法名
 * 
 * 3. 类名 :: 实例方法名
 * 
 * 注意：
 * 	 ①方法引用所引用的方法的参数列表与返回值类型，需要与函数式接口中抽象方法的参数列表和返回值类型保持一致！
 * 	 ②若Lambda 的参数列表的第一个参数，是实例方法的调用者，第二个参数(或无参)是实例方法的参数时，格式： ClassName::MethodName
 * 
 * 二、构造器引用 :构造器的参数列表，需要与函数式接口中参数列表保持一致！
 * 
 * 1. 类名 :: new
 * 
 * 三、数组引用
 * 
 * 	类型[] :: new;
 * 
 * 
 */
public class LibraryMethodRefTest5 {
	private Logger log = LoggerFactory.getLogger(this.getClass());
	/**
	 * 数组引用 创建
	 */
	public void test8(){
		Function<Integer, String[]> fun = (args) -> new String[args];
		String[] strs = fun.apply(10);
		log.info("{}",strs.length);
		
		log.info("{}","--------------------------");
		
		Function<Integer, Employee[]> fun2 = Employee[] :: new;
		Employee[] emps = fun2.apply(20);
		log.info("{}",emps.length);
	}
	
	//构造器引用
	
	public void test7(){
		Function<String, Employee> fun = Employee::new;
		
		BiFunction<String, Integer, Employee> fun2 = Employee::new;
	}

	/**
	 * 利用构造函数创建对象
	 * 场景在哪里？
	 */
	public void test6(){
		Supplier<Employee> sup = () -> new Employee();
		log.info("{}",sup.get());
		
		log.info("{}","------------------------------------");
		
		Supplier<Employee> sup2 = Employee::new;
		log.info("{}",sup2.get());

	}

	/**
	 * 类名 :: 实例方法名
	 */
	public void test5(){
		BiPredicate<String, String> bp = (x, y) -> x.equals(y);
		log.info("{}",bp.test("abcde", "abcde"));
		
		log.info("{}","-----------------------------------------");
		
		BiPredicate<String, String> bp2 = String::equals;
		log.info("{}",bp2.test("abc", "abc"));
		
		log.info("{}","-----------------------------------------");
		
		
		Function<Employee, String> fun = (e) -> e.show();
		log.info("{}",fun.apply(new Employee()));
		
		log.info("{}","-----------------------------------------");
		
		Function<Employee, String> fun2 = Employee::show;
		log.info("{}",fun2.apply(new Employee()));
		
	}

	/**
	 *
	 *类名 :: 静态方法名
	 */
	public void test4(){
		Comparator<Integer> com = (x, y) -> Integer.compare(x, y);
		
		log.info("{}","-------------------------------------");
		
		Comparator<Integer> com2 = Integer::compare;
	}

	/**
	 * 类名 :: 静态方法名
	 */
	public void test3(){
		BiFunction<Double, Double, Double> fun = (x, y) -> Math.max(x, y);
		log.info("{}",fun.apply(1.5, 22.2));
		
		log.info("{}","--------------------------------------------------");
		
		BiFunction<Double, Double, Double> fun2 = Math::max;
		log.info("{}",fun2.apply(1.2, 1.5));
	}

	/**
	 *
	 *对象的引用 :: 实例方法名
	 */
	public void test2(){
		Employee emp = new Employee(101, "张三", 18, 9999.99);
		
		Supplier<Integer> sup = () -> emp.getAge();
		log.info("{}",sup.get());
		
		log.info("{}","----------------------------------");
		
		Supplier<Integer> sup2 = emp::getAge;
		log.info("{}",sup2.get());
	}


	/**
	 * 对象::方法名
	 * 类名::方法名
	 */
	public void test1(){
		PrintStream ps = System.out;
		Consumer<String> con = (str) -> ps.println(str);
		con.accept("Hello World！");
		
		
		Consumer<String> con2 = ps::println;
		con2.accept("Hello Java8！");
		
		Consumer<String> con3 = System.out::println;
		con3.accept("hell java888");
	}

	public static void main(String[] args) {
		LibraryMethodRefTest5 methodRef = new LibraryMethodRefTest5();
//		methodRef.test1();
//		methodRef.test2();
		methodRef.test6();
	}
}
