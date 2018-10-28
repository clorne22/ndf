package models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import models.helpers.Utils;
import models.report.IJRReportProvider;
import models.report.JRUtils;
import models.report.MyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.oval.constraint.ValidateWithMethod;
import play.Logger;
import play.data.binding.As;
import play.data.validation.Check;
import play.data.validation.CheckWith;
import play.data.validation.Required;
import play.templates.JavaExtensions;
import ext.CustomExtension;

@Entity
@Table(name="t_ndf")
public class Ndf extends play.db.jpa.Model implements IJRReportProvider {

	@Required
	@Column(name="ndf_date")
	@CheckWith(value=NdfDateCheck.class, message="error.ndf.validdates")
	public Date ndfDate;
	public void setNdfDate(Date ndfDate) {
		this.ndfDate = ndfDate;
		if (null != this.ndfDate) {
			this.km = Km.find("startDate <= :date and endDate >= :date").bind("date", this.ndfDate).first();
		}
	}

	public Date visa, payment;
	
	@Required
	@ManyToOne
	@JoinColumn(name="km_id")
	public Km km;	
	
	@Required
	@ManyToOne
	@JoinColumn(name="user_id")
	public User user;
	
	@OneToMany(cascade=CascadeType.ALL, mappedBy="ndf")
	@OrderBy(value="itemDate, business")
	public List<NdfItem> items = new ArrayList<NdfItem>();
	
	public Ndf(Date date) {
		this.ndfDate = date;
	}
	public Ndf() {
		
	}
	
	public Calendar getDateAsCalendar() {
		assert null != this.ndfDate;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.ndfDate);
		
