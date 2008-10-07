package at.gp.web.jsf.extval.validation;

import org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIInput;
import java.util.Map;
import java.util.HashMap;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */
public class StandardComponentInitializer implements ComponentInitializer
{
    public void configureComponent(FacesContext facesContext, UIComponent uiComponent, Map<String, Object> metaData)
    {
        //just a simplified impl. - don't use it that way!
        if(uiComponent instanceof UIInput && metaData.containsKey("required"))
        {
            ((UIInput)uiComponent).setRequired(true);
        }
    }
}