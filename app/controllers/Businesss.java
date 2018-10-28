package controllers;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;

import ext.Constantes;
import ext.MyRenderJson;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;
import models.Business;
import play.Logger;
import play.libs.WS;
import play.libs.WS.HttpResponse;
import play.libs.XML;
import play.libs.XPath;
import play.mvc.With;

@Check("admin")
@With(Secure.class)
public class Businesss extends BaseCRUD {

	public static void getDefaultValues(Long id) {
		Business business = (Business) Business.findById(id);
		Logger.debug("Recherche des valeurs par défaut pour l'affaire %s", business);
		
		//Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		//JsonSerializer<Business> serializer = gson.;
		Logger.info("valeurs par défaut de l'affaire %s", business);
		renderMyJSON(business/*, serializer*/);
	}
	
	public static void getTripDefaultValues(String destination) {
		Business business = new Business("", "");
		if (destination != null && !destination.isEmpty()) {
			business.place = destination.toUpperCase();
			business.trip = Constantes.getDefaultTrip(business.place);
			
			// récupération du kilométrage
			//
			business.km = Constantes.getDistance(business.place);
		}
		renderMyJSON(business);
	}
	
	public static void showUpload() {
		render();
	}
	
	public static void upload(File fileAffaires) throws BiffException, IOException {
		notFoundIfNull(fileAffaires);
		
		Logger.info("Chargement de la liste d'affaires...");
		
		// lecture du fichier Excel
		//
		WorkbookSettings ws = new WorkbookSettings();
		ws.setEncoding("ISO-8859-15"); // encoding par défaut 
		
		Workbook workbook = Workbook.getWorkbook(fileAffaires, ws);
		Sheet sheet = workbook.getSheet(0); // premier onglet
		
		for (int i = 0; i < sheet.getRows(); i++) {
			
			if (i == 0) { // titre des colonnes
				continue;
			}

			// on ne rapatrie que les affaires qui ne sont pas archivées
			//
			String archivage = sheet.getCell(6, i).getContents();
			if ("A".equals(archivage)) {
				continue;
			}

			// contrôle qu'il y a bien un libellé d'affaire
			//
			String label = sheet.getCell(3, i).getContents();
			if (label.isEmpty()) { 
				continue;
			}

			String number = sheet.getCell(2, i).getContents();
			
			Business business = Business.findByNumber(number);
			if (business == null) {
				
				business = new Business(number, label);
				business.save();
			}
			
		}
		
		Logger.info("Chargement de la liste d'affaires réussie");

		flash.success("Success " + fileAffaires.getAbsolutePath());
		showUpload();
	}
	
    protected static void renderMyJSON(Object o) {
        throw new MyRenderJson(o);
    }
	
}
