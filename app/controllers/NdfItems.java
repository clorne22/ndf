package controllers;

import java.util.List;

import ext.Constantes;
import ext.MyRenderJson;

import play.data.binding.Binder;
import play.exceptions.TemplateNotFoundException;
import play.Logger;

import models.Business;
import models.Ndf;
import models.NdfItem;

public class NdfItems extends BaseCRUD {
	
	private static Ndf getActiveNDF() {
		Long ndfId = Long.valueOf(session.get("ndfItemId"));
		Ndf ndf = Ndf.findById(ndfId);
		return ndf;
	}

	public static void list() {
		
		Ndf ndf = getActiveNDF();
		List<NdfItem> objects = ndf.items;
		render(ndf, objects);
	}
	
	public static void blank() throws Exception {
		NdfItem object = new NdfItem(getActiveNDF()); 
		render(object);
	}
	
	public static void create() throws Exception {
        ObjectType type = ObjectType.get(getControllerClass());
		NdfItem object = new NdfItem();
        Binder.bindBean(params.getRootParamNode(), "object", object);
        validation.valid(object);
        if (validation.hasErrors()) {
            renderArgs.put("error", play.i18n.Messages.get("crud.hasErrors"));
            try {
                render(request.controller.replace(".", "/") + "/blank.html", type, object);
            } catch (TemplateNotFoundException e) {
                render("CRUD/blank.html", type, object);
            }
        }
        object._save();
        flash.success(play.i18n.Messages.get("crud.created", type.modelName));
		
		if (params.get("_saveAndUpdateBusiness") != null) {
			// update the values for the business for the next time
			Business business = object.business;
			
			business.initializeWithNdfItem(object);
			business.save();
			redirect(request.controller + ".list");
		}
		
        if (params.get("_save") != null) {
            redirect(request.controller + ".list");
        }
        if (params.get("_saveAndAddAnother") != null) {
            redirect(request.controller + ".blank");
        }
        redirect(request.controller + ".show", object._key());
    }

	
	public static void duplicate(Long id) throws Exception {
		NdfItem toDuplicate = NdfItem.findById(id);
		Logger.debug("Duplication de l'item id = %s; n° affaire = %s", id, toDuplicate.business.number);
		
		NdfItem newItem = toDuplicate.duplicate(1);
		newItem.save();
		
		list();
	}
	
	public static void getDefaultValuesForPlace(String place) {
		place = place.toUpperCase();
		Logger.debug("Recherche des trajets par défaut pour la destination %s", place);
		
		NdfItem item = new NdfItem(null);
		
		if (place != null && !place.isEmpty()) {
			item.place = place;
			item.trip = Constantes.getDefaultTrip(place);
			
			if ("MARSEILLE".equalsIgnoreCase(place)) {
				item.km = 70;
			} else {			
				item.km = Constantes.getDistance(place);
			}
		}
		renderMyJSON(item/*, serializer*/);
	}
	
    protected static void renderMyJSON(Object o) {
        throw new MyRenderJson(o);
    }

}
