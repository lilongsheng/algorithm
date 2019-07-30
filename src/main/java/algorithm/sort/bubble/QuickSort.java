package algorithm.sort.bubble;

/**
 * Created by zhanggd on 2018/8/11.
 */
public class QuickSort {

    public static void main(String[] args) {
        int[] arrayData = new int[]{26, 53, 67, 48, 57, 13, 48, 32, 60, 50};

        QuickSort qs = new QuickSort();
        qs.quickSort(arrayData, 0, 9);

        for (int i : arrayData) {
            System.out.println(i);
        }
    }

    /**
     * 插入排序  动态图地址 http://www.atool.org/sort.php?rqnglq=9h8ac3
     * 时间复杂度：该排序最怀复杂度为O(n^2) 最坏情况意味着每次选取轴值都白选了，一边为空 一边为n-1
     * 最好情况和平均为n*logn 、平均复杂度也为n*logn可以根据二分公式推导出来
     * 用途：各种文件排序中
     * 优点：利用了分支法思想（一分为二）在冒泡基础之上优化，每次元素比较完不需要重复比较，也可以说下一次比较利用到了上一次比较的结果，因此高
     * 缺点：每次元素移动的跨度比较大，不稳定排序，如原始数组为12 、12‘，排序后可能为12'、12
     * 优化：在于优化找轴值，比如随机化快排 在于每次随机找轴值
     * 默认从小到大排序
     * 思路：首先找一个轴值进行元素划分，通常取半将数据分为两部分小于轴值和大于轴值，递归对划分后的两部分继续找轴值进行划分
     *
     * @param arrayData 数字数组
     * @param left      数组长度
     * @param right     数组长度
     * @return 排序后的数组
     */
    public void quickSort(int[] arrayData, int left, int right) {
        //出口必须有，否则right会减成负数，程序死循环
        if (right <= left) return;
        //选择轴值
        int pivot = selectPivot(left, right);
        //轴值与数组元素换位置（任意位置即可）
        swap(arrayData, pivot, right);
        //根据轴值分成两组
        pivot = partition(arrayData, left, right);
        //左边数组继续分组
        quickSort(arrayData, left, pivot - 1);
        //右边数组继续分组
        quickSort(arrayData, pivot + 1, right);

    }

    /**
     * 轴值选取（影响排序性能,可优化）
     *
     * @param left
     * @param right
     * @return
     */
    public int selectPivot(int left, int right) {
        return (left + right) / 2;
    }

    public void swap(int[] arrayData, int pivot, int right) {
        int temp = arrayData[right];
        arrayData[right] = arrayData[pivot];
        arrayData[pivot] = temp;
    }

    /**
     * 根据轴值对数组分为大小两组
     *
     * @param arrayData
     * @param left
     * @param right
     * @return
     */
    public int partition(int[] arrayData, int left, int right) {

        int l = left;
        int r = right;
        int temp = arrayData[r];
        //从左右两端像中间循环
        while (l < r) {
            //从左像右找 大于等于轴值得数据
            while (arrayData[l] <= temp && l < r) {
                l++;
            }
            if (l < r) {
                arrayData[r] = arrayData[l];
                r--;
            }
            //从右像左找 小于等于轴值得数据
            while (arrayData[r] >= temp && l < r) {
                r--;
            }
            if (l < r) {
                arrayData[l] = arrayData[r];
                l++;
            }
        }
        //循环结束时 l = r ,即轴值位置
        arrayData[l] = temp;

        return l;
    }
}
