package jdk.stream.practice;


import jdk.stream.Status;
import jdk.stream.day2.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 归约数据reduce、最大值、最小值、平均值等、
 * 分组、多级分组
 * 分区
 * 逗号拼接
 */
public class ReduceCollecGroupTest7 {
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	List<Employee> emps = Arrays.asList(
			new Employee(102, "李四", 79, 6666.66, Status.BUSY),
			new Employee(101, "张三", 18, 9999.99, Status.FREE),
			new Employee(103, "王五", 28, 3333.33, Status.VOCATION),
			new Employee(104, "赵六", 8, 7777.77, Status.BUSY),
			new Employee(104, "赵六", 8, 7777.77, Status.FREE),
			new Employee(104, "赵六", 8, 7777.77, Status.FREE),
			new Employee(105, "田七", 38, 5555.55, Status.BUSY)
	);
	
	//3. 终止操作
	/**
	*	归约
	*	reduce(T identity, BinaryOperator) / reduce(BinaryOperator) ——可以将流中元素反复结合起来，得到一个值。
	 *
	 * 求和
	 * 求员工薪资之和
	 * 传入一个求和的方式
	 */
	public void test1(){
		List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);
		
		Integer sum = list.stream()
			.reduce(0, (x, y) -> x + y);
		
		logger.info("{}",sum);
		
		logger.info("----------------------------------------");
		
		Optional<Double> op = emps.stream()
			.map(Employee::getSalary)
			.reduce(Double::sum);
		
		logger.info("{}",op.get());
	}
	
	/**
	 * 需求：搜索名字中 “六” 出现的次数
	 * map 是作用于元素的函数，可以返回新值
	 */
	public void test2(){
		Optional<Integer> sum = emps.stream()
			.map(Employee::getName)
			.flatMap(MapFlatMapSortTest6::filterCharacter)
			.map((ch) -> {
				if(ch.equals('六'))
					return 1;
				else 
					return 0;
			}).reduce(Integer::sum);
		
		logger.info("{}",sum.get());
	}
	
	/**
	 *collect——将流转换为其他形式。接收一个 Collector接口的实现，用于给Stream中元素做汇总的方法
	 * collect 像是stream 转为其他数据格式的出口
	 * stream 像是其它数据格式转为流的入口
	 */
	public void test3(){
		List<String> list = emps.stream()
			.map(Employee::getName)
			.collect(Collectors.toList());
		
		list.forEach(System.out::println);
		
		logger.info("----------------------------------");
		
		Set<String> set = emps.stream()
			.map(Employee::getName)
			.collect(Collectors.toSet());
		
		set.forEach(System.out::println);

		logger.info("----------------------------------");
		
		HashSet<String> hs = emps.stream()
			.map(Employee::getName)
			.collect(Collectors.toCollection(HashSet::new));
		
		hs.forEach(System.out::println);
	}


	/**
	 * Collectors
	 * 求薪资列的最大值maxBy、最小值minBy、求和summingDouble、平均值averagingDouble、计数counting()
	 */
	public void test4(){
		Optional<Double> max = emps.stream()
			.map(Employee::getSalary)
			.collect(Collectors.maxBy(Double::compare));
		
		logger.info("{}",max.get());
		
		Optional<Employee> op = emps.stream()
			.collect(Collectors.minBy((e1, e2) -> Double.compare(e1.getSalary(), e2.getSalary())));
		
		logger.info("{}",op.get());
		
		Double sum = emps.stream()
			.collect(Collectors.summingDouble(Employee::getSalary));
		
		logger.info("{}",sum);
		
		Double avg = emps.stream()
			.collect(Collectors.averagingDouble(Employee::getSalary));
		
		logger.info("{}",avg);
		
		Long count = emps.stream()
			.collect(Collectors.counting());
		
		logger.info("{}",count);
		
		logger.info("--------------------------------------------");
		
		DoubleSummaryStatistics dss = emps.stream()
			.collect(Collectors.summarizingDouble(Employee::getSalary));
		
		logger.info("{}",dss.getMax());
	}
	

	/**
	 * 分组
	 * 对员工按等级分组
	 */
	public void test5(){
		Map<Status, List<Employee>> map = emps.stream()
			.collect(Collectors.groupingBy(Employee::getStatus));
		
		logger.info("{}",map);
	}
	

	/**
	 * 多级分组 先按员工等级分组、再按年龄分组
	 * 多应用一次groupingBy即可
	 */
	public void test6(){
		Map<Status, Map<String, List<Employee>>> map = emps.stream()
			.collect(Collectors.groupingBy(Employee::getStatus, Collectors.groupingBy((e) -> {
				if(e.getAge() >= 60)
					return "老年";
				else if(e.getAge() >= 35)
					return "中年";
				else
					return "成年";
			})));
		
		logger.info("{}",map);
	}
	

	/**
	 * 分区
	 * 根据条件薪资大于500进行分区
	 * Lists.partitions 分为若干个相等的组
	 */
	public void test7(){
		Map<Boolean, List<Employee>> map = emps.stream()
			.collect(Collectors.partitioningBy((e) -> e.getSalary() >= 5000));
		
		logger.info("{}",map);
	}
	

	/**
	 * 将list里面员工性能取出来并且逗号拼接起来
	 */
	public void test8(){
		String str = emps.stream()
			.map(Employee::getName)
			.collect(Collectors.joining("," , "----", "----"));
		
		logger.info(str);
	}
	

	public void test9(){
		Optional<Double> sum = emps.stream()
			.map(Employee::getSalary)
			.collect(Collectors.reducing(Double::sum));
		
		logger.info("{}",sum.get());
	}

	public static void main(String[] args) {
		ReduceCollecGroupTest7 reduceColle = new ReduceCollecGroupTest7();
//		reduceColle.test1();
//		reduceColle.test1();
//		reduceColle.test3();
		reduceColle.test4();
	}
}
