package com.hochoy.leetcode;

import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

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


    @Test
    public void canMakeArithmeticProgression() {
        int[] arr = new int[]{65, 58, 95, 10, 57, 62, 13, 106, 78, 23, 85};

        arr = new int[]{3,5,7,9,1,13,11};
        System.out.println("排序前："+ Arrays.toString(arr));
        boolean result = canMakeArithmeticProgression(arr);
        System.out.println("排序前："+ Arrays.toString(arr));
        System.out.println(result);
    }
    public boolean canMakeArithmeticProgression(int[] arr) {
        int len;
        if ( (len = arr.length)  <3 )
            return true;
        quickSort(arr,0,len-1);
        int  gap = arr[1] - arr[0];
        for (int i = 0; i < arr.length-1; i++) {
            if (gap !=  arr[i+1] - arr[i]){
                return false;
            }
        }
        return true;
    }

    void quickSort(int[] arr, int left ,int right){

        int pivot = 0;
        if (left < right){
            pivot = partition(arr,left,right);
            quickSort(arr,left,pivot-1);
            quickSort(arr,pivot +1 ,right);

        }

    }


    int partition(int[] arr , int left, int right){
        int flag = arr[right];
        while (left < right){
            while (left<right && arr[left] <= flag){
                left ++;
            }
            arr[right] = arr[left];

            while (left < right && arr[right] >= flag){
                right --;
            }
            arr[left] = arr[right];
        }
        arr[left] = flag;
        return left;
    }



    @Test
    public void sortString() {
        String s = "abcabcabcabc";
        String res = sortString(s);
        assertEquals("abccbaabccba",res);



    }
    public String sortString(String str ) {

        // a , 3
        // b , 3
        // c , 3

        int a = 'c';

        System.out.println(a);



        StringBuilder sb = new StringBuilder();
         char[] arr = str.toCharArray();


//        System.out.println("before sort: "+Arrays.toString(arr));
//        quickSort(arr,0,arr.length-1);
//        System.out.println("after  sort: "+Arrays.toString(arr));
//

        sb.append('c');

        return sb.toString();
    }

    //      0 1 2 3 4 5 6 7 8
    //      a a a b b b c c c  min = A[0] = a ,max = A[8] = c
    //1 ->  _ a a _ b b _ c c  min = A[1] ,max = A[8] count+=1,count+=1,count+=1 ; boolean toBig = true;
    //2 <-  _ a _ _ b _ _ c _
    //3 ->  _ a _ _ _ _ _ _ _

    String  sortString( char[] arr ) {

        StringBuilder sb = new StringBuilder();

        int len = arr.length;

        char min = arr[0];

        char max = arr[len - 1];

        int count = 0;

        // front -> back
        for (int i = arr.length - 1; i >= 0; i--) {

            sb.append(arr[i]);



        }





//        boolean toBig = true;
//        while (count < len){
//            if (toBig){
//                for (int i = arr.length - 1; i >= 0; i--) {
//
//                }
//
//            }else{
//
//            }
//        }


        return sb.toString();
    }



    void quickSort(char[] arr , int left,int right){
        int pivot = 0;
        if (left < right){
            pivot = partition(arr,left,right);
            quickSort(arr,left,pivot -1);
            quickSort(arr,pivot +1 ,right );

        }

    }
    int partition(char[] arr, int left, int right){
        char flag = arr[left];
        while (left < right){
            while (left<right && arr[right] >= flag){
                right --;
            }
            arr[left] = arr[right];
            while (left < right && arr[left] <= flag){
                left ++;
            }
            arr[right] = arr[left];
        }

        arr[left] = flag;
        return left;
    }









    @Test
    public void average() {

        int[] salary = new int[]{8000,9000,2000,3000,6000,1000};
        double avg = average1(salary);
        System.out.println(avg );

        int []arr = new int[]{25000,48000,57000,86000,33000,10000,42000,3000,54000,29000,79000,40000};
        avg = average(arr);
        System.out.println(avg);
        System.out.println();

        avg = average1(arr);
        System.out.println(avg);

    }


    public double average1(int[] salary) {
        int min = salary[0] ,max = salary[0] ;
        double sum = 0d;
        for (int i = 0; i < salary.length; i++) {

            if (salary[i] <= min)
                min = salary[i];
            if (salary[i] >= max)
                max = salary[i];
            sum += salary[i];
        }
        System.out.println("min: " + min + ",  max: " + max);
        return (sum - min - max) / (salary.length - 2);

    }


    public double average(int[] salary) {
        System.out.println("before sort :"+Arrays.toString(salary));
        quickSortSalary(salary,0,salary.length - 1);
        System.out.println("after sort :"+Arrays.toString(salary));

        double sum = 0d;
        for (int i = 1 ; i < salary.length -1; i++) {
            sum += salary[i];
        }

        return sum /( salary.length -2);
    }


    void quickSortSalary(int[] salary,int left,int right){
        int pivot  ;
        if (left<right){
            pivot = partitionSalary(salary,left,right );
            quickSortSalary(salary, left,pivot-1);
            quickSortSalary(salary,pivot + 1,right);
        }

    }

    int partitionSalary(int[] salary , int left , int right ){
        int key = salary[left];
        while (left< right){
            while (left < right && salary[right] > key) {
                right --;
            }
            salary[left] = salary[right];
            while (left < right && salary[left] < key){
                left ++;
            }
            salary[right] = salary[left];
        }
        salary[left] = key;
        return left;
    }





    @Test
    public void minSubsequence() {
        int[]nums = new int[]{4,4,7,6,7};

        List<Integer> list = minSubsequence(nums);
        System.out.println(list);
        nums= new int []{4,3,10,9,8};
        list = minSubsequence(nums);
        System.out.println(list);
    }



    public List<Integer> minSubsequence(int[] nums) {

        List<Integer>  res = new ArrayList<>();
        if(nums.length == 1){
            res.add(nums[0]);
            return res;
        }

        System.out.println("before sort : " + Arrays.toString(nums));
        quickSortDesc(nums,0,nums.length -1 );
        System.out.println("after sort : " + Arrays.toString(nums));

        int sum = 0,tmp = 0;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i] ;
        }


        for (int i = 0; i < nums.length; i++) {
            tmp += nums[i];
            if (tmp <= sum - tmp){
                res.add(nums[i]);
            }else {
                res.add(nums[i]);
                break;
            }
        }



        return res;
    }

    void quickSortDesc(int[]nums ,int left,int right){
        int pivot ;
        if (left < right){
            pivot = partitionDesc(nums,left,right);
            quickSortDesc(nums,left,pivot - 1 );
            quickSortDesc(nums,pivot + 1 ,right);
        }
    }

    int partitionDesc(int[]nums , int left ,int right ){

        int key = nums[left];
        while (left<right){
            while (left < right && nums[right] <= key){
                right -- ;
            }
            nums[left] = nums[right];
            while (left < right && nums[left] >= key){
                left ++;
            }
            nums[right] = nums[left];
        }
        nums[left] = key;
        return left;
    }





    @Test
    public void countSmaller() {
        int[]nums = new int[]{};
        List<Integer> list = countSmaller(nums);
        System.out.println(list);

    }
    public List<Integer> countSmaller(int[] nums) {
        List<Integer> res = new ArrayList<>();
        if (nums.length == 0){
            return res;
        }

        for (int i = 0; i < nums.length -1 ; i++) {
            int count =0 ;
            for (int j = i+1; j < nums.length; j++) {
                if (nums[i] > nums[j])
                    count ++;
            }
            res.add(count);
        }
        res.add(0);

        return  res;
    }

    @Test
    public void countNegatives(){
        int[][] grid;
        int countNegatives ;

//        grid= new int[][]{{4,3,2,-1},{3,2,1,-1},{1,1,-1,-2},{-1,-1,-2,-3}};
//        countNegatives = countNegatives(grid);
//        assertEquals(8,countNegatives);
////
//        countNegatives = countNegatives1(grid);
//        assertEquals(8,countNegatives);

        grid = new int[][]{{3,-1},{-1,-1}} ;
        countNegatives = countNegatives1(grid);
        assertEquals(3,countNegatives);


    }
    public int countNegatives(int[][] grid) {
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            int[] line = grid[i];
            for (int j = 0; j < line.length; j++) {
                if (line[j] < 0)
                    count ++;
            }
        }
        return count;
    }

    // 8   7   5    3    4    7   9    19  -32
    // 0   1   2    3    4    5   6    7    8
    // L                 M                  R
    //                        L   M         R
    //                                 L    R
    //                                     L/R

    // 8  -7  -5   -3   -4   -7  -9   -19  -32
    // 0   1   2    3    4    5   6    7    8
    // L                 M                  R
    //

    public int countNegatives1(int[][] grid){
        int count = 0;
        for (int i = 0; i < grid.length; i++) {
            int[] row = grid[i];
            int len = row.length;
            int loc = firstNegativeLoc(row);

            System.out.println(loc + " --> " + (len - loc ));
            count += (len-loc);
        }
        return count;
    }
    public int firstNegativeLoc(int[] grid){
        if (grid[grid.length -1] >= 0)
            return grid.length;
        if(grid[0] < 0)
            return 0;
        int left = 0 ;
        int right = grid.length - 1 ;
        while (left < right ){
            int mid = (left + right ) >> 1;
            if ( grid[mid] >=0 ){
                left = mid + 1;
            }else {
                if (grid[mid - 1] >= 0)
                    return mid;
                right = mid -1;
            }
        }
        return left;
    }


    @Test
    public void peakIndexInMountainArray() {
        int[] A ;
        int index;

        A = new int[]{1,3, 5, 7, 9, 11, 13, 15  };
        index = peakIndexInMountainArray(A);
        assertEquals(7 ,index );

        A = new int[]{0, 1, 0};
        index = peakIndexInMountainArray(A);
        assertEquals(1 ,index );

        A = new int[]{0, 2, 1, 0};
        index = peakIndexInMountainArray(A);
        assertEquals(1 ,index );



    }

    public int peakIndexInMountainArray(int[] A) {

        // 0 ,1, 2, 4
        // 0  1  0
        for (int i = 1; i < A.length -1; i++) {
            if (A[i] > A[i-1] && A[i] > A[i+1]){
                return i;
            }
        }
        return -1;


    }











