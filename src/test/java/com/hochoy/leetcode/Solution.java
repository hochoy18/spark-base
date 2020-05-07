package com.hochoy.leetcode;

import org.junit.Test;

import java.util.*;

public class Solution {


    @Test
    public void isUnique() {
        boolean isUnique = isUnique("leetcode");
        System.out.println(isUnique);
        isUnique = isUnique("abc");
        System.out.println(isUnique);
    }

    public boolean isUnique(String astr) {
        char[] chars = astr.toCharArray();
        for (int i = 0; i < chars.length - 1; i++) {
            for (int i1 = i + 1; i1 < chars.length; i1++) {
                if (chars[i] == chars[i1]) {
                    return false;
                }
            }
        }
        return true;
    }

    @Test
    public void smallerNumbersThanCurrent() {
        int[] nums = {8, 1, 2, 2, 3};
        int[] result = smallerNumbersThanCurrent(nums);
        for (int i : result) {
            System.out.println(i);
        }
    }

    /**
     * how-many-numbers-are-smaller-than-the-current-number/
     *
     * @param nums
     * @return
     */
    public int[] smallerNumbersThanCurrent(int[] nums) {

        if (nums.length == 0)
            return new int[]{};
        int[] res = new int[nums.length];
        for (int i = 0; i < nums.length; i++) {
            int count = 0;
            for (int j = 0; j < nums.length; j++) {
                if (i != j && nums[i] > nums[j]) {
                    count++;
                }
            }
            res[i] = count;

        }
        return res;
    }


    @Test
    public void findNumbers() {
        int[] nums = {18853, 98579, 84784, 79454, 10299, 58256, 96202, 81050, 92048, 6855, 93106, 84110, 24894, 60975, 48451, 55448, 99647, 87199, 66581, 3063, 36963, 36696, 40852, 79534, 79591, 23124, 65905, 31938, 61166, 98292, 29774, 12087, 6811, 64437, 266, 3028, 64007, 48221, 7531, 56025, 35611, 8206, 85401, 39255, 66701, 73154, 12989, 46684, 51753, 3600, 48464, 13338, 66088, 98450, 19728, 42616, 57521, 67969, 20385, 13611, 95241, 90068, 21711, 82577, 65270, 37433, 87718, 42349, 82668, 56912, 91325, 74395, 34160, 86971, 93206, 47751, 88441, 12074, 5360, 62085, 4748, 82707, 64559, 54395, 77052, 42592, 59440, 769, 3814, 78624, 52859, 13084, 37725, 66096, 26931, 74020, 7370, 63389, 3549, 33970, 17627, 4607, 58533, 97358, 52414, 46379, 1989, 63322, 78122, 8180, 49827, 251, 41683, 61762, 91819, 46191, 30481, 97541, 15913, 41795, 60096, 61109, 87217, 25375, 40960, 38542, 72039, 91298, 44093, 67882, 93683, 73792, 87239, 22308, 94178, 70032, 31901, 63034, 14015, 54571, 63013, 81331, 9737, 63605, 22180, 28964, 78196, 27752, 76249, 85811, 97485, 40704, 48937, 76064, 39849, 7596, 97440, 35378, 4764, 47483};
        int numbers = findNumbers(nums);
        System.out.println(numbers);
    }

    public int findNumbers(int[] nums) {

        int count = 0;
        for (int num : nums) {
            if ((num + "").length() % 2 == 0)
                count++;
        }

        for (int num : nums) {
            if ((num >= 1e1 && num < 1e2) || (num >= 1e3 && num < 1e4)) {
                count++;
            }
        }

        for (int num : nums) {
            if ((int) Math.log10(num) % 2 == 1) {
                count++;
            }
        }
        return count;

    }

    @Test
    public void createTargetArray() {
        int[] nums = {0, 1, 2, 3, 4}, index = {0, 1, 2, 2, 1};
        Integer[] target = createTargetArray(nums, index);
        for (int i : target) {
            System.out.print(i + "\t");
        }
    }

    /**
     * create-target-array-in-the-given-order/
     *
     * @param nums
     * @param index
     * @return
     */
    public Integer[] createTargetArray(int[] nums, int[] index) {
        Integer[] target = new Integer[nums.length];
        for (int i = 0; i < index.length; i++) {

        }
        return target;
    }


    @Test
    public void threeSum() {
        int[] nums = {-1, 0, 1, 2, -1, -4};
        threeSum(nums);
    }

    void threeSum(int[] nums) {
        for (int i = 0; i < nums.length; i++) {

            for (int j = 0; j < nums.length / 2; j++) {
                if (i != j
                        && i != nums.length - j - 1
                        && j != nums.length - j - 1
                        && nums[i] + nums[j] + nums[nums.length - j - 1] == 0) {
                    System.out.printf(" %d\t%d\t%d%n", nums[i], nums[j], nums[nums.length - j - 1]);
                }
            }
        }

    }


