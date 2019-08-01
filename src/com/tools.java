package com;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

class tools{
	
	public static String queryMicroservice(String msURL) throws Exception{
		URL url = new URL(msURL);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(50);
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer content = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			content.append(inputLine);
		}
		in.close();
		con.disconnect();
		return content.toString();
	}
	/*
	 * trim the out most useless parentheses and return a char array
	 */
	public static char[] toCharArrayTrimOutParenthes(String src) {

		if (src.length() == 0) {
			return null;
		}
		String result = src;
		while (result.charAt(0) == '(' && result.charAt(result.length() - 1) == ')') {
			if(result.length() == 3) {
				result = result.substring(1, result.length() - 1);
				return result.toCharArray();
				//System.out.println("label"+result);
			}
			int parenthes = 0;
			for (int i = 0; i < result.length() - 1; i++) {
				if (result.charAt(i) == '(') {
					parenthes++;
				} else if (result.charAt(i) == ')') {
					parenthes--;
				}
				if (parenthes == 0) {
					return result.toCharArray();
				}
			}
			result = result.substring(1, result.length() - 1);

		}

		return result.toCharArray();
	}
	
	public static boolean isOperation(char c) {
		if(c=='-' || c=='*') {
			return true;
		}else {
			return false;
		}
	}
	
	public static boolean hasOperation(char[] cArray) {
		for (int i = 0; i < cArray.length; i++) {
			if (isOperation(cArray[i])) {
				return true;
			}

		}
		return false;
	}
}