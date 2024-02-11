import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class Task14 implements Runnable {
    public static void main(String[] args) throws IOException {
        new Thread(null, new Task14(), "", 64 * 1024 * 1024).start();
    }

    @Override
    public void run() {
        try {
            task14();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static int lowerBound(List<Node> list, Node el) {
        int l = 0;
        int r = list.size();
        int k;

        while(l < r)
        {
            k = (l+r)/2;
            if (list.get(k).getValue() >= el.getValue()) {
                r = k;
            } else {
                l = k+1;
            }
        }
        return l;
    }

    public static void task14() throws FileNotFoundException {
        Tree tree = new Tree();

        FileReader file1 = null;
        try {
            file1 = new FileReader("tst.in");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        assert file1 != null;
        Scanner sc = new Scanner(file1);
        PrintWriter out = null;
        try {
            out = new PrintWriter(new File("tst.out"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (sc.hasNextInt())
        {
            tree.add(sc.nextInt());
        }

        Stack<Node> leaves = new Stack<>();
        Node current = tree.t;
        leaves.push(tree.t);

        ArrayList<Node> findAverageNode = new ArrayList<>();
        Stack<Node> deleteNodes = new Stack<>();
        boolean isEven;
        int lowerBound = 0;

        findAverageNode.add(tree.t);

        while (leaves.size()!=0) {

            if (leaves.peek().getRight()!=null && !leaves.peek().getRight().checked) {
                current = leaves.peek().getRight();
                lowerBound = lowerBound(findAverageNode, current);
                findAverageNode.add(lowerBound, current);
                leaves.push(current);
                current.checked = true;
                current.isRight = true;

            } else if(leaves.peek().getLeft()!=null && !leaves.peek().getLeft().checked) {
                current = leaves.peek().getLeft();
                findAverageNode.add(lowerBound(findAverageNode, current), current);
                leaves.push(current);
                current.checked = true;
                current.isRight = false;
            }
            else {
                isEven = leaves.size()%2==0;
                if (leaves.size()!=0) {
                    current = leaves.peek();
                    if (current.IsLeaf()) {
                        if (leaves.size() <= tree.minPath || tree.minPath == 0) {
                            if (leaves.size() < tree.minPath) {
                                deleteNodes.clear();
                            }
                            if (!isEven) {
                                if (!deleteNodes.contains(findAverageNode.get(leaves.size()/2)))
                                    deleteNodes.add(findAverageNode.get(leaves.size()/2));
                            }
                            tree.minPath = leaves.size();
                        }
                    }
                }
                findAverageNode.remove(lowerBound(findAverageNode,current));
                leaves.pop();
            }
        }

        System.out.println("minPath = " + tree.minPath);
        System.out.println("Nodes to delete:");

        for (Node el: deleteNodes) {
            System.out.print(el.getValue() + " ");
        }

        while (deleteNodes.size()>0){
            tree.leftDelete(deleteNodes.pop());
        }
        // end
        tree.preOrderTraversal(out);

        //delete later
        assert out != null;
        out.close();
    }
}

class Tree {

    public Tree() { t=null; }
    //-------------------------------------------------------------------
    public boolean add(int x)
    {
        Node p = t;
        Node pp=null;
        boolean xIsLeft=true, xRetVal=true;
        while (p!=null)
        {
            if (x<p.getValue())
            {
                pp = p;
                p=p.getLeft();
                xIsLeft = true;
            }
            else if (x>p.getValue())
            {
                pp = p;
                p=p.getRight();
                xIsLeft = false;
            }
            else // x==p.getValue()
            {
                xRetVal = false;
                break;
            }
        }
        if (xRetVal)
        {
            p = new Node(x);
            if (pp==null)
            {
                t = p;
                t.setRoot();
            }
            else
            if (xIsLeft)
                pp.setLeft(p);
            else
                pp.setRight(p);
        }
        return xRetVal;
    }
    static private boolean isLeaf(Node p)
    {
        return p.getLeft()==null && p.getRight()==null;
    }
    //-------------------------------------------------------------------
    static private boolean isOneSon(Node p)
    {
        if (p.getLeft()==null)
            return p.getRight()!=null;
        else // p.getLeft()!=null
            if (p.getRight()==null)
                return true;
            else
                return false;

    }
    //-------------------------------------------------------------------
    //Возвращает true, если p имеет не более одного сына
    public boolean isLE1Son(Node p)
    {
        if (p.getLeft()==null)
            return true;
        else  // p.getLeft()!=null
            return p.getRight()==null;
    }
    //-------------------------------------------------------------------
    //Удаляет узел p (p имеет не более одного сына)
    public Node deleteLE1Son(Node p, Node pPrev, boolean isLeft) {

        // не для корня
        Node pSub;
        if (p == t) {
            //1t 0t
            if (p.IsLeaf()) {
                // 0t
                t = null;
            }
            else
            // 1t
            {
                if (p.getRight()!=null) {
                    t = p.getRight();
                    p.getRight().setRoot();
                } else {
                    t = p.getLeft();
                    p.getLeft().setRoot();
                }
            }
        }
        else {
            // 1!t и 0!t
            if (p.getLeft() == null)
                pSub = p.getRight();
            else
                pSub = p.getLeft();
            if (isLeft)
                pPrev.setLeft(pSub);
            else
                pPrev.setRight(pSub);
        }
        return pPrev;
    }
    /////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!////
    public Node delete(Node p, Node right, Node prevRight, boolean isLeft)
    {
        deleteLE1Son(right,prevRight,isLeft);
        p.setElement(right.getValue());
        return p;
    }

    public Node leftDelete(Node el) {
        Node delElement = t;
        Node prev = null;
        Node left = null;
        boolean isLeft = false;

        // find this element
        while (delElement!=null)
        {
            if (el.getValue()<delElement.getValue())
            {
                prev = delElement;
                delElement=delElement.getLeft();
                isLeft = true;
            }
            else if (el.getValue()>delElement.getValue())
            {
                prev = delElement;
                delElement=delElement.getRight();
                isLeft = false;
            }
            else // el==delElement.getValue()
            {
                break;
            }
        }
        // нашли
        if (delElement != null)
        {
            if (hasLE1Son(delElement))
            {
                left = deleteLE1Son(delElement,prev,isLeft);
            }
            else {
                left = delElement.getLeft();
                Node newPrev = delElement;
                boolean newIsLeft = true;

                while (left.getRight() != null) {
                    newPrev = left;
                    left = left.getRight();
                    newIsLeft = false;
                }
                left = delete(delElement, left, newPrev, newIsLeft);
            }
        }
        return left;
    }

    public boolean hasLE1Son (Node node) {
        if (node.IsLeaf()) return true;
        else {
            short b = 0;
            if (node.getRight() != null) {
                b++;
            }
            if (node.getLeft() != null) {
                b++;
            }
            return b == 1;
        }
    }
    public boolean hasOneSon (Node n)
    {
        return (n.getRight() != null && n.getLeft() == null) ||
                (n.getLeft() != null && n.getRight() == null);
    }

    public Node find(int el)
    {
        Node current = t;
        if (t!=null) {
            while (current.getValue()!=el)
            {
                if (el > current.getValue())
                {
                    current = current.getRight();
                } else if (el < current.getValue())
                {
                    current = current.getLeft();
                }
            }
        }
        return current;
    }

    public Node find(Node el)
    {
        Node current = t;
        if (t!=null) {
            while (current.getValue()!=el.getValue())
            {
                if (el.getValue() > current.getValue())
                {
                    current = current.getRight();
                } else if (el.getValue() < current.getValue())
                {
                    current = current.getLeft();
                }
            }
        }
        return current;
    }

    //-------------------------------------------------------------------
    // ef  PreOrderTraversal (v):
    //         if v is not None:
    // Action (v)
    // PreOrderTraversal (v.left)
    // PreOrderTraversal (v.right)

    public Node maxSubTreeElement (Node root, boolean rightSubtree)
    {
        Node max = find(root);

        if (rightSubtree)
        {

        }
        else {
            max = max.getLeft();
            while (max.getRight() != null) {
                max = max.getRight();
            }
        }
        return max;
    }

    public void preOrderTraversal (PrintWriter out)
    {
        preOrderTraversal(t, out);
    }

    public ArrayList<Integer> preOrderTraversal ()
    {
        return preOrderTraversal(t);
    }

    public void preOrderTraversal (PrintStream out)
    {
        preOrderTraversal(t, out);
    }

    public ArrayList<Integer> preOrderTraversal(Node tr) {
        ArrayList<Integer> arr = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        if (tr!=null)
        {
            arr.add(tr.getValue());
            preOrderTraversal(tr.getLeft());
            preOrderTraversal(tr.getRight());
        }
        return arr;
    }

    public void preOrderTraversal (Node tr, PrintWriter out)
    {
        if (tr!=null)
        {
            out.println(tr.getValue());
            preOrderTraversal(tr.getLeft(),out);
            preOrderTraversal(tr.getRight(),out);
        }
    }

    public void preOrderTraversal (Node tr, PrintStream out)
    {
        if (tr!=null)
        {
            out.print(tr.getValue() + " ");
            preOrderTraversal(tr.getLeft(),out);
            preOrderTraversal(tr.getRight(),out);
        }
    }

    public Node t;
    int minPath;
}

class Node {
    private int element;
    private Node left, right;
    private Node root;
    public int minPath;
    public boolean checked = false;
    public boolean isRight;
    //--------------------------------------------------
    public Node()
    {
        this(0,null,null);
    }
    //--------------------------------------------------
    public Node(int x)
    {
        this(x,null,null);
    }
    //--------------------------------------------------
    public Node(int e, Node aLeft, Node aRight)
    {
        element = e; left = aLeft; right = aRight;
    }
    public Node(int e, Node aLeft, Node aRight, boolean ch)
    {
        this(e,aLeft,aRight);
        checked = ch;
    }

    //--------------------------------------------------
    int getValue()
    {
        return element;
    }
    //--------------------------------------------------
    void setElement(int x)
    {
        element = x;
    }
    void setNode (Node node)  {
        element = node.getValue();
        left = node.getLeft();
        right = node.getRight();

    }
    //--------------------------------------------------
    Node getLeft()
    {
        return left;
    }
    void setRoot()
    {
        root = this;
    }
    Node getRoot() {
        return root;
    }
    //--------------------------------------------------
    //--------------------------------------------------
    void setLeft(Node newLeft)
    {
        left = newLeft;
    }
    //--------------------------------------------------
    Node getRight()
    {
        return right;
    }
    //--------------------------------------------------
    void setRight(Node newRight)
    {
        right = newRight;
    }

    boolean IsLeaf() {
        return getRight() == null && getLeft()==null;
    }
}
