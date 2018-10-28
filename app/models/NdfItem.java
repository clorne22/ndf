package models;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.gson.annotations.Expose;

import play.Logger;
import play.data.binding.Binder;
import play.data.validation.Max;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="t_ndf_item")
public class NdfItem extends Model implements Comparable<NdfItem> {

	public NdfItem() {
	}
	public NdfItem(Ndf ndf) {
		this.ndf = ndf;
	}
	

	@Required
	@Max(value = 31d)
	@Transient
	public int dayNumber = 0;
	
	public void setDayNumber(int dayNumber) {
		this.dayNumber = dayNumber;
		if (dayNumber > 0) {
			initializeDate();
		} else {
			itemDate = null;
		}
	}

	public int getDayNumber() {
		if (0 == dayNumber && itemDate != null) {
			dayNumber = Integer.parseInt(new SimpleDateFormat("dd").format(itemDate));
		}
		return dayNumber;
	}
	
	
	
	@Column(name="ndf_item_date")
	@Required(message = "ndfitem.validation.required")
	public Date itemDate;
	
	@Expose
	public String place;
	
	@Column(name="ndf_item_object")
	public String itemObject;
	
	@Expose
	public String trip;
	
	@Column(name="ndf_item_km_price")
	public double ndfItemKmPrice = 0d;
	public Double getNdfItemKmPriceAsObject() {
		return (0d == ndfItemKmPrice) ? null : ndfItemKmPrice;
	}

	@Column(name="ndf_item_trip_price")
	public double ndfItemTripPrice = 0d;
	
	public Double getNdfItemTripPriceAsObject() {
		return (0d == ndfItemTripPrice) ? null : ndfItemTripPrice;
	}
	
	@Expose
	public int km = 0;
	
	@Required
	@ManyToOne
	@JoinColumn(name="ndf_id")
	public Ndf ndf;
	
	@ManyToOne
	@JoinColumn(name="business_id")
	public Business business;
	
	
	public String getTrajetObjet() {
		if (null != business) {
			String trajetObjet = business.label;
//			if (itemObject != null && !itemObject.equals(business.label)) {
//				trajetObjet += " - " + itemObject;
//			}
			return trajetObjet;
		}
		return null;
	}
	
	public void initializeDate() {
		String sDate = String.format("%s/%s", String.valueOf(dayNumber), new SimpleDateFormat("MM/yyyy").format(ndf.ndfDate));
		Logger.info("Initialisation de la date avec %s", sDate);
		try {
			itemDate = (Date) Binder.directBind(sDate, Date.class);
		} catch (Exception e) {
			Logger.error("Impossible de convertir la date %s", sDate);
			itemDate = null;
		}
	}
	
	public int compareTo(NdfItem o) {
		if (o.itemDate.equals(itemDate)) {
			return o.business.number.compareTo(business.number);
		}
		return o.itemDate.compareTo(itemDate);
	}
	
	public NdfItem duplicate(int dayNumber) {
		NdfItem n = new NdfItem(this.ndf);
		
		n.dayNumber = dayNumber;
		n.place = this.place;
		n.itemObject = this.itemObject;
		n.trip = this.trip;
		n.ndfItemKmPrice = this.ndfItemKmPrice;
		n.ndfItemTripPrice = this.ndfItemTripPrice;
		n.km = this.km;
		n.business = this.business;
		
		return n;	
	}
}
