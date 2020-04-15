package com.hochoy.leetcode;

import org.junit.Test;

import java.util.Stack;

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
}
