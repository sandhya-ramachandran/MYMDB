package org.sandhya.MyMDB.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyMDBHelper {
	
	public  static SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");

	public static boolean isEmpty(Object inp)
	{
		boolean rev =  ((inp == null) || (inp.toString().trim().equals("")) );
		return rev;
	}
	
	public static int returnInt(String inStr) {
		try {
			return Integer.parseInt(inStr);
		} catch (Exception ex) {
		}
		return 0;
	}
	
	public static boolean isNotEmpty(Object obj) {

		if (obj != null && !obj.toString().trim().equals("")) {
			return true;
		}
		return false;
	}
	
	public static Date parseDate(String input) throws Exception
	{
		return sdf.parse(input);
	}
	
	public static void main(String args[]) throws Exception {
		final SimpleDateFormat sdf1 = new SimpleDateFormat("dd MMM yyyy");
		System.out.println(sdf1.parse("20 Oct 2015"));
	}
	
}
