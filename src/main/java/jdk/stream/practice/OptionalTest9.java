package jdk.stream.practice;

import jdk.stream.day2.Employee;
import jdk.stream.day2.Godness;
import jdk.stream.day2.Man;
import jdk.stream.day2.NewMan;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


/**
 *
 * 一、Optional 容器类：用于尽量避免空指针异常
 * 	Optional.of(T t) : 创建一个 Optional 实例
 * 	Optional.empty() : 创建一个空的 Optional 实例
 * 	Optional.ofNullable(T t):若 t 不为 null,创建 Optional 实例,否则创建空实例
 * 	isPresent() : 判断是否包含值
 * 	orElse(T t) :  如果调用对象包含值，返回该值，否则返回t
 * 	orElseGet(Supplier s) :如果调用对象包含值，返回该值，否则返回 s 获取的值
 * 	map(Function f): 如果有值对其处理，并返回处理后的Optional，否则返回 Optional.empty()
 * 	flatMap(Function mapper):与 map 类似，要求返回值必须是Optional
 */

@Slf4j
public class OptionalTest9 {
	

	public void test4(){
		Optional<Employee> op = Optional.of(new Employee(101, "张三", 18, 9999.99));
		
		Optional<String> op2 = op.map(Employee::getName);
		log.info("{}",op2.get());
		
		Optional<String> op3 = op.flatMap((e) -> Optional.of(e.getName()));
		log.info("{}",op3.get());
	}
	

	public void test3(){
//		Optional<Employee> op = Optional.ofNullable(new Employee());
		Optional<Employee> op = Optional.ofNullable(null);
		
		if(op.isPresent()){
			log.info("{}",op.get());
		}
		
		Employee emp = op.orElse(new Employee("张三"));
		log.info("{}",emp);
		
//		Employee emp2 = op.orElseGet(() -> new Employee());
//		log.info("{}",emp2);
	}
	

	public void test2(){
//		Optional<Employee> op = Optional.ofNullable(null);
//		log.info("{}",op.get());

		Optional<Employee> op1 = Optional.empty();
		log.info("{}",op1.get());
	}


	public void test1(){
		Optional<Employee> op = Optional.of(new Employee());
		Employee emp = op.get();
		log.info("{}",emp);
	}
	

	public void test5(){
		Man man = new Man();
		
		String name = getGodnessName(man);
		log.info("{}",name);
	}
	
	//需求：获取一个男人心中女神的名字
	public String getGodnessName(Man man){
		if(man != null){
			Godness g = man.getGod();
			
			if(g != null){
				return g.getName();
			}
		}
		
		return "苍老师";
	}
	
	//运用 Optional 的实体类

	public void test6(){
		Optional<Godness> godness = Optional.ofNullable(new Godness("林志玲"));
		
		Optional<NewMan> op = Optional.ofNullable(new NewMan(godness));
		String name = getGodnessName2(op);
		log.info("{}",name);
	}
	
	public String getGodnessName2(Optional<NewMan> man){
		return man.orElse(new NewMan())
				  .getGodness()
				  .orElse(new Godness("苍老师"))
				  .getName();
	}

	public static void main(String[] args) {
		OptionalTest9 op = new OptionalTest9();
//		log.info("{}",op.getGodnessName(new Man()));
//		op.test1();


//		op.test2();

//		Optional opt1 = Optional.ofNullable(null);
//		log.info("{}",opt1.orElse(3));

		op.test3();
	}
}
