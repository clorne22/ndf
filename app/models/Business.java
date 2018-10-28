package models;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.junit.Ignore;

import com.google.gson.annotations.Expose;

import play.Logger;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="t_business")
public class Business extends Model {
	

	public static Business findByNumber(String number) {
		if (null == number) {
			throw new IllegalArgumentException("number");
		}
		return find("byNumber", number).first();
	}
	

	@Required
	@Expose
	public String number;
	
	@Required
	@Expose
	public String label;
	
	@Expose
	public String place;

	@Expose
	public String trip;
	
	@Column(name="trip_object")
	@Expose
	public String tripObject;
	
	@Expose
	public int km = 0;
	
	@Column(name="trip_price")
	@Expose
	public double tripPrice = 0;
	
	@OneToMany(mappedBy="business")	
	public Set<NdfItem> items; // transient is for JSON serialisation
	
	/**
	 * Constructeur standard.
	 */
	public Business() {
		
	}
	
	public Business(String number, String label) {
		this.number = number;
		this.label = label;
		this.items = new HashSet<NdfItem>();
	}

	@Override
	public String toString() {
		return number + " " + label;
	}
	
	public void initializeWithNdfItem(NdfItem item) {
		Logger.debug("Initialisation de l'affaire %s avec l'item %s", number, item.id);
		this.place = item.place;
		this.label = item.itemObject;
		this.trip = item.trip;
		this.tripPrice = item.ndfItemTripPrice;
		this.km = item.km;
	}
}
