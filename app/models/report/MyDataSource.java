package models.report;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import ext.CustomExtension;

public final class MyDataSource extends JRBeanCollectionDataSource {
    
    private String name;
    private boolean ignorePagination = false;
    
    private Map parameters = new HashMap();
//    private Map<String, ParamsReport> groupsParameters = new HashMap<String, ParamsReport>();
    
    
    public MyDataSource(Collection coll) {
        super(coll);
    }
    public MyDataSource(Collection coll, String name) {
        super(coll);
        setName(name);
    }
    public MyDataSource(Collection coll, String name, Map parameters) {
        super(coll);
        setName(name);
        setParameters(parameters);
    }

    /* (non-Javadoc)
     * @see net.sf.jasperreports.engine.data.JRBeanCollectionDataSource#getFieldValue(net.sf.jasperreports.engine.JRField)
     */
    public Object getFieldValue(JRField field) throws JRException {
        
        Object value = super.getFieldValue(field);
        
        if (null != value) {
            if (value instanceof Date) {
                value = CustomExtension.format((Date) value, null);
            } else if (value instanceof Character) {
                value = ((Character) value).toString();
            }
        }
        
        return value;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Map getParameters() { return parameters; }
    public void setParameters(Map parameters) { this.parameters = parameters; }
/*
    public void addParamsGroupsReport(String nameGroup, ParamsReport params) {
        groupsParameters.put(nameGroup, params);
    }
    public void removeParamsGroupsReport(String nameGroup) {
        groupsParameters.remove(nameGroup);
    }
    public Map getParamsGroupsReport() {
        return groupsParameters;
    }
*/
    public boolean isIgnorePagination() {
        return ignorePagination;
    }
    public void setIgnorePagination(boolean ignorePagination) {
        this.ignorePagination = ignorePagination;
    }
}

