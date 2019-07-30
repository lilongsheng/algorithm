package jdk.stream.practice;

import jdk.stream.Status;
import jdk.stream.day2.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * 一、 Stream 的操作步骤
 * <p>
 * 1. 创建 Stream
 * <p>
 * 2. 中间操作
 * <p>
 * 3. 终止操作
 * allMatch——检查是否匹配所有元素
 * anyMatch——检查是否至少匹配一个元素
 * noneMatch——检查是否没有匹配的元素
 * findFirst——返回第一个元素
 * findAny——返回当前流中的任意元素
 * count——返回流中元素的总个数
 * max——返回流中最大值
 * min——返回流中最小值
 */
@Slf4j
public class MatchAllAnyMaxMinTest8 {

    List<Employee> emps = Arrays.asList(
            new Employee(102, "李四", 59, 6666.66, Status.BUSY),
            new Employee(101, "张三", 18, 9999.99, Status.FREE),
            new Employee(103, "王五", 28, 3333.33, Status.VOCATION),
            new Employee(104, "赵六", 8, 7777.77, Status.BUSY),
            new Employee(104, "赵六", 8, 7777.77, Status.FREE),
            new Employee(104, "赵六", 8, 7777.77, Status.FREE),
            new Employee(105, "田七", 38, 5555.55, Status.BUSY)
    );

    /**
     * 终止操作
     * allMatch——检查是否匹配所有元素
     * anyMatch——检查是否至少匹配一个元素
     * noneMatch——检查是否没有匹配的元素
     */
    public void test1() {
        boolean bl = emps.stream()
                .allMatch((e) -> e.getStatus().equals(Status.BUSY));
        Assert.isTrue(!bl, "b1 is true");

        boolean bl1 = emps.stream()
                .anyMatch((e) -> e.getStatus().equals(Status.BUSY));
        Assert.isTrue(bl1, "bl1 is false");

        boolean bl2 = emps.stream()
                .noneMatch((e) -> e.getStatus().equals(Status.BUSY));
        Assert.isTrue(!bl2, "bl2 is true");
    }


    /**
     * findFirst——返回第一个元素
     * findAny——返回当前流中的任意元素
     */
    public void test2() {
        // 按工资排序 并返回第一个员工
        Optional<Employee> op = emps.stream()
                .sorted((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()))
                .findFirst();

        log.info("{}", op.get());

        log.info("--------------------------------");

        // 返回当前流中任意元素
        Optional<Employee> op2 = emps.parallelStream()
                .filter((e) -> e.getStatus().equals(Status.FREE))
                .findAny();

        log.info("{}", op2.get());
    }


    /**
     * count——返回流中元素的总个数
     * max——返回流中最大值
     * min——返回流中最小值
     */
    public void test3() {
        long count = emps.stream()
                .filter((e) -> e.getStatus().equals(Status.FREE))
                .count();

        log.info("{}", count);

        // 返回最大值
        Optional<Double> op = emps.stream()
                .map(Employee::getSalary)
                .max(Double::compare);

        log.info("{}", op.get());

        Optional<Employee> op2 = emps.stream()
                .min((e1, e2) -> Double.compare(e2.getSalary(), e1.getSalary()));

        log.info("{}", op2.get());
    }

    /**
     *注意：流进行了终止操作后，不能再次使用
     */
    public void test4() {
        Stream<Employee> stream = emps.stream()
                .filter((e) -> e.getStatus().equals(Status.FREE));

        long count = stream.count();

        stream.map(Employee::getSalary)
                .max(Double::compare);
    }

    public static void main(String[] args) {
        MatchAllAnyMaxMinTest8 api2 = new MatchAllAnyMaxMinTest8();
//		api2.test1();
//        api2.test2();
//        api2.test3();
            api2.test4();
    }
}