    @Test
    public void merge() {
        int[] A = {1, 2, 3, 0, 0, 0}, B = {2, 5, 6};

        int m = 6, n = 3;
        merge(A, m, B, n);


//        int index = 3;
//        int[] res = new int[6 - 3 - index];
//        System.arraycopy(A, index, res, 0, 6 - 3 - index);
//        for (int re : res) {
//            System.out.println(re);
//        }

    }

    public void merge(int[] A, int m, int[] B, int n) {
        for (int b = 0; b < n; b++) {

            for (int a = 0; a < m; a++) {

                if (B[b] < A[a]) {

                    int[] res = new int[m - n - a];

                    System.arraycopy(A, a, res, 0, m - n - a);

                    System.arraycopy(res, 0, A, a + 1, m - n - a);


                }
            }

        }
        for (int re : A) {

            System.out.println(re);

        }

    }


    @Test
    public void removeElement() {
        int[] nums = {0, 1, 2, 2, 3, 0, 4, 2};
        int val = 2;
        int i = removeElement(nums, val);
        System.out.println(i);
    }

    public int removeElement(int[] nums, int val) {

        int count = 0;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != val)
                count++;
        }

        return count;
    }

    class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }
    }

    @Test
    public void addTwoNumbers() {
        ListNode l11 = new ListNode(7);
        ListNode l12 = new ListNode(2);
        ListNode l13 = new ListNode(4);
        ListNode l14 = new ListNode(3);
        l13.next = l14;
        l12.next = l13;
        l11.next = l12;


        ListNode l21 = new ListNode(5);
        ListNode l22 = new ListNode(6);
        ListNode l23 = new ListNode(4);
        l22.next = l23;
        l21.next = l22;
        addTwoNumbers(l11, l21);
    }

    public ListNode addTwoNumbers(ListNode l1, ListNode l2) {

        Stack<ListNode> reverse = reverse(l1);
        Stack<ListNode> reverse1 = reverse(l2);
        reverse.pop();
        reverse.size();
        for (ListNode listNode : reverse) {
            System.out.println(listNode.val);
        }
        for (ListNode listNode : reverse1) {
            System.out.println(listNode.val);
        }
        return null;
    }

    public Stack<ListNode> reverse(ListNode node) {
        Stack<ListNode> stack = new Stack();
        if (null == node)
            return null;
        if (null == node.next)
            stack.push(node);
        else {
            ListNode tmp = node;
            while (null != tmp) {
                stack.push(tmp);
                tmp = tmp.next;

            }
        }


        return stack;
    }


    @Test
    public void myStackTest() {

        MyStack obj = new MyStack();
        obj.push(111);
        obj.push(32);
        int param_2 = obj.pop();
        System.out.println(param_2);
        int param_3 = obj.top();
        System.out.println(param_3);

        boolean param_4 = obj.empty();
        System.out.println(param_4);
    }

    @Test
    public void calculate() {
        String s = " 3+53 / 2 -9*3+8";
//        s ="1*2-3/4+5*6-7*8+9/10+1";

        // (3 + 4) *5 - 6
        s = "   30 4 + 5 * 6 -   ";
        int calculate = calc(s);
        System.out.println(calculate);
    }

    public int calculate(String s) {
        String s1 = s.replaceAll("\\s", "");
        Stack<Integer> num = new Stack<>();
        Stack<Character> op = new Stack<>();

        char[] chars = s1.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            char ch = chars[i];

            if (isOp(ch)) {
                if (op.empty()) {
                    op.push(ch);
                } else {

                    /**
                     *
                     *  1     2
                     * 10    5
                     * 3
                     *
                     *         -
                     * -    *
                     * +
                     */

                    // * / :1
                    // + - : 0
                    if (priority(op.peek()) >= priority(ch)) {
                        int num1 = num.pop();
                        int num2 = num.pop();
                        Character op1 = op.pop();
                        int res = calc(num1, num2, op1);

                        num.push(res);
                        op.push(ch);
//                        if (res != 0){
//                           num.push(res);
//                           op.push(ch);
//                       }else {
//                           op.pop();
//                       }
                    } else {
                        op.push(ch);
                    }

                }


            } else {
                int idx = 1;
                StringBuilder numStr = new StringBuilder();
                numStr.append(ch - 48);

//                if (idx == chars.length -1){
//                    num.push(Integer.parseInt(numStr.toString()));
//                }

                while (i + idx < chars.length && !isOp(chars[i + idx])) {
                    numStr.append(chars[i + idx] - 48);
                    idx++;
                }
                num.push(Integer.parseInt(numStr.toString()));

                if (idx > 1)
                    i += (idx - 1);
            }
        }


        while (!op.empty()) {
            int num1 = num.pop();
            int num2 = num.pop();
            Character op1 = op.pop();
            int res = calc(num1, num2, op1);
            if (res != 0) {
                num.push(res);
            } else {
                op.pop();
            }
        }
        //s ="1*2-3/4+5*6-7*8+9/10";
        // 2
        /**
         * 2
         * 0
         * 30
         * 56
         *
         *
         * -
         * +
         * -
         * +

         */

        return num.pop();

    }

    private boolean isOp(char ch) {
        return ch == '+' || ch == '-' || ch == '*' || ch == '/';
    }

    private int calc(int num1, int num2, char op) {
        switch (op) {
            case '+':
                return num1 + num2;
            case '-':
                return num2 - num1;

            case '*':
                return num1 * num2;
            case '/':
                return num2 / num1;
            default:
                return 0;
        }
    }

    private int priority(char op) {
        return (op == '*' || op == '/') ? 1 : 0;
    }


    @Test
    public void nibolan() {
        List<String> xxx = xxx("11+((123+133)×14)-15");
        System.out.println(xxx);

        Stack<Integer> numStack = new Stack<>();
        Stack<Character> opStack = new Stack<>();

//        for (String s : xxx) {
//            if (s.matches("\\d+")){
//                //遇到操作数时，将其压s2；
//                numStack.push(Integer.parseInt(s));
//            }else if (s.charAt(0) == '+' || s.charAt(0) =='-' || s.charAt(0) =='*' ||s.charAt(0) == '/'){
//                //4.  遇到运算符时，比较其与s1栈顶运算符的优先级：
//                //4.1 如果s1为空，或栈顶运算符为左括号“(”，则直接将此运算符入栈；
//                if (opStack.empty() || opStack.peek() == '('){
//                    opStack.push(s.charAt(0));
//
//                }else if(priority(opStack.peek()) < priority(s.charAt(0))) {
//                    //4.2 否则，若优先级比栈顶运算符的高，也将运算符压入s1
//                    // （注意转换为前缀表达式时是优先级较高或相同，而这里则不包括相同的情况）；
//                    opStack.push(s.charAt(0));
//                }else {
//                    numStack.push(Integer.parseInt(opStack.pop().toString()));
//                }
//
//
//            }else if (s.charAt('0') == '('){
//                opStack.push(s.charAt(0));
//            }else if ()
//
//        }

    }

