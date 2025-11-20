package kr.co.cleaning.core.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.stereotype.Component;

@Component("sUtils")
public class SUtils {

	/**
	 * 현재 날짜
	 * */
    public static String getYear() {
        return getToDatePattern("yyyy");
    }

    public static String getMonth() {
        return getToDatePattern("MM");
    }

    public static String getDay() {
        return getToDatePattern("dd");
    }

    public static String getToDay() {
    	return getToDatePattern("yyyyMMdd");
    }

    public static String getDate(){
    	return getToDatePattern("yyyyMMddHHmmss");
    }

    public static String getToDatePattern(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        return formatter.format(new Date());
    }

    /**
     * 문자열 값의 널 여부를 체크한다. 널인 경우 지정된 값으로 리턴한다.
     * */
	public static String nvl(Object obj){
		return nvl(String.valueOf(obj), "");
	}

	public static String nvl(Object obj, String defaultStr) {
		return nvl(String.valueOf(obj), defaultStr);
	}

	public static String nvl(String str){
		return nvl(str,"");
	}

	public static String nvl(String str,String defaultStr){
		return isNvl(str) ? defaultStr  : str.trim();
	}

	public static String toCamelCase(String str){

		String strLower 	= SUtils.nvl(str);

		if(!strLower.equals("") || strLower.contains("_")){

			StringBuilder sb	= new StringBuilder();
			boolean nextUpper	= false;

			for(char c : strLower.toCharArray()){
				if(c == '_'){
					nextUpper	= true;

				}else{
					sb.append(nextUpper ? Character.toUpperCase(c) : c);
					nextUpper	= false;
				}
			}
			return sb.toString();

		}else{
			return str;
		}
	}


    /**
     * 문자열이 null ? true : false
     */
    public static boolean isNvl(Object obj){
        return isNvl(String.valueOf(obj));
    }

    public static boolean isNvl(String str){
        return (str == null || str.equals("null") || str.trim().length() <= 0);
    }

    /**
     * String 형 변환
     */
	public static String intToStr(int i){
		return String.valueOf(i);
	}

	public static int strToInt(Object str) {
		return strToInt(str,0);
	}

    public static int strToInt(Object str, int def) {
        try{
            return Integer.parseInt(nvl(str));
        }catch(NumberFormatException e){
            return def;
        }
    }

    public static Long strToLong(Object str) {
    	return strToLong(str, (long) 0);
    }

    public static Long strToLong(Object str, Long def) {
    	try{
    		return Long.parseLong(nvl(str));
    	}catch(NumberFormatException e){
    		return def;
    	}
    }

    /**
     * 휴대번호 정규식
     */
    public String phoneformat(String phone) {
        if (phone == null) return "";
        return phone.replaceAll("(\\d{3})(\\d{4})(\\d{4})", "$1-$2-$3");
    }

	/**
	 * 파일 경로 졍규화
	 * */
	public static String normalizePath(String path) {
	    String os = System.getProperty("os.name").toLowerCase();
	    return os.contains("win") ? path.replace("/", "\\") : path.replace("\\", "/");
	}

    public static String toDate(String yyyymmdd) {
        if(yyyymmdd == null || yyyymmdd.length() != 8){
            return yyyymmdd; // 그대로 반환
        }
        return yyyymmdd.replaceAll("(\\d{4})(\\d{2})(\\d{2})", "$1-$2-$3");
    }
}
