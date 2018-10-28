package models.report;

import java.io.ByteArrayOutputStream;
import java.io.File;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRReport;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRProperties;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

public class JRUtils {
	
	
	public static JasperReport getJasperReport(String xmlName)
		throws JRException {
		
		// builds the complete path of the jasper file
		//
		String path = "jr" + File.separatorChar + xmlName;
		
		// turn off the xml validation
		//
		
		JRProperties.setProperty(JRProperties.COMPILER_XML_VALIDATION, false);
//		JRProperties.setProperty(JRProperties.COMPILER_CLASSPATH, Thread.currentThread().getContextClassLoader().toString());
		
		// get the jasper design object
		//
		JasperDesign design = JRXmlLoader.load(JRUtils.class.getClassLoader().getResourceAsStream(path));
        design.setLanguage(JRReport.LANGUAGE_JAVA);
        
		
        return JasperCompileManager.compileReport(design);
	}
	
	public static JasperPrint getJasperPrint(JasperReport report, MyDataSource ds)
		throws JRException{
		
		// manages an empty datasource
		//
		boolean notempty = ds.next();
		if (notempty) ds.moveFirst();
		
		// generates the print JR object with datasource and parameters
		//
		JasperPrint print = (notempty) ? JasperFillManager.fillReport(report, ds.getParameters(), ds)
				: JasperFillManager.fillReport(report, ds.getParameters(), new JREmptyDataSource());
		return print;
	}
	
	
	public static ByteArrayOutputStream exportReport(String xmlName, MyDataSource ds)
		throws JRException {
		
		// get the jasper print
		//
		JasperPrint jp = getJasperPrint(getJasperReport(xmlName), ds);
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		// print in the output stream
		//
		JasperExportManager.exportReportToPdfStream(jp, os);
		
		return os;
	}
}
