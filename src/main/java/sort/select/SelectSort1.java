package sort.select;

import java.util.Arrays;

/**
 * Created by lilongsheng on 2018/7/29.
 */
public class SelectSort1 {

    public static void main(String[] args) {
        System.out.println("hello world");
        int[] array = new int[]{6,15,14,7,44,8,6,74,2,4};
        System.out.println("n="+array.length);
        int[] arrayResult = SelectSort(array, array.length);
        System.out.println(Arrays.toString(arrayResult));


//        第0 层循环，结果array ：[2, 15, 14, 7, 44, 8, 6, 74, 6, 4]
//        第1 层循环，结果array ：[2, 4, 14, 7, 44, 8, 6, 74, 6, 15]
//        第2 层循环，结果array ：[2, 4, 6, 7, 44, 8, 14, 74, 6, 15]
//        第3 层循环，结果array ：[2, 4, 6, 6, 44, 8, 14, 74, 7, 15]
//        第4 层循环，结果array ：[2, 4, 6, 6, 7, 8, 14, 74, 44, 15]
//        第5 层循环，结果array ：[2, 4, 6, 6, 7, 8, 14, 74, 44, 15]
//        第6 层循环，结果array ：[2, 4, 6, 6, 7, 8, 14, 74, 44, 15]
//        第7 层循环，结果array ：[2, 4, 6, 6, 7, 8, 14, 15, 44, 74]
//        第8 层循环，结果array ：[2, 4, 6, 6, 7, 8, 14, 15, 44, 74]

//        左上角到右下角的对角线 连接起来，是每次排序后最小元素
    }

    /**
     * 选择排序  动态图地址 http://www.atool.org/sort.php?rqnglq=9h8ac3
     * 时间复杂度：该排序最怀复杂度为O(n^2)
     * 用图：常用于数值较大的排序 因为选择排序交换次数少，只在最后找到最大最小值时交换一次，中间过程式标记下标
     * 默认从小到大排序 每次外循环会找到一个最小的元素，然后同当次第一个元素交换
     * 优点：属于基础排序
     * 缺点：适用于值较大的排序
     * 思路：每次从待排序列中寻找最大、最小元素，找到后标记下下标，然后交换放入已排序数组中
     * @param array 数字数组
     * @param n 数组长度
     * @return 排序后的数组
     */
    private static int[] SelectSort(int[] array,int n){
        int i,j,min,temp;
        //外循环数组
        for (i = 0;i < n-1;i++){
            min = i;
            //寻找最小值元素 找到后记下索引
            for (j = i+1;j < n;j++){
                if (array[j] < array[min]){
                    min = j;
                }
            }

            //每次排序寻找最小元素 找到后同当次待排元素交换
            temp = array[min];
            array[min] = array[i];
            array[i] = temp;
            System.out.println("第" +i+ " 层循环，结果array ："+ Arrays.toString(array));
        }
        return array;
    }
}
