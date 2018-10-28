package controllers;

import controllers.Secure;
import models.User;

public class Security extends Secure.Security {
	
	/**
	 * Authentication.
	 * @param username The login
	 * @param password The password
	 * @return True if authentication succeded
	 */
	static boolean authentify(String username, String password) {
		return (User.connect(username, password) != null);
	}

	/**
	 * Authorization management.
	 */
	static boolean check(String profile) {
		if("admin".equals(profile)) {
	        return User.find("byLogin", connected()).<User>first().administrator;
	    }
	    return false;
    }
}
