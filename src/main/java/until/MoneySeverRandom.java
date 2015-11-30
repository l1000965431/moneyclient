package until;

import java.util.Random;

/**
 * 随机函数
 * <p>User: liumin
 * <p>Date: 15-7-7 下午2:24
 * <p>Version: 1.0
 */

public class MoneySeverRandom {

    /**
     * 获得一个随机数
     *
     * @param Min 最小范围
     * @param Max 最大范围
     * @return 返回随机数
     */
    public static int getRandomNum(int Min, int Max) {
        return (int) Math.round(Math.random() * (Max - Min) + Min);
    }


    /**
     * 获得一个范围内不重复的随机数
     *
     * @param Min 最小范围
     * @param Max 最大范围
     * @param n   随机结果的个数 不得超过Max-Min+1
     * @return 返回随机数
     */
    public static int[] getRandomNumCommon(int Min, int Max, int n) {
        if (n > (Max - Min + 1) || Max < Min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while (count < n) {
            int num = (int) (Math.random() * (Max - Min)) + Min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if (num == result[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag) {
                result[count] = num;
                count++;
            }
        }
        return result;
    }
}
