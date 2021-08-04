package top.bestguo.chat.util;

import java.util.Random;

/**
 * 随机数工具类
 */
public class RandomUtil {

    /**
     * 指定生成范围的随机数
     *
     * @param start 指定开始范围
     * @param end 指定结束范围
     * @return 随机数
     */
    public static int randomNumber(int start, int end) {
        Random random = new Random();
        return random.nextInt(end - start) + start;
    }

}
