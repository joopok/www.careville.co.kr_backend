package kr.co.cleaning.core.config;

import java.io.BufferedReader;
import java.io.Reader;
import java.sql.Clob;
import java.util.HashMap;

import kr.co.cleaning.core.utils.SUtils;

public class HashMapCamel extends HashMap {

	private static final long serialVersionUID = 6723434363565852261L;

	@Override
	public Object put(Object key, Object value) {

		String camelKey = toCamelCase(SUtils.nvl(key).toLowerCase());

		if(value instanceof java.sql.Clob){				// CLOB to String

			try(
				Reader reader 		= ((Clob)value).getCharacterStream();
				BufferedReader br	= new BufferedReader(reader);
				){

				StringBuffer strOut = new StringBuffer();
				String str			= "";

				while((str = br.readLine()) != null){
					strOut.append(str).append("\n");
				}

				value = strOut.toString();

			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return super.put(camelKey, value);
	}

	private String toCamelCase(String str) {

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
}