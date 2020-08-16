package com.hochoy.Algorithms_dataStructures.datastructures.stack;


public class StackDemo {


    /**
     *
     * @param str
     * @return
     */
    public static boolean bracketsIsMatch(String str){

        if (str == null || str.length() <2 ){
            return  false;
        }

        Stack<Character> stack = new Stack<>();

        for (char ch : str.toCharArray()) {
            if (ch == '(' ){
                stack.push(ch);
            }else if(ch == ')'){
                Character pop = stack.pop();
                if (pop == null){
                    return false;
                }
            }
        }
        if (stack.nonEmpty()){
            return false;
        }else {
            return true;
        }
    }


    public static int conversion(int num, int r) {
        Stack<Integer> stack = new Stack<>();
        while (num > 0) {
            stack.push(num % r);
            num /= r;
        }
        StringBuilder sb = new StringBuilder();
        while (stack.nonEmpty()){
            sb.append(stack.pop());
        }
        return Integer.parseInt(sb.toString());
    }

}
