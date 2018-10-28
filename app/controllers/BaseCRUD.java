package controllers;

import java.util.List;


import models.Ndf;
import models.User;
import play.Play;
import play.mvc.Before;
import play.mvc.With;

@With(Secure.class)
public class BaseCRUD extends CRUD {
	
	@Before
	static void setConnectedUser() {
		if (Security.isConnected()) {
			User user = User.find("byLogin", Security.connected()).first();
			renderArgs.put("user", user);
			if (null != user) {
				List<Ndf> lastNdfs = Ndf.find("user = :user order by ndfDate desc").bind("user", user).fetch(3);
				renderArgs.put("lastNdfs", lastNdfs);
			}
		}
	}
	
	@Before
	static void addDefaults() {
		renderArgs.put("ndfTitle", Play.configuration.getProperty("ndf.title"));
		renderArgs.put("ndfBaseLine", Play.configuration.getProperty("ndf.baseline"));
		
	}
	
}
