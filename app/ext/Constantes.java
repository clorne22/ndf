package ext;
import models.Business;

import org.w3c.dom.Document;

import play.Logger;
import play.libs.WS;
import play.libs.XPath;
import play.libs.WS.HttpResponse;


public final class Constantes {
	public final static String VILLE_ORIGINE = "AIX EN PROVENCE";

	public static int getDistance(String to) {
		int distance = 0;
		String url = "http://maps.googleapis.com/maps/api/distancematrix/xml?language=fr-FR&sensor=false&origins=%s&destinations=%s";
		HttpResponse response = WS.url(url, VILLE_ORIGINE, to).get();
		if (response.getStatus() == 200) { // OK
			Document document = response.getXml();
			String d = XPath.selectText("//row/element/distance/value", document);
			Logger.debug("d=%s", d);
			distance = (Integer.parseInt(d) / 1000) * 2;
			Logger.info("On a trouvé un nombre de kilomètres pour %s - %s = %s via Google", VILLE_ORIGINE, to, distance);
		}
		return distance;
	}
	
	public static String getDefaultTrip(String to) {
		return VILLE_ORIGINE  + " - " + to + " AR";
	}
}
