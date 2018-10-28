package models.report;

import java.util.Map;

import net.sf.jasperreports.engine.JRException;

public interface IJRReportProvider {

	MyDataSource getJRDataSource(Map<String, String> params)
		throws JRException;
	
	String getJRXmlName(Map<String, String> params);
}
