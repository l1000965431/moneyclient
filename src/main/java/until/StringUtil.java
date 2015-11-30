package until;

/**
 * Created by fisher on 2015/7/9.
 */
public class StringUtil {
    /**
     * ¼ì²é×Ö·û´®ÊÇ·ñÎª¿Õ
     * <p>Checks if a String is not empty (""), not null and not whitespace only.</p>
     *
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank("bob")     = true
     * </pre>
     *
     * @param str  the String to check, may be null
     * @return <code>true</code> if the String is
     *  not empty and not null and not whitespace
     * @since 2.0
     */
    public static boolean isNotBlank(String str) {
        boolean isBlank = true;
        if (str == null || "".equals(str.trim())) {
            isBlank = false;
        }
        return isBlank;
    }



    public static boolean isEmpty(String str) {
        boolean empty = false;
        if(str == null || str.trim().length() <=0){
            empty = true;
        }
        return empty;
    }
}

