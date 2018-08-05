package sort.insert;

import java.util.Arrays;

/**
 * Created by lilongsheng on 2018/7/29.
 */
public class InsertSort1 {

    public static void main(String[] args) {
        System.out.println("hello world");
        int[] array = new int[]{6,15,14,7,44,8,6,74,2,4};
        System.out.println("n="+array.length);
        int[] arrayResult = InsertSort(array, array.length);
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

//        左上角到右下角的对角线 连接起来
    }

    /**
     * 插入排序  动态图地址 http://www.atool.org/sort.php?rqnglq=9h8ac3
     * 时间复杂度：该排序最怀复杂度为O(n^2) 同选择排序、冒泡排序一样、最好为O(n+d) d为移动次数，即基本有序时
     * 用图：可以用户检查是否有序
     * 优点：短序列 、基本有序拥有这两个特点会比较快。数据量少时快
     * 缺点：每次寻找插入位置时 如果没有顺序时需要移动很多元素
     * 优化：抓住特点进行放大 短序列、基本有序两个、请看希尔排序
     * 默认从小到大排序
     * 思路：主要分为了两步 第一步选取一个未排序的元素  第二步寻找取出来元素的最佳插入位置
     * @param array 数字数组
     * @param n 数组长度
     * @return 排序后的数组
     */
    private static int[] InsertSort(int[] array,int n){
        try {
            int i,j,v;
            //外层循环 对应于第一步选取未排序元素
            for(i = 2;i<=n-1;i++){
                v = array[i];
                j = i;

                //第二步拿到未排序元素后 在已排序的元素中寻找插入位置
//                System.out.println("选取第 "+i+ " 个元素{"+array[i]+"} 插入到前 "+i+" 个已拍好序列中");
                while (j >= 1 && (array[j-1] > v) ){
                    array[j] = array[j-1];
                    j--;
//                    System.out.println("移动元素 正在找" +array[i]+ "元素的合适位置");
                }
//                System.out.println("已找到第 "+i+ " 个元素{"+array[i]+"} 插入的位置");
                array[j] = v;
                System.out.println("第" +i+ " 层循环，结果array ："+ Arrays.toString(array));
            }

            return array;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return array;
    }
}
