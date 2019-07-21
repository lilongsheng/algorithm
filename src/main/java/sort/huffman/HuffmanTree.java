package sort.huffman;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class HuffmanTree {

    /**
     * 压缩文件 可以是文件路径或网络文件
     */
    private String sourceFile;
    /**
     * 二叉树的根节点
     */
    private BinaryTreeNode root;
    /**
     * 存储不同字符以及权重
     */
    private LinkedList<CharData> charList;
    /**
     * 优先队列存储huffman树节点
     */
    private PriorityQueue<BinaryTreeNode> huffmanNodeQueue;

    private class CharData {
        int num = 1;
        char c;
        public CharData(char ch) {
            c = ch;
        }
    }

    /**
     * 构建哈夫曼树
     * @param sourceFile
     */
    public void creatHfmTree(String sourceFile) {
        try {
            this.sourceFile = sourceFile;

            charList = new LinkedList<CharData>();

            getCharNum(sourceFile);

            huffmanNodeQueue =new PriorityQueue<BinaryTreeNode>(charList.size(),
                    new Comparator<BinaryTreeNode>() {
                        @Override
                        public int compare(BinaryTreeNode o1, BinaryTreeNode o2) {
                            return o1.count - o2.count;
                        }
                    });

            creatNodes();

            creatTree();
            root = huffmanNodeQueue.peek();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计出现的字符及其频率
     * @param sourceFile
     */
    private void getCharNum(String sourceFile) {
        boolean flag;
        for (int i = 0; i < sourceFile.length(); i++) {
            char ch = sourceFile.charAt(i); // 从给定的字符串中取出字符
            flag = true;

            for (int j = 0; j < charList.size(); j++) {
                CharData data = charList.get(j);

                if (ch == data.c) {
                    data.num++;
                    flag = false;
                    break;
                }
            }

            if (flag) {
                charList.add(new CharData(ch));
            }

        }

    }

    /**
     * 将出现的字符创建成单个的结点对象
     */
    private void creatNodes() {
        for (int i = 0; i < charList.size(); i++) {
            String data = charList.get(i).c + "";
            int count = charList.get(i).num;
            BinaryTreeNode node = new BinaryTreeNode(data, count); // 创建节点对象
            huffmanNodeQueue.add(node);
        }

    }

    /**
     * 构建哈夫曼树
     */
    private void creatTree() {

        while (huffmanNodeQueue.size() > 1) {
            BinaryTreeNode left = huffmanNodeQueue.poll();
            BinaryTreeNode right = huffmanNodeQueue.poll();

            left.code = "0";
            right.code = "1";
            setCode(left);
            setCode(right);

            int parentWeight = left.count + right.count;
            BinaryTreeNode parent = new BinaryTreeNode(parentWeight, left, right);

            huffmanNodeQueue.add(parent);
        }
    }

    /**
     * 设置结点的哈夫曼编码
     *
     * @param root
     */
    private void setCode(BinaryTreeNode root) {

        if (root.lChild != null) {
            root.lChild.code = root.code + "0";
            setCode(root.lChild);
        }

        if (root.rChild != null) {
            root.rChild.code = root.code + "1";
            setCode(root.rChild);
        }
    }

    /**
     * 遍历
     *
     * @param node 节点
     */
    private void output(BinaryTreeNode node) {

        if (node.lChild == null && node.rChild == null) {
            System.out.println(node.data + ": " + node.code);
        }
        if (node.lChild != null) {
            output(node.lChild);
        }
        if (node.rChild != null) {
            output(node.rChild);
        }
    }

    /**
     * 输出结果字符的哈夫曼编码
     */
    public void output() {
        output(root);
    }


    private String hfmCodeStr = "";// 哈夫曼编码连接成的字符串

    /**
     * 编码
     *
     * @param str
     * @return
     */
    public String toHufmCode(String str) {

        for (int i = 0; i < str.length(); i++) {
            String c = str.charAt(i) + "";
            search(root, c);
        }

        return hfmCodeStr;
    }

    /**
     * @param root 哈夫曼树根节点
     * @param c    需要生成编码的字符
     */
    private void search(BinaryTreeNode root, String c) {
        if (root.lChild == null && root.rChild == null) {
            if (c.equals(root.data)) {
                hfmCodeStr += root.code; // 找到字符，将其哈夫曼编码拼接到最终返回二进制字符串的后面
            }
        }
        if (root.lChild != null) {
            search(root.lChild, c);
        }
        if (root.rChild != null) {
            search(root.rChild, c);
        }
    }


    // 保存解码的字符串
    String result = "";
    boolean target = false; // 解码标记

    /**
     * 解码
     *
     * @param codeStr
     * @return
     */
    public String CodeToString(String codeStr) {

        int start = 0;
        int end = 1;

        while (end <= codeStr.length()) {
            target = false;
            String s = codeStr.substring(start, end);
            matchCode(root, s); // 解码
            // 每解码一个字符，start向后移
            if (target) {
                start = end;
            }
            end++;
        }

        return result;
    }

    /**
     * 匹配字符哈夫曼编码，找到对应的字符
     * @param root 哈夫曼树根节点
     * @param code 需要解码的二进制字符串
     */
    private void matchCode(BinaryTreeNode root, String code) {
        if (root.lChild == null && root.rChild == null) {
            if (code.equals(root.code)) {
                result += root.data; // 找到对应的字符，拼接到解码字符穿后
                target = true; // 标志置为true
            }
        }
        if (root.lChild != null) {
            matchCode(root.lChild, code);
        }
        if (root.rChild != null) {
            matchCode(root.rChild, code);
        }

    }


    public static void main(String[] args) {

        HuffmanTree huffman = new HuffmanTree();// 创建哈弗曼对象

        String data = "aaaaaaaabbbbccddd";
        huffman.creatHfmTree(data);// 构造树

        huffman.output(); // 显示字符的哈夫曼编码

        // 将目标字符串利用生成好的哈夫曼编码生成对应的二进制编码
        String hufmCode = huffman.toHufmCode(data);
        System.out.println("编码:" + hufmCode);

        // 将上述二进制编码再翻译成字符串
        System.out.println("解码：" + huffman.CodeToString(hufmCode));
    }

}