/**
 * 
 */
package controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.oval.constraint.AssertTrue;

import models.Business;
import models.Ndf;
import models.Ndf.YearStatisticBuilder;

import play.Logger;
import play.mvc.With;

/**
 * @author Christian
 *
 */
@With(Secure.class)
public class StatisticsController extends BaseController {
	
	
	/**
	 * Renvoie les statistiques des kilomètres parcourus par mois et par affaire.
	 */
	public static void mine(int year) {
		
		if (year == 0) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			year = cal.get(Calendar.YEAR) - 1;
		}
		Logger.info("Calcul des statistiques sur l'année %s", year);
		
		YearStatisticBuilder stat = new Ndf.YearStatisticBuilder(year);
		render("application/statistics.html", stat);
	}
	
}
