import java.util.Locale;
import java.util.ResourceBundle;

import net.sf.oval.Validator;
import net.sf.oval.localization.message.ResourceBundleMessageResolver;
import play.jobs.Job;
import play.jobs.OnApplicationStart;

@OnApplicationStart
public class BootStrap extends Job {

	@Override
	public void doJob() throws Exception {
		Locale.setDefault(Locale.FRANCE);
		
		// Check if the database is empty
		//if (User.count() == 0) {
			//Fixtures.load("initial-data.yml");
			
		//}
		
		ResourceBundleMessageResolver resolver = (ResourceBundleMessageResolver)Validator.getMessageResolver();
		if (!resolver.addMessageBundle(ResourceBundle.getBundle("errors")))
			System.out.println("Impossible de charger le bundle error");

	}

	
}
