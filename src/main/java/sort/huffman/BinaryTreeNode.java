package main.java.sort.huffman;

public class BinaryTreeNode {

    /**
     * 节点的哈夫曼编码
     */
    public String code = "";
    /**
     * 节点的数据
     */
    public String data = "";
    /**
     * 节点权值
     */
    public int count;
    public BinaryTreeNode lChild;
    public BinaryTreeNode rChild;

    public BinaryTreeNode(String data, int count) {
        this.data = data;
        this.count = count;
    }

    public BinaryTreeNode(int count, BinaryTreeNode lChild, BinaryTreeNode rChild) {
        this.count = count;
        this.lChild = lChild;
        this.rChild = rChild;
    }

}
