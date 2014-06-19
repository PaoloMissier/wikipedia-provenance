package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/*
 * Class for consuming and producing ISO8601 Date format strings modelled off 
 * http://stackoverflow.com/questions/2201925/converting-iso8601-compliant-string-to-java-util-date
 * 
 * Will only work exclusively with UTC timezone - forces the Z at the end the string representation
 * 
 */

	public final class ISO8601 {
		
		private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
		
	    /** Transform Calendar to ISO 8601 string. */
	    public static String fromCalendar(final Calendar calendar) {
	    	calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
	        Date date = calendar.getTime();
	        String formatted = dateFormat.format(date);
	        String result = formatted.substring(0, 22) + ":" + formatted.substring(22); 
	               result = result.replace("+00:00", "Z");
	               result = result.replace("+01:00", "Z");
	        return result;
	    }

	    /** Get current date and time formatted as ISO 8601 string. */
	    public static String now() {
	        return fromCalendar(GregorianCalendar.getInstance());
	    }

	    /** Transform ISO 8601 string to Calendar. */
	    public static Calendar toCalendar(final String iso8601string)
	            throws ParseException {
	        Calendar calendar = GregorianCalendar.getInstance();
	        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
	        String s = iso8601string.replace("Z", "+00:00");
	        try {
	            s = s.substring(0, 22) + s.substring(23);
	        } catch (IndexOutOfBoundsException e) {
	            throw new ParseException("Invalid length", 0);
	        }
	        Date date = dateFormat.parse(s);
	        calendar.setTime(date);
	        return calendar;
	    }
	
}
