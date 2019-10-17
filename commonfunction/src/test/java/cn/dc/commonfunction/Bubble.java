package cn.dc.commonfunction;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

public class Bubble {

    /*
    冒泡排序
        外层循环执行一次表示执行一次比较
        内层循环执行一次具体的比较行为
            表示nums(0)跟后面的(nums.length - i - 1)个数通过两两比较求出最大值并放到(nums.length - i - 1)位置
     */
    @Test
    public void bubble() {
        Integer[] nums = {3, 4, 7, 5, 0};
        for (int i = 0; i < nums.length - 1; i++) {
            int temp = 0;
            //此循环一次找出前(nums.length - i - 1)的最大值
            for (int j = 0; j < nums.length - i - 1; j++) {
                if (nums[j] > nums[j + 1]) {
                    temp = nums[j];
                    nums[j] = nums[j + 1];
                    nums[j + 1] = temp;
                }
            }
        }
        /*
        坑！！！ Arrays.asList(nums)只能正常转对象类型的数组，如果是普通类型数据，会把整个数组作为一个元素。
        Arrays.asList(nums).stream().forEach(num -> System.out.println(num));
         */
        Arrays.stream(nums).forEach(num -> System.out.println(num));
    }
}
