package com.hochoy.leetcode;

import org.junit.Test;

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


}