		return cal;
	}
	
	/**
	 * 
	 * @param user L'utilisateur connecté.
	 * @return Renvoie la liste des toutes les notes de frais de l'utilisateur connecté
	 * ordonnée par date décroissante.
	 */
	public static List<Ndf> getAllNdf(User user) {
		return Ndf.find("user = ? order by ndfDate desc", user).fetch();
	}
	
	public static boolean exists(Date date) {
		return null != Ndf.find("ndfDate = ?", date).first();
	}
	
	/**
	 * 
	 * @return Renvoie l'année de la note de frais.
	 */
	public int getNdfYear() {
		return this.getDateAsCalendar().get(Calendar.YEAR);
	}
	
	public String getDateLabel() {
		return new SimpleDateFormat("MMMM yyyy", Locale.FRANCE).format(this.ndfDate);
	}
	
	
	static class NdfDateCheck extends Check {
		
		@Override
		public boolean isSatisfied(Object validatedObject, Object ndfDate) {
			Ndf ndf = (Ndf) validatedObject;
			if (null == ndf.ndfDate) {
				return false;
			}
			
			// by default its the first day of the month
			Date theDate = ndf.ndfDate;

			Calendar calStart = Calendar.getInstance();
			calStart.setTime(theDate);
			calStart.set(Calendar.DATE, calStart.getActualMinimum(Calendar.DATE));
			
			Calendar calEnd = Calendar.getInstance();
			calEnd.setTime(theDate);
			calEnd.set(Calendar.DATE, calEnd.getActualMaximum(Calendar.DATE));
			
			Logger.info("user: %s", ndf.user);
			
			boolean ret = find("select n from Ndf n where n.user=:user and n.ndfDate >= :ndfDateStart and n.ndfDate <= :ndfDateEnd")
				.bind("user", ndf.user)
				.bind("ndfDateStart", calStart.getTime())
				.bind("ndfDateEnd", calEnd.getTime())
				.fetch().isEmpty();
			
			Logger.debug("NdfDateCheck.isSatisfied ret=" + ret + "; user: " + ndf.user + "; ndf.ndfDate: " + ndf.ndfDate);
			return ret;
		}
		
	}
	
	
	@Override
	public String toString() {
		return (null == ndfDate) ? super.toString() : (JavaExtensions.format(ndfDate, "MMMM yyyy"));
	}


	public double getTotalPrice() {
		double total = 0d;
		for (NdfItem item : items) {
			total += item.ndfItemTripPrice + item.ndfItemKmPrice;
		}
		return total;
	}


	public MyDataSource getJRDataSource(Map<String, String> params)
		throws JRException {
		
		// creates the datasource object
		//
		MyDataSource ds = new MyDataSource(items);
		
		// generates the parameters
		//
		Map parameters = new HashMap();
		parameters.put("salarie", user.toString());
		parameters.put("mois", "Mois " + CustomExtension.format(ndfDate, "MMMM yyyy"));
		parameters.put("deplacementsSubReport", JRUtils.getJasperReport("ndf_deplacements.jrxml"));
		parameters.put("ventilAnalytiqueSubReport", JRUtils.getJasperReport("ndf_ventil_analytique.jrxml"));
		parameters.put("trajetsSubReport", JRUtils.getJasperReport("ndf_trajets.jrxml"));
		parameters.put("ventilKmsSubReport", JRUtils.getJasperReport("ndf_ventil_kms.jrxml"));
		
		// génération de la source de données pour les frais de déplacement
		List<NdfItem> deplacements = new ArrayList<NdfItem>();
		for (NdfItem ndfItem : items) {
			if (0d != ndfItem.ndfItemTripPrice) {
				deplacements.add(ndfItem);
			}
		}
		// on complète jusqu'à 25 éléments
		if (25 > deplacements.size()) {
			for (int i = deplacements.size(); i < 25; i++) {
				deplacements.add(new NdfItem(this));
			}
		}
		parameters.put("deplacementsDataSource", new MyDataSource(deplacements));
		
		// génération de la ventilation analytique
		List<VentilAnalytique> ventils = new ArrayList<VentilAnalytique>();
		for (NdfItem ndfItem : items) {
			VentilAnalytique ventil = new VentilAnalytique(ndfItem.business);
			if (ventils.contains(ventil)) {
				ventil = ventils.get(ventils.indexOf(ventil));
			} else {
				ventils.add(ventil);
			}
			ventil.amount += ndfItem.ndfItemTripPrice;
			ventil.amount += ndfItem.ndfItemKmPrice;
		}
		parameters.put("ventilAnalytiqueDataSource", new MyDataSource(ventils));
		
		// génération de la source de données pour les trajets
		List<NdfItem> trajets = new ArrayList<NdfItem>();
		for (NdfItem ndfItem : items) {
			if (0 != ndfItem.km) {
				trajets.add(ndfItem);
			}
		}
		// on complète jusqu'à 25 éléments
		if (25 > trajets.size()) {
			for (int i = trajets.size(); i < 25; i++) {
				trajets.add(new NdfItem(this));
			}
		}
		parameters.put("trajetsDataSource", new MyDataSource(trajets));
		
		// génération de la ventilation des kms
		List<VentilKms> ventilKms = new ArrayList<VentilKms>();
		for (NdfItem ndfItem : items) {
			VentilKms ventil = new VentilKms(ndfItem.business);
			if (ventilKms.contains(ventil)) {
				ventil = ventilKms.get(ventilKms.indexOf(ventil));
			} else {
				ventilKms.add(ventil);
				ventil.pu = km.price;
			}
			ventil.kms += ndfItem.km;
		}
		parameters.put("ventilKmsDataSource", new MyDataSource(ventilKms));
		
		
		
		double kmSum = 0d;
		for (NdfItem ndfItem : items) {
			kmSum += ndfItem.ndfItemKmPrice;
		}
		parameters.put("kmSum", kmSum);
		params.put("logoPath", "logo.jpg");
		
		// puts the parameters into the datasource
		//
		ds.setParameters(parameters);
		
		return ds;
	}


	public String getJRXmlName(Map<String, String> params) {
		return "ndf.jrxml";
	}
	
	
	
	public class VentilAnalytique {
		public Business business;
		public double amount = 0d;
		
		public VentilAnalytique(Business business) {
			this.business = business;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((business == null) ? 0 : business.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final VentilAnalytique other = (VentilAnalytique) obj;
			if (business == null) {
				if (other.business != null)
					return false;
			} else if (!business.equals(other.business))
				return false;
			return true;
		}
	}
	
	public class VentilKms {
		public Business business;
		public double pu;
		public int kms = 0;
		
		public double getTotal() {
			return kms * pu;
		}

		public VentilKms(Business business) {
			this.business = business;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((business == null) ? 0 : business.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final VentilKms other = (VentilKms) obj;
			if (business == null) {
				if (other.business != null)
					return false;
			} else if (!business.equals(other.business))
				return false;
			return true;
		}
		
	}
	
	/**
	 * Statistiques par année.
	 * 
	 * @author Christian christian.lorne.pro@gmail.com
	 *
	 */
	public static class YearStatisticBuilder {
		
		public static final short MONTH_IN_YEAR = 12;
		
		/**
		 * L'année qui regroupe les statistiques
		 */
		public int year;
		
		/**
		 * Chaque élément de cette liste est une map qui associe une affaire avec sa liste d'items.
		 * Les mois vont de 1 à 12 et correspondent à l'indice dans le tableau.
		 */
		public List<Map<Business, List<NdfItem>>> statistics = new ArrayList<Map<Business,List<NdfItem>>>();
		
		/**
		 * Constructeur par défaut qui initialise cet objet à partir des données en base.
		 * @param year Année des statistiques
		 */
		public YearStatisticBuilder(int year) {
			
			this.year = year;
			
			// Initialisation de la liste
			//
			for (int i = 0; i < MONTH_IN_YEAR; i++) {
				statistics.add(new HashMap<Business, List<NdfItem>>());
			}
			
			List<Ndf> ndfs = Ndf.find(
					"ndfDate >= :startDate and ndfDate <= :endDate" +
					" order by ndfDate"
					)
					.bind("startDate", getFirstDayOfYear(year).getTime())
					.bind("endDate", getLastDayOfYear(year).getTime())
					.fetch();
			
			for (Ndf ndf : ndfs) {
				int month = ndf.getDateAsCalendar().get(Calendar.MONTH);
				Logger.info("Traitement du mois %s ; date: %s; id: %s", month, ndf.ndfDate, ndf.id);
				
				Map<Business, List<NdfItem>> map = new TreeMap<Business, List<NdfItem>>(new Comparator<Business>() {
					
					@Override
					public int compare(Business o1, Business o2) {
						return o1.number.compareTo(o2.number);
					}
				
				});
				for (NdfItem item : ndf.items) {
					List<NdfItem> items = map.get(item.business);
					if (null == items) {
						items = new ArrayList<NdfItem>();
						map.put(item.business, items);
					}
					
					items.add(item);
				}
				
				
				// Conserver la map construite
				//
				this.statistics.set(month, map);
			}
			
		}
		
		/**
		 * @return Renvoie du nombre de kilomètres pour une année complète.
		 */
		public long getKmNumberForYear() {
			assert MONTH_IN_YEAR == this.statistics.size();
			assert 0 != this.year;
			
			long kmNumber = 0l;
			
			for (int i = 0; i < statistics.size(); i++) {
				kmNumber += getKmNumberForMonth(i);
			}
			
			return kmNumber;
		}
		
		/**
		 * @param month Le mois
		 * @return Renvoie le nombre de kilomètres pour un mois donné.
		 */
		public long getKmNumberForMonth(int month) {
			assert month <= MONTH_IN_YEAR;
			
			long kmNumber = 0l;

			Map<Business, List<NdfItem>> map = this.statistics.get(month);
			
			if (null == map) { // Vérifications d'usage
				Logger.info("Les notes de frais n'ont pas été trouvées pour la date %s/%s"
						, month, year);				
			} else {
				for (Business b : map.keySet()) {
					kmNumber += getKmNumberForBusinessAndMonth(b, month);
				}
				
			}
			
			return kmNumber;
		}
		
		public long getKmNumberForMonth(String monthLabel) {
			int month = Utils.getMonthNumber(monthLabel);
			return getKmNumberForMonth(month);
		}
		
		public long getKmNumberForBusinessAndMonth(Business business, int month) {
			List<NdfItem> items = this.statistics.get(month).get(business);
			
			long kmNumber = 0l;
			for (NdfItem item : items) {
				kmNumber += item.km;
			}
			return kmNumber;
		}
		
		
		private Calendar getFirstDayOfYear(int year) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.DAY_OF_YEAR, 1); 
			return cal;
		}
		private Calendar getLastDayOfYear(int year) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, year);
			cal.set(Calendar.MONTH, 11);
			cal.set(Calendar.DAY_OF_MONTH, 31); 
			return cal;
		}
	}

}
