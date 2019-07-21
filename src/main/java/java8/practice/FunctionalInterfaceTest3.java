package main.java.java8.practice;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 *
 * 理解：这些接口只是定义好了 接口含义， 具体实现需要根据我们自己需要提供，参数列表和 返回值需要和我们的需要相符
 * Java8 内置的四大核心函数式接口
 *
 *
 * @link Consumer<T> : 消费型接口
 * void accept(T t); 一个入参 没有返回值
 *
 *
 *
 * Supplier<T> : 供给型接口
 * T get(); 没有入参 有一个返回值
 *
 *
 * Function<T, R> : 函数型接口
 * R apply(T t);一个入参  一个返回值
 *
 * Predicate<T> : 断言型接口
 * boolean test(T t);  一个入参 boolean 返回值
 *
 *
 * 上面四个是基本函数，只是对应了四种基本使用场景，28原则，更具体的场景使用有更详细的参数提供
 *
 *
 */
public class FunctionalInterfaceTest3 {

    //Predicate<T> 断言型接口：

    public void test4() {
        List<String> list = Arrays.asList("Hello", "atguigu", "Lambda", "www", "ok");
        List<String> strList = filterStr(list, (s) -> s.length() > 3);

        for (String str : strList) {
            System.out.println(str);
        }
    }

    //需求：将满足条件的字符串，放入集合中
    public List<String> filterStr(List<String> list, Predicate<String> pre) {
        List<String> strList = new ArrayList<>();

        for (String str : list) {
            if (pre.test(str)) {
                strList.add(str);
            }
        }

        return strList;
    }

    //Function<T, R> 函数型接口：
    public void test3() {
        String newStr = strHandler("\t\t\t 我大尚硅谷威武   ", (str) -> str.trim());
        System.out.println(newStr);

        String subStr = strHandler("我大尚硅谷威武", (str) -> str.substring(2, 5));
        System.out.println(subStr);
    }

    //需求：用于处理字符串
    public String strHandler(String str, Function<String, String> fun) {
        return fun.apply(str);
    }

    //Supplier<T> 供给型接口 :
    public void test2() {
        List<Integer> numList = getNumList(10, () -> (int) (Math.random() * 100));

        for (Integer num : numList) {
            System.out.println(num);
        }
    }

    //需求：产生指定个数的整数，并放入集合中
    public List<Integer> getNumList(int num, Supplier<Integer> sup) {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            Integer n = sup.get();
            list.add(n);
        }

        return list;
    }

    //Consumer<T> 消费型接口 :
    public void test1() {

        happy(10000, (m) -> System.out.println("你们刚哥喜欢大宝剑，每次消费：" + m + "元"));
    }

    public void happy(double money, Consumer<Double> con) {
        con.accept(money);
    }

    public static void main(String[] args) {
        Consumer<Double> con = m -> System.out.println("你们好？");
        con.accept(new Double(1.2));
    }
}