//    public int peakIndexInMountainArray1(int[] A) {
//        int L = 0 ;
//        int R = A.length - 1;
//        while (L <= R){
//            int M = (L + R) >> 1;
//            if (M>0 && M <  &&A[M] < A[R] )
//
//        }
//
//    }


    @Test
    public void kWeakestRows() {
        int[][] mat = new int[][]{
                {1, 1, 0, 0, 0},
                {1, 1, 1, 1, 0},
                {1, 0, 0, 0, 0},
                {1, 1, 0, 0, 0},
                {1, 1, 1, 1, 1}
        };

        int[] res = kWeakestRows(mat,3);
        assertArrayEquals(new int[]{2,0,3},res);

    }

    public int[] kWeakestRows(int[][] mat, int k) {

        int len = mat.length;
        int[] tmp = new int[len];
        int[] res = new int[k];

        for (int i = 0; i < len; i++) {
            int[] row = mat[i];
            int count = 0;
            for (int ele : row) {
                if (ele  == 1){
                    count ++;
                }else {
                    break;
                }
            }
            tmp[i] = count;
        }



        // 2  4  1  2  5
        return tmp;


    }

    @Test
    public void isPerfectSquare() {
        boolean flag;
//        flag = isPerfectSquare(16);
//        assertEquals(true,flag);

//        flag = isPerfectSquare(10000);
//        assertEquals(true ,flag);
//
//
//        flag = isPerfectSquare(1000000);
//        assertEquals(true ,flag);

//        flag = isPerfectSquare(1000001);
//        assertEquals(true ,flag);



        flag = isPerfectSquare(808201);
        assertEquals(true ,flag);
        System.out.println(flag);

    }

    // L   M   R     MM     16
    // 0   8   16    64     >     R = M -1
    // 0   3    7    9      <     L = M +1
    // 4   5    7    25     >     R = M -1
    // 4   4   4     14     =
    public boolean isPerfectSquare(int num) {
        if (num ==1)return true;

        long  L = 1;
        long  R = num ;
        while (L <= R){
            long M = (L + R) / 2;
            long MM = M * M;
            if (MM == num)
                return true;
            else if (MM < num )
                L = M +1;
            else if (MM > num)
                R = M -1;
        }
       return false;

    }

    @Test
    public void guessNumber() {

    }


    public int guessNumber(int n) {

        return n;
    }




    @Test
    public void game() {
        int[] guess = new int[]{1,2,3};
        int[] answer = new int[]{1,2,3};
        int game = game(guess,answer);

        assertEquals(3,game);


    }


    public int game(int[] guess, int[] answer) {
        int count = 0;
        int i = 0 ;
        int len = guess.length;
        while(i < len && guess[i] == answer[i] ){
            count ++;
            i++;
        }
        return count;

    }


    @Test
    public void twoSum() {
        int[]numbers = new int[]{2, 7, 11, 15};
        int target = 17;

        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long start = System.nanoTime();
        int[] res = twoSum(numbers, target);

        long twoSum =  ( System.nanoTime() - start);
        System.out.println("twoSum: " +twoSum + "  nano seconds");
        System.out.println(Arrays.toString(res));
        assertArrayEquals(new int[]{1,4},res);

        start =  System.nanoTime();
        res = twoSum1(numbers, target);

        long twoSum1 =  ( System.nanoTime() - start);
        System.out.println("twoSum1: " + twoSum1+ "  nano seconds");
        System.out.println(" binarySearch is faster than tow-pointer : " + (twoSum1 < twoSum) );
        System.out.println(Arrays.toString(res));
        assertArrayEquals(new int[]{1,4},res);

    }

    public int[] twoSum(int[] numbers, int target) {
        int[] res = new int[2];
        for (int i = 0; i < numbers.length; i++) {
            for (int j = i+1; j < numbers.length; j++) {
                if (numbers[i] + numbers[j] == target)
                    res = new int[]{i+1,j+1};
            }
        }
        return res;
    }
    public int[] twoSum1(int[] numbers, int target) {
        for (int i = 0; i < numbers.length - 1; i++) {
            int first = numbers[i];
            int start = i + 1;
            int second = target - first;
            int idx = binarySearch(numbers,start,second);
            if (idx != -1){
                return new int[]{i+1,idx +1};
            }
        }
        return new int[0];

    }

    // 0  1  2  3  4  5  6  7   8  9   10  11  12  13
    // 1  2  3  4  6  8  9  10  11 12  15  17  18  19    target = 5 return {2 ,3 }
    //       L              M                       R
    //
    public int binarySearch(int[] numbers, int start ,int target){

        int end = numbers.length - 1;
        while (start <= end){
            int mid = (start + end ) >> 1;
            if (numbers[mid] == target)
                return mid;
            else  if (numbers[mid] > target){
                end = mid - 1;
            }else  if (numbers[mid] < target){
                start = mid +1;
            }
        }
        return -1;
    }

    @Test
    public void intersect() {

        int[] nums1 = new int[]{1, 2,2,1};
        int[] nums2 = new int[]{2, 2};

        int[] intersect = intersect(nums1, nums2);
        assertArrayEquals(new int[]{2,2},intersect);
        System.out.println(Arrays.toString(intersect));


    }


    public int[] intersect(int[] nums1, int[] nums2) {
        int len1 = nums1.length;
        int len2 = nums2.length;
        int[] min ;//= len1 >  len2 ? nums2 : (len1 == len2 ? nums1 : nums2);
        int[] max ;//= len1 <  len2 ? nums2 : (len1 == len2 ? nums2 : nums1);

        if (len1 <= len2){
            min = nums1;
            max = nums2;
        }else {
            min = nums2;
            max = nums1;
        }

        quickSort1(max,0,max.length - 1);// O( N * logN )
        quickSort1(min,0,min.length - 1);// O( N * logN )


        // 1  2  2
        // 1  2  2  3
        int kk = 0;

       int[] res = new int[min.length ];
        for (int i = 0; i < min.length; i++) {
            int k = min[i];
            int bs = bs(max, kk, k);
            if (bs != -1){
                res[kk] = k;
                System.out.println(k + "----->" + kk);
                kk ++;
            }
        }



        int[] result = new int[kk];
        System.arraycopy(res,0,result,0,kk);
        return result;

    }


    int bs(int[] nums ,int start,int target){
//        int start = 0;
        int end = nums.length - 1;
        // 1  2  3  4  5  7
        while (start <= end){
            int mid = (start + end ) >> 1;
            if (nums[mid] == target)
                return mid;
            else if (nums[mid] > target)
                end  = mid -1;
            else if (nums[mid ] < target)
                start = mid +1;
        }


        return  -1;
    }

    void quickSort1(int[] nums ,int left ,int right ){
        int pivot = 0 ;
        if (left < right){
            pivot = partition1(nums, left, right);
            quickSort1(nums,left,pivot -1);
            quickSort1(nums,pivot +1 , right);
        }
    }

    // k=2
    //  2  2  9  6  4  7  9
    //  0  1  2  3  4  5  6
    //     P K/L           R
    // key = 9
    //  2  2  9  6  4  7  9
    //     P K/L       R
    //  2  2  7  6  4  9  9
    //                 P
    int partition1(int[] nums , int left ,int right ){
        int key = nums[left];
        while (left < right){

            while ( left < right && nums[right] >= key){
                right -- ;
            }
            nums[left] = nums[right];
            while (left < right && nums[left] <= key){
                left ++;
            }
            nums[right] = nums[left];
        }
        nums[left] = key;
        return left;
    }




    @Test
    public void sortArrayByParityII() {

        int[] A = new int[]{4,2,5,7};
        int[] res = sortArrayByParityII(A);

        System.out.println(Arrays.toString( res ));

        res = sortArrayByParityII2(A);
        System.out.println(Arrays.toString( res ));


    }

    public int[] sortArrayByParityII2(int[] A) {

        int[] res = new int[A.length];

        int nextOddIndex = 1;
        int nextEvenIndex = 0;

        for (int i = 0; i < A.length; i++) {
            int Ai = A[i];
            if ((Ai & 1) == 1) {
                res[nextOddIndex] = Ai;
                nextOddIndex += 2;
            } else {
                res[nextEvenIndex] = Ai;
                nextEvenIndex += 2;
            }
        }
        return res;
    }



    public int[] sortArrayByParityII(int[] A) {

        int[] res = new int[A.length];
        Stack<Integer> odd = new Stack<>(); // 奇数
        Stack<Integer> even = new Stack<>(); // 偶数
        for (int i = 0; i < A.length; i++) {
            int Ai = A[i];

            if ((Ai & 1) == 1){
                odd.push(Ai);
            }else{
                even.push(Ai);
            }
        }
        for (int i = 0; i < res.length; i++) {
            if ( (i & 1) == 1)
                res[i] = odd.pop();
            else res[i] = even.pop();
        }

        return res;
    }

    void insertSort(int[] nums){

        int tmp ;
        for (int i = 1; i < nums.length; i++) {

            if (nums[i-1] > nums[i]){
                tmp = nums[i];
                int j = i;
                while (j>=0){
                    if (j> 0 && nums[j-1] > tmp){
                        nums[j ]= nums[j];
                    }else{
                        nums[j ]= tmp;
                    }
                    j--;

                }


            }

        }


    }



    @Test
    public void minimumTotal() {
        List<List<Integer>> triangle = new ArrayList<>();

        triangle.add(Collections.singletonList(2));
        triangle.add(Arrays.asList(3,4));
        triangle.add(Arrays.asList(6,5,7));
        triangle.add(Arrays.asList(4,1,8,3));
        int res = minimumTotal(triangle);
        System.out.println(res);
        assertEquals(11,res);

    }

    /**
     * [
     *       [ 2 ]
     *     [ 3, 4 ]
     *   [ 6, 5, 7 ]
     * [ 4, 1, 8, 3 ]
     * ]
     */
    public int minimumTotal(List<List<Integer>> triangle) {

        int n = triangle.size();

        int[][] f = new int[n][n];
        // f[i][j] 表示从三角形顶部走到位置 (i, j)的最小路径和。
        //(i,j) 指的是三角形中第 ii 行第 jj 列（均从 00 开始编号）的位置。
        f[0][0] = triangle.get(0).get(0);

        for (int i = 1; i < n; i++) {
            f[i][0] = f[i-1][0] + triangle.get(i).get(0);
            for (int j = 1; j < i; j++) {
                f[i][j] = Math.min(f[i-1][j-1], f[i-1][j] + triangle.get(i).get(j));
            }
        }

        return 0;

    }


    @Test
    public void subtractProductAndSum() {
        int n = 4421;
        int res = subtractProductAndSum(n);
        System.out.println(res);

    }

    public int subtractProductAndSum(int n) {
//        int mod ;
//        int tmp = n;
//        if (n<10){
//            return 0;
//        }
//        int sum = 0;
//        int prod = 1;
//        while (tmp >=10){
//            mod = tmp % 10;
//            sum += mod;
//            prod *= mod;
//            tmp /= 10;
//        }
//        mod = tmp % 10;
//        sum += mod;
//        prod *= mod;
//        return prod - sum;


        int add = 0,mult = 1;
        while (n > 0){
            int mod = n % 10;
            n /= 10;
            add += mod;
            mult *= mod;
        }
        return mult - add;

    }

    @Test
    public void removeDuplicates() {
        int[] nums = new int[]{0,0,1,1,1,2,2,3,3,4};
        nums = new int[]{1,2,3,4,5};


        int res = removeDuplicates(nums);
        System.out.println(Arrays.toString(nums));

        System.out.println(res);
        assertEquals(5,res);

        nums = new int[]{1,2,3,4,5};
        res = removeDuplicates(nums);
        System.out.println(Arrays.toString(nums));
        System.out.println(res);
    }


    // 0,0,1,1,1,2,2,3,3,4
    //
    public int removeDuplicates(int[] nums) {
        int len = nums.length;
        if (len<=1)
            return len;

        int start = 0;

        int dups = 0;
        int max = nums[len-1];
        while (start < len-1){
            if (max == nums[start])
                return start +1;
            if(nums[start] == nums[start + 1 ]){
                System.arraycopy(nums,start+1,nums,start,len - start-1);
                dups ++;
            }
            else
                start++;
        }
        return len - dups;

    }

    @Test
    public void maxArea() {
        int[] height = new int[]{1,8,6,2,5,4,8,3,7};
        int maxArea = maxArea(height);
        System.out.println(maxArea);
        assertEquals(49,maxArea);

    }
    public int maxArea(int[] height) {
        int max = 0;
        for (int i = 0; i < height.length - 1; i++) {
            for (int j = i+1; j < height.length; j++) {
                int hi = height[i];
                int hj = height[j];
                int multi = Math.min(hi,hj) * (j-i);

                if (multi > max){
                    max = multi;
                }
            }
        }
        return max;

    }


    @Test
    public void threeSumClosest() {
        int[] nums = new int[]{-1, 2, 1, -4};
        int target = 1;

        nums = new int[]{-3,-2,-5,3,-4};
        target = -1;

        int res = threeSumClosest(nums, target);
        System.out.println(res);


    }

    public int threeSumClosest(int[] nums, int target) {
        int res;
        int[] resArr = new int[]{nums[0], nums[1], nums[2]};
        int minAbs = Math.abs(nums[0] + nums[1] + nums[2] - target);

        for (int i = 0; i < nums.length - 2; i++) {
            for (int j = i + 1; j < nums.length - 1; j++) {
                for (int k = j + 1; k < nums.length; k++) {
                    int sum = nums[i] + nums[j] + nums[k];
                    int tmpAbs = Math.abs(sum - target);
                    if (tmpAbs < minAbs && (i != j && j != k && i != k)) {
                        resArr = new int[]{nums[i], nums[j], nums[k]};
                        minAbs = tmpAbs;
                    }

                }
            }
        }

        res = resArr[0] + resArr[1] + resArr[2];
        System.out.println(Arrays.toString(resArr));
        return res;
    }

    @Test
    public void searchInsert() {
        int[]nums = new int[]{1,3,5,6};

//        for (int i = 0; i < nums.length; i++) {
//            int res = searchInsert(nums,nums[i]);
//            System.out.print(res + " ");// 0 1 2 3
//            assertEquals(i,res);
//        }
        System.out.println();
        int[] out = new int[]{-1,0,2,4,9};
        int[] kk = new int[]{ 0, 0,1,2,4};
        for (int i = 0; i < out.length; i++) {
            int res = searchInsert(nums,out[i]);
            System.out.print(res + " \n");//0 0 1 2 4
            assertEquals(kk[i],res);
        }


    }

    public int searchInsert(int[] nums, int target) {

        int len = nums.length;
        int left = 0;
        int right = len - 1;

        int sub0 = Math.abs(target - nums[0]);
        int data = 0;

        int idx = -1;
        while (left <= right) {
            int mid = (left + right) >> 1;
            int sub = target - nums[mid] ;
            if (sub0 >= Math.abs(sub)){
                sub0 = Math.abs(sub);
                idx = mid;
                data = nums[mid];
            }
            if (sub<0) {
                right = mid - 1;
            }else if (sub > 0 ){
                left = mid + 1;
            }else {
               return idx;
            }
        }
        System.out.printf("data: %d, target: %d,  idx:  %d %n",data, target ,idx );
        return data > target ? idx  : idx +1;

    }
    @Test
    public void numIdenticalPairs() {

        int[]nums = new int[]{1,2,3,1,1,3};

        int res = numIdenticalPairs(nums);

        System.out.println(res);

        assertEquals(4,res);

        nums = new int[]{1,1,1,1};
        res = numIdenticalPairs(nums);

        System.out.println(res);

        assertEquals(6,res);

        nums = new int[]{6,5,1,5,7,7,9,1,5,7,1,6,10,9,7,4,1,8,7,1,1,8,6,4,7,4,10,5,3,9,10,1,9,5,5,4,1,7,4,2,9,2,6,6,4,2,10,3,5,3,6,4,7,4,6,4,4,6,3,4,10,1,10,6,10,4,9,6,6,4,8,6,9,5,4};
        res = numIdenticalPairs(nums);

        System.out.println(res);

        assertEquals(303,res);


    }


    public int numIdenticalPairs(int[] nums) {

        Map<Integer, Integer> map = new HashMap<>();
        for (int num : nums) {
            if (!map.containsKey(num))
                map.put(num, 1);
            else map.put(num, map.get(num) + 1);
        }
        int count = 0;
        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            Integer value = entry.getValue();
            if (value >1){
                int combiner = value *(value-1)/2 ;//factorial((long)value) / ( 2 * factorial((long)(value-2)));
                count += combiner;
            }
        }
        return count;
    }

    long factorial(long num) {
        if (num <= 1)
            return 1;
        return num * factorial(num - 1);
    }

    @Test
    public void getKth() {

    }

    public int getKth(int lo, int hi, int k) {


        return 0;
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