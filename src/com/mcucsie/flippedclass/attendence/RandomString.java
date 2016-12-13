package com.mcucsie.flippedclass.attendence;

public class RandomString {
	public String randomString(int len) { 
		String str = "0123456789abcdefghijklmnopqrstuvwxyz"; 
		StringBuffer sb = new StringBuffer(); 
		for (int i = 0; i < len; i++) { 
			int idx = (int)(Math.random() * str.length()); 
			sb.append(str.charAt(idx)); 
		} 
	return sb.toString(); 
	} 
	
	
}
