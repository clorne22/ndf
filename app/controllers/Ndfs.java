package controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Future;

import play.Logger;
import play.cache.Cache;

import jobs.ReportAsPDFJob;
import models.Ndf;
import models.helpers.Utils;

public class Ndfs extends BaseController {

	/**
	 * Affichage d'une note de frais particulière
	 * @param id
	 */
	public static void show(Long id) {
		Logger.debug("id = %s", id);
		session.put("ndfItemId", id);
		
		if (null == id) {
			String[] months = Utils.getMonths();
			render("ndfs/create.html", months);
		} else { // Affichage d'une note de frais existante
			NdfItems.list();
		}
	}
	
	/**
	 * Liste des notes de frais de l'utilisateur connecté.
	 */
	public static void listNdfs() {
		
		List<Ndf> ndfs = Ndf.getAllNdf(getConnectedUser());
		render("ndfs/older.html", ndfs);
		
	}
	
	/**
	 * Création d'une nouvelle note de frais. Un message d'erreur est affiché si
	 * une note de frais correspondant à la date fournie existe déjà.
	 * @param year L'année
	 * @param monthLe mois
	 * @throws ParseException
	 */
	public static void create(String year, String month)
		throws ParseException {

		// On calcule la date
		//
		Date date = new SimpleDateFormat("MMMM yyyy", Locale.FRANCE).parse(
				String.format("%s %s", month, year)
				);
		Logger.debug("year = %s; month = %s; date = %s", year, month, date);
		
		Ndf ndf = new Ndf();
		if (Ndf.exists(date)) {
			flash.error("Cette note de frais existe déjà.");
		} else {
			ndf.ndfDate = date;
			ndf.user = getConnectedUser();
			ndf = ndf.save();
			
			flash.success("Note de frais de %s %s créée.", month, year);
		}
		
		show(ndf.id);
	}
	
	
	public static void generatePDF(Long id)
		throws Exception {
		
		if (request.isNew) {
			
			// builds our JR provider
			//
			Ndf ndf = Ndf.findById(id);			
			Logger.info("Génération de la note de frais %s de %s", ndf, ndf.user);
			Future<ByteArrayOutputStream> task = new ReportAsPDFJob(ndf, params.allSimple()).now();
			request.args.put("task", task);
			
			// asynchronous execution of the provider
			//
			waitFor(task);
		}
		
		// gets the created ended task
		//
		Future<ByteArrayOutputStream> task = (Future<ByteArrayOutputStream>) request.args.get("task");
		ByteArrayOutputStream os = task.get();
		
		// converts the outputstream to an inputstream to render
		//
		response.contentType = "application/pdf";
		
		InputStream is = new ByteArrayInputStream(os.toByteArray());
		renderBinary(is);
	}
}
