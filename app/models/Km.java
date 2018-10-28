package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import net.sf.oval.constraint.NotNegative;
import net.sf.oval.constraint.ValidateWithMethod;
import play.Logger;
import play.data.binding.As;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.db.jpa.Model;

@Entity
@Table(name="t_km")
@ValidateWithMethod(
		methodName="hasConflict"
		, parameterType=Km.class
		, errorCode="ndf.km.duplicate.dates"
		, message="ndf.km.duplicate.dates"
)
public class Km extends Model {

	@Column(name="start_date")
	@Required
	public Date startDate;
	
	@Column(name="end_date")
	@Required
	@CheckWith(message="ndf.km.enddate", value=EndDateCheck.class)
	public Date endDate;
	
	@Required
	@NotNegative
	public double price;
	
	@OneToMany(mappedBy="km")
	public List<Ndf> ndfs = new ArrayList<Ndf>();
	
	
	public Km() {
	}

	static class EndDateCheck extends Check {
		@Override
		public boolean isSatisfied(Object validatedObject, Object value) {
			Km km = (Km) validatedObject;
			if (null == km.startDate || null == value) {
				return false;
			}
			
			return km.startDate.before((Date) value);
		}
	}
	
	@SuppressWarnings("unused")
	private boolean hasConflict(Km kmb) {

		Logger.debug("start=%s; end=%s", kmb.startDate, kmb.endDate);
		
		if (kmb.startDate == null || kmb.endDate == null) {
			return false;
		}
				
		List<Km> kms = find("startDate <= ? and endDate >= ?"
				, kmb.startDate, kmb.endDate)
				.fetch();
		kms.remove(kmb);
		return kms.isEmpty();
		
		
//		List<Km> kms = Km.findAll();
//
//		for (Km km : kms) {
//			if (null != kmb.getId() && kmb.getId() == km.getId())
//				continue;
//			
//			if ((km.startDate.before(kmb.endDate) && km.endDate.after(kmb.endDate)) 
//					|| (km.startDate.before(kmb.startDate) && km.endDate.after(kmb.startDate))) {
//				return false;
//			}
//		}
		
//		return true;
		
	}

	
}
