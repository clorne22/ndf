package ext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import play.templates.JavaExtensions;


public class CustomExtension extends JavaExtensions {

	public static String format(Date date, String pattern) {
		if (null == pattern || "".equals(pattern))
			return new SimpleDateFormat("dd/MM/yyyy").format(date);
		return new SimpleDateFormat(pattern).format(date);
    }
	public static String formatCcy(Number number, String pattern) {
        return new DecimalFormat("##,##0.00").format(number) + " €";
    }
	
}
