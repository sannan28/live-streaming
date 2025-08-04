package live.streaming.framework.web.starter.error;


public class ErrorAssert {


    /**
     * 判断参数不能为空
     *
     * @param obj
     * @param liveBaseError
     */
    public static void isNotNull(Object obj, LiveBaseError liveBaseError) {
        if (obj == null) {
            throw new LiveErrorException(liveBaseError);
        }
    }

    /**
     * 判断字符串不能为空
     *
     * @param str
     * @param liveBaseError
     */
    public static void isNotBlank(String str, LiveBaseError liveBaseError) {
        if (str == null || str.trim().length() == 0) {
            throw new LiveErrorException(liveBaseError);
        }
    }

    /**
     * flag == true
     *
     * @param flag
     * @param liveBaseError
     */
    public static void isTure(boolean flag, LiveBaseError liveBaseError) {
        if (!flag) {
            throw new LiveErrorException(liveBaseError);
        }
    }

    /**
     * flag == true
     *
     * @param flag
     * @param liveErrorException
     */
    public static void isTure(boolean flag, LiveErrorException liveErrorException) {
        if (!flag) {
            throw liveErrorException;
        }
    }
}
