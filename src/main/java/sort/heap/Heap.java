package main.java.sort.heap;

public class Heap {

    /**
     * 数据元素
     */
    public int[] array;
    /**
     * 元素个数
     */
    public int count;
    /**
     * 堆得大小
     */
    public int capacity;
    /**
     * 最小堆或最大堆
     */
    public int heap_type;

    /**
     * 创建堆
     * @param capacity
     * @param heap_type
     */
    public Heap(int capacity,int heap_type){
        this.heap_type = heap_type;
        this.count = 0;
        this.capacity = capacity;
        this.array = new int[capacity];
    }

    /**
     * 返回父节点
     * @param i
     */
    public int Parent(int i){
        if (i <= 0 || i >= this.count){
            return -1;
        }
        return (i-1)/2;
    }

    /**
     * 返回左孩子
     * @param i
     * @return
     */
    public int LeftChild(int i){
        int left = 2*i + 1;
        if (left >= this.count){
            return -1;
        }
        return left;
    }

    /**
     * 返回右孩子
     * @param i
     * @return
     */
    public int RightChild(int i){
        int right = 2*i + 2;
        if (right >= this.count){
            return -1;
        }
        return right;
    }

    /**
     * 返回最大堆元素
     * @param i
     * @return
     */
    public int GetMaximum(int i){
        if (this.count == 0){
            return -1;
        }
        return this.array[0];
    }

    /**
     * 自顶向下逐步堆化二叉树
     * @param i
     */
    public void PercolateDown(int i){
        int l,r,max,temp;
        l = LeftChild(i);
        r = RightChild(i);
        if (l != -1 && array[l] >= array[i]){
            max = l;
        }else {
            max = i;
        }
        if (r != -1 && array[r] >= array[i]){
            max = r;
        }
        if (max != i){
            temp = this.array[i];
            this.array[i] = this.array[max];
            this.array[max] = temp;
        }
        PercolateDown(max);
    }

    /**
     * 从堆顶删除元素，然后将最后一个元素复制到这个位置，再从根节点调整堆结构
     * @return
     */
    int DeleteMax(){
        if (this.count == 0){
            return -1;
        }
        int data = this.array[0];
        //最后的元素与堆顶元素互换
        this.array[0] = this.array[this.count-1];
        //减小堆的大小
        this.count--;
        PercolateDown(0);
        return data;
    }

    /**
     * 像堆中插入新的元素
     * 插入时为自下向上逐渐调整元素位置 与删除时相反
     * @param data
     * @return
     */
    int Insert(int data){
        int i;
        if (this.count == this.capacity){
            ResizeHeap();
        }
        //增加堆的大小来放置新元素
        this.count++;
        i = this.count-1;
        //该循环过程是在找新插入元素的位置，找到之后赋值完成插入操作
        while (i >= 0 && data > this.array[(i-1)/2]){
            this.array[i] = this.array[(i-1)/2];
            i = (i-1)/2;
        }
        this.array[i] = data;
        return i;
    }

    /**
     * 调整堆的大小 每次扩充为原来2倍
     */
    void ResizeHeap(){
        int[] array_old = new int[this.capacity];
        //复制到新的数组中
        System.arraycopy(this.array,0,array_old,0,this.count-1);
        this.array = new int[this.capacity * 2];
        if (this.array == null){
            System.out.println("Memory error");
            return;
        }
        for (int i = 0;i < this.capacity ; i++){
            this.array[i] = array_old[i];
        }
        this.capacity *= 2;
        array_old = null;
    }

    void DestroyHeap(){
        this.count = 0;
        this.array = null;
    }

    /**
     * 创建堆
     * @param h
     * @param A
     * @param n
     */
    void BuildHeap(Heap h,int A[],int n){
        if (h == null)
            return;
        while (n > this.capacity){
            h.ResizeHeap();
        }
        for (int i = 0;i < n;i++){
            h.array[i] = A[i];
        }
        this.count = n;
        for (int i= (n-1)/2 ;i>=0 ;i--){
            h.PercolateDown(i);
        }
    }

    /**
     * 堆排序
     * 步骤是不断的从堆的顶部取元素，顶部为最大或最小，然后最后一个元素移动到顶部，不断的取不断的调整堆大小，既可以排好序
     * 因为从堆中删除、和插入元素的复杂度为O(lonN) 所有堆排序整体复杂度为O(N*logN)
     */
    void Heapsort(int A[],int n){
        Heap h = new Heap(n,0);
        int old_size,i,temp;
        //第一步先利用给出的数据构建堆
        BuildHeap(h,A,n);
        old_size = h.count;
        //不断的从堆顶取出来元素，然后从新调整堆满足对性质，继续取元素
        for (i = n-1;i>0;i--){
            temp = h.array[0];h.array[0] = h.array[h.count-1];
            h.count--;
            h.PercolateDown(i);
        }
        h.count = old_size;
    }
}
