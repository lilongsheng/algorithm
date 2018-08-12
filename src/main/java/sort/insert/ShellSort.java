package sort.insert;

import java.util.Arrays;

/**
 * Created by zhanggd on 2018/8/12.
 */
public class ShellSort {

    public static void main(String[] args) {
        System.out.println("开始排序");
        int[] array = new int[]{6, 15, 14, 7, 44, 8, 6, 74};
        System.out.println("数组长度为：" + array.length);
        int[] arrayResult = ShellSort1(array, array.length);
        System.out.println("排序结果为：" + Arrays.toString(arrayResult));
    }

    /**
     * shell排序  动态图地址 http://www.atool.org/sort.php?rqnglq=9h8ac3
     * 时间复杂度：该排序复杂度为O(n^2/3) 到 O(n^6/7)
     * 用图：可以用户检查是否有序
     * 优点：分治思想（一分到底 步长一直递减）插入排序的优化  利用了短序列、基本有序这两个特点
     * 缺点：交换元素的跨度比较大 因此属于不稳定排序
     * 默认从小到大排序
     * 思路：首先是选取步长，可以一直取deal=n/2 ，也可以一直变；然后再每一个子序列上面应用插入排序
     * @param array 数字数组
     * @param n 数组长度
     * @return 排序后的数组
     */
    private static int[] ShellSort1(int[] array,int n){
        int i,delta;
        //增量delta每次除以2递减
        for (delta = n/2;delta > 0;delta /=2){
            System.out.println("delta=="+delta);
            //分别对delta个子序列进行插入排序
            for (i = 0;i<delta;i++){
                ModInsSort(array,array.length,delta);
            }
        }
        return array;
    }

    //delta表示当前增量
    private static int[] ModInsSort(int[] array,int n,int delta){
        int i,j;
        //对子序列中第i个记录，寻找合适的插入位置
        for (i = delta;i<n;i+=delta){
            //j以delta为步长向前寻找逆置对进行调整
            for (j=i;j>=delta;j-=delta){
                if (array[j] < array[j-delta]){
                    int temp = array[j];
                    array[j] = array[j-delta];
                    array[j-delta] = temp;
                }else {
                    break;
                }
            }
        }

        return array;
    }

}
