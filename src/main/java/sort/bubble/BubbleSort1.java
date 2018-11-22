package sort.bubble;

import java.util.Arrays;

/**
 * Created by lilongsheng on 2018/7/29.
 */
public class BubbleSort1 {

    public static void main(String[] args) {
        System.out.println("hello world");
        int[] array = new int[]{6,15,14,7,44,8,6,74,2,4};
        System.out.println("n="+array.length);
        int[] arrayResult = BubbleSort(array, array.length);
        System.out.println(Arrays.toString(arrayResult));


//        第9 层循环，结果array ：[6, 14, 7, 15, 8, 6, 44, 2, 4, 74]
//        第8 层循环，结果array ：[6, 7, 14, 8, 6, 15, 2, 4, 44, 74]
//        第7 层循环，结果array ：[6, 7, 8, 6, 14, 2, 4, 15, 44, 74]
//        第6 层循环，结果array ：[6, 7, 6, 8, 2, 4, 14, 15, 44, 74]
//        第5 层循环，结果array ：[6, 6, 7, 2, 4, 8, 14, 15, 44, 74]
//        第4 层循环，结果array ：[6, 6, 2, 4, 7, 8, 14, 15, 44, 74]
//        第3 层循环，结果array ：[6, 2, 4, 6, 7, 8, 14, 15, 44, 74]
//        第2 层循环，结果array ：[2, 4, 6, 6, 7, 8, 14, 15, 44, 74]
//        第1 层循环，结果array ：[2, 4, 6, 6, 7, 8, 14, 15, 44, 74]
//        第0 层循环，结果array ：[2, 4, 6, 6, 7, 8, 14, 15, 44, 74]

//        左下角到右上角的对角线 连接起来，是每次排序后最大元素
    }

    /**
     * 冒泡排序    动态图地址 http://www.atool.org/sort.php?rqnglq=9h8ac3
     * 时间复杂度：该排序最怀复杂度为O(n^2)、最好为O(n)
     * 用图：可以用户检查是否有序
     * 优点：分治思想（一分到底） 简单 容易理解 属于基础排序
     * 缺点：每次都是两两比较交换 存在重复比较交换 （逆向）  可以对这点进行优化 即快速排序
     * 默认从小到大排序 泡泡每次到最后一个数字 需要注意的是外层循环次数 n 如果是长度那么为n-1 ,边界地方要有等于号
     * @param array 数字数组
     * @param n 数组长度
     * @return 排序后的数组
     */
    private static int[] BubbleSort(int[] array,int n){
        for (int pass = n-1;pass >= 0;pass--){
            for (int i = 0;i <= pass-1;i++){
                if (array[i] >= array[i+1]){
                    int temp = array[i];
                    array[i] = array[i+1];
                    array[i+1] = temp;
                }
            }
            System.out.println("第" +pass+ " 层循环，结果array ："+ Arrays.toString(array));
        }
        return array;
    }
}
