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
     * 冒泡排序  动态图地址 http://www.atool.org/sort.php?rqnglq=9h8ac3
     * 时间复杂度：该排序最怀复杂度为O(n^2)、最好为O(n)
     * 用图：常用语数值大的排序
     * 默认从小到大排序 每次外循环会找到一个最小的元素，然后同当次第一个元素交换
     * @param array 数字数组
     * @param n 数组长度
     * @return 排序后的数组
     */
    private static int[] SelectSort(int[] array,int n){
        int i,j,min,temp;
        //循环数组
        for (i = 0;i < n-1;i++){
            min = i;
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
