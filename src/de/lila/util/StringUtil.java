package de.lila.util;

public class StringUtil {
	
	public static String collapseArray(String[] arr, int from, int to) {
		String s = "";
		
		for(int i=from; i<=to; i++) {
			s = s + arr[i];
			
			if(i < to) s = s + " ";
		}
		
		return s;
	}
	
}
