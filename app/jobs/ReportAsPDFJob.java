package jobs;
import java.io.ByteArrayOutputStream;
import java.util.Map;

import models.Ndf;
import models.report.IJRReportProvider;
import models.report.JRUtils;
import models.report.MyDataSource;
import play.db.jpa.Model;
import play.jobs.Job;


public class ReportAsPDFJob extends Job<ByteArrayOutputStream> {

	private IJRReportProvider provider;
	private Map<String, String> params;
	
	public ReportAsPDFJob(IJRReportProvider provider, Map<String, String> params) {
		super();
		this.provider = provider;
		this.params = params;
	}

	@Override
	public ByteArrayOutputStream doJobWithResult() throws Exception {
		
		// ensures the object model is loaded in the JPA session factory
		//
		if (Model.class.isAssignableFrom(provider.getClass())) {
			Model model = (Model) provider;
			provider = (IJRReportProvider)Ndf.findById(model.id);
		}
		
		// gets the datasource and the xml name
		//
		MyDataSource ds = provider.getJRDataSource(params);
		String xmlName = provider.getJRXmlName(params);
		
		// generates the output stream
		//
		return JRUtils.exportReport(xmlName, ds);
	}

}
