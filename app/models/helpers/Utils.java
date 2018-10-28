package models.helpers;

import java.text.DateFormatSymbols;
import java.util.Locale;
import java.util.NoSuchElementException;

import play.Logger;

/**
 * 
 * @author Christian christian.lorne.pro@gmail.com
 *
 */
public class Utils {

	/**
	 * 
	 * @param number Le numéro de mois.
	 * @return Renvoie en français le nom du mois en fonction du numéro de mois fourni
	 */
	public static String getMonthLabel(int number) {
		return getMonths()[number - 1];
	}
	
	
	public static String[] getMonths() {
		return new DateFormatSymbols(Locale.FRANCE).getMonths();
	}

	/**
	 * 
	 * @param monthLabel Le label du mois.
	 * @return Renvoie le numéro du mois de 0 à 11 en fonction du label du mois fourni
	 */
	public static int getMonthNumber(String monthLabel) {
		Logger.debug("Traitement du mois %s", monthLabel);
		assert null != monthLabel;
		String[] months = new DateFormatSymbols(Locale.FRANCE).getMonths();
		for (int i = 0; i < months.length; i++) {
			if (monthLabel.equals(months[i])) {
				return i;
			}
		}
		throw new NoSuchElementException();
	}
}
