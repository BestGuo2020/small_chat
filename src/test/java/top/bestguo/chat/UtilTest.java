package top.bestguo.chat;

import org.junit.jupiter.api.Test;
import top.bestguo.chat.util.RandomUtil;

public class UtilTest {

    @Test
    public void testRandom() {
        System.out.println(RandomUtil.randomNumber(100000000, 999999999));
    }

}
