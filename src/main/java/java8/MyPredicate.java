package java8;

/**
 * 公用 是否过滤员工
 * @param <T>
 */
@FunctionalInterface
public interface MyPredicate<T> {

	boolean test(T t);
	
}