//    int priority(char op ){
//        return (op == '*' || op == '/') ? 1 : 0;
//    }



    public List<String> xxx(String expression) {
        String s = expression.replaceAll("\\s", "");
        List<String> list = new ArrayList<>();
        int i = 0;
        do {
            char c = s.charAt(i);
            if (c < 48 || c > 57) { //non-num
                list.add(c + "");
                i++;
            } else { // num
                String str = "";
                while (i < s.length() && (c = s.charAt(i)) >= 48 && (c = s.charAt(i)) <= 57) {
                    str += c;
                    i++;
                }
                list.add(str);
            }
        } while (i < s.length());


        return list;
    }

    public int calc(String s) {
        List<String> list = Arrays.asList(s.trim().replaceAll("\\s", " ").split(" "));
        Stack<String> stack = new Stack<>();

        for (String value : list) {
            if (value.matches("\\d+")) {
                stack.push(value);
            } else {
                int num2 = Integer.parseInt(stack.pop());
                int num1 = Integer.parseInt(stack.pop());
                int res = 0;
                switch (value) {
                    case "+":
                        res = num1 + num2;
                        break;

                    case "-":
                        res = num1 - num2;
                        break;

                    case "*":
                        res = num1 * num2;
                        break;
                    case "/":
                        res = num1 / num2;
                        break;
                }
                stack.push(String.valueOf(res));
            }
        }
        return Integer.parseInt(stack.pop());
    }

}

class MyStack {

    /**
     * Initialize your data structure here.
     */
    private Queue<Integer> input;
    private Queue<Integer> output;

    public MyStack() {
        input = new LinkedList<>();
        output = new LinkedList<>();
    }

    /**
     * Push element x onto stack.
     */
    public void push(int x) {
        input.offer(x);
        // 将b队列中元素全部转给a队列
        while (!output.isEmpty())
            input.offer(output.poll());
        // 交换a和b,使得a队列没有在push()的时候始终为空队列
        Queue temp = input;
        input = output;
        output = temp;
    }

    /**
     * Removes the element on top of the stack and returns that element.
     */
    public int pop() {
        return output.poll();
    }

    /**
     * Get the top element.
     */
    public int top() {
        return output.peek();
    }

    /**
     * Returns whether the stack is empty.
     */
    public boolean empty() {
        return output.isEmpty();
    }
}