package java8.day1;

/**
 * 采用声明式接口方式
 * 带一个参数、一个返回值
 * @param <T>
 * @param <R>
 */
@FunctionalInterface
public interface MyFunction2<T, R> {

	public R getValue(T t1, T t2);
	
}
