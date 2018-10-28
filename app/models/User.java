package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.oval.constraint.ValidateWithMethod;

import play.Logger;
import play.data.validation.Password;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="t_user")
public class User extends Model {

	@Column(name="first_name")
	public String firstName;
	
	@Column(name="last_name")
	public String lastName;
	
	@Required
	public String login;
	
	@Password @Required
	public String password;

	@Column(name="administrator")
	public boolean administrator;
	
	@Column(name="peage")
	public boolean peage;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="user")
	public List<Ndf> ndfs;
	
	/**
	 * Constructeur par défaut
	 */
	public User() {
		
	}
	public User(String login, String password) {
		this.login = login;
		this.password = password;
		this.ndfs = new ArrayList<Ndf>();
	}
	
	
	@Override
	public String toString() {
		return firstName + " " + lastName;
	}


	public static User connect(String login, String password) {
		User user = find("byLoginAndPassword", login, password).first();
		Logger.info("Connexion de %s --> %s", login, ((null == user) ? "ECHEC" : "OK"));		
		return user;
	}
	
}
