package com.logiware.rates.util;

import java.util.Arrays;

public class StringUtils {

	public static boolean isEmpty(String str) {
		return str == null || str.trim().isEmpty();
	}
	
	public static boolean isNotEmpty(String str) {
		return str != null && !str.trim().isEmpty();
	}

	public static boolean isAllEmpty(String... strs) {
        if (null != strs) {
            for (String str : strs) {
                if (isNotEmpty(str)) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public static boolean isAllNotEmpty(String... strs) {
        if (null != strs) {
            for (String str : strs) {
                if (isEmpty(str)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAtLeastOneNotEmpty(String... strs) {
        if (null != strs) {
            for (String str : Arrays.asList(strs)) {
                if (isNotEmpty(str)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }
	
	public static boolean isEqual(String str1, String str2) {
		return str1 != null && str2 != null ? str1.trim().toLowerCase().equalsIgnoreCase(str2.trim().toLowerCase()) : false;
	}

	public static boolean in(String str, String... strs) {
        if (null != str && null != strs) {
            for (String string : strs) {
                if (isEqual(str, string)) {
                    return true;
                }
            }
        }
        return false;
    }

	public static boolean notIn(String str, String... strs) {
        if (null != str && null != strs) {
            for (String string : strs) {
                if (isEqual(str, string)) {
                    return false;
                }
            }
        }
        return true;
    }

}
