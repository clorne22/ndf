package controllers;

import java.util.List;

import com.google.gson.Gson;

import models.Business;
import models.Km;
import models.User;
import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Required;
import play.data.validation.Valid;
import play.mvc.With;

@With(Secure.class)
public class Application extends BaseController {

	/**
	 * Page principale de l'application.
	 */
    public static void index() {
    	render();
    }

    /**
     * Liste des frais kilométriques.
     */
    @Check("admin")
    public static void kms() {
    	List<Km> kms = Km.find("order by startDate desc").fetch();
    	render("application/kms.html", kms);
    }

    /**
     * Liste des utilisateurs.
     */
    @Check("admin")
    public static void users() {
    	List<User> users = User.findAll();
    	render("application/users.html", users);
    }

    /**
     * Liste des affaires.
     */
    @Check("admin")
    public static void businesses() {
    	List<Business> bs = Business.find("order by number desc").fetch();
    	render("application/businesses.html", bs);
    }
    
    @Check("admin")
    public static void bs(int page, int rows) {
    	
    	Gson gson = new Gson();
    	//gson.
    	
    	List<Business> bs = Business.find("order by number desc").fetch();
    }

    /**
     * Affichage d'un km.
     * @param id Identifiant du km à afficher
     */
    @Check("admin")
    public static void showKm(Long id) {
    	Km km = ((id == null) ? new Km() : (Km) Km.findById(id));
    	
    	Logger.info("Affichage du km %s", km);
    	render("application/showkm.html", km);
    }
    
    /**
     * Affichage d'un utilisateur.
     * @param id Identifiant de l'utilisateur à afficher
     */
    @Check("admin")
    public static void showUser(Long id) {
    	User user = ((id == null) ? new User() : (User) User.findById(id));
    	
    	Logger.info("Affichage de l'utilisateur %s", user);
    	render("application/showuser.html", user);
    }
    
    /**
     * Affichage d'une affaire.
     * @param id Identifiant de l'affaire à afficher
     */
    @Check("admin")
    public static void showBusiness(Long id) {
    	Business b = ((id == null) ? new Business() : (Business) Business.findById(id));
    	
    	Logger.info("Affichage de l'affaire %s", b);
    	render("application/showbusiness.html", b);
    }
    
    /**
     * Suppression d'un km.
     * @param id Identifiant du km à supprimer.
     */
    @Check("admin")
    public static void deleteKm(@Required Long id) {

    	Km km = Km.findById(id);
    	if (!km.ndfs.isEmpty()) {
			flash.error("Ce km est dejà référencé par une note de frais. Suppression impossible.");
			kms();
		}
    	
    	Logger.info("Suppression du frais kilométrique %s", km.id);
    	km.delete();
    	
    	kms();
    }

    /**
     * Suppression d'un utilisateur.
     * @param id Identifiant de l'utilisateur à supprimer.
     */
    @Check("admin")
    public static void deleteUser(@Required Long id) {

    	User user = User.findById(id);
    	Logger.info("Suppression de l'utilisateur %s", user.id);
    	user.delete();
    	
    	users();
    }

    /**
     * Suppression d'une affaire.
     * @param id Identifiant de l'affaire à supprimer.
     */
    @Check("admin")
    public static void deleteBusiness(@Required Long id) {

    	Business business = Business.findById(id);
    	if (!business.items.isEmpty()) {
			flash.error("Cette affaire est déjà utilisée dans des frais kilométriques. Suppression impossible.");
			businesses();
		}
    	
    	Logger.info("Suppression de l'affaire %s", business.id);
    	business.delete();
    	
    	businesses();
    }

    /**
     * Sauvegarde d'un nouveau prix kilométrique.
     * @param km Le nouveau prix avec ses dates
     */
    public static void saveKm(@Valid Km km) {
    	if (validation.hasErrors()) {
        	Logger.debug("Sauvegarde km a des erreurs");
    		params.flash();
    		//flash.error("Impossible de sauvegarder l'objet km...");
    		validation.keep();
    		Logger.debug("%s", validation.errorsMap());
			showKm(km.id);
		}

    	Logger.info("Sauvegarde du km %s", km);
    	km = km.save();
    	flash.success("Prix du kilomètre sauvegardé");
    	
    	kms();
    }

    /**
     * Sauvegarde d'un nouvel utilisateur.
     * @param km Le nouvel à utilisateur (ou mise à jour d'un existant)
     */
    public static void saveUser(@Valid User user) {
    	if (validation.hasErrors()) {
        	Logger.debug("Sauvegarde utilisateur a des erreurs");
    		params.flash();
    		validation.keep();
    		Logger.debug("%s", validation.errorsMap());
			showUser(user.id);
		}

    	Logger.info("Sauvegarde de l'utilisateur %s", user);
    	user = user.save();
    	
    	flash.success("Utilisateur '%s' sauvegardé", user.login);
    	
    	users();
    }
    
    /**
     * Sauvegarde d'une affaire.
     * @param km Le nouveau prix avec ses dates
     */
    public static void saveBusiness(@Valid Business b) {
    	if (validation.hasErrors()) {
        	Logger.debug("Sauvegarde de l'affaire a des erreurs");
    		params.flash();
    		validation.keep();
    		Logger.debug("%s", validation.errorsMap());
			showBusiness(b.id);
		}

    	Logger.info("Sauvegarde de l'affaire %s", b);
    	b = b.save();
    	flash.success("Affaire sauvegardée");
    	
    	businesses();
    }

    
}