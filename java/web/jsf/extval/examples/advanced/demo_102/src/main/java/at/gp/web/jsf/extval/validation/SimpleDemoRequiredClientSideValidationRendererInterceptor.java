package at.gp.web.jsf.extval.validation;

import org.apache.myfaces.extensions.validator.core.interceptor.AbstractRendererInterceptor;

import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlForm;
import javax.faces.render.Renderer;
import java.io.IOException;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

/**
 * very simplified client-side-validation support - just to illustrate the feasibility
 * !!!please do >not< use the shown approach in production code!!!
 *
 * @author Gerhard Petracek
 */
public class SimpleDemoRequiredClientSideValidationRendererInterceptor extends AbstractRendererInterceptor
{
    public void beforeEncodeBegin(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped) throws IOException
    {
        if(uiComponent instanceof HtmlForm)
        {
            ((HtmlForm)uiComponent).setOnsubmit("return validateRequired();");
        }
        else if(uiComponent instanceof UIInput && ((UIInput)uiComponent).isRequired())
        {
            saveForCurrentRequest(UIInput.class.getName(), uiComponent.getClientId(facesContext));
        }
    }

    public void afterEncodeEnd(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped) throws IOException
    {
        if(uiComponent instanceof UIForm && getSavedRequestInformation(UIInput.class.getName()) != null)
        {
            renderJavaScriptForSingleFormPerView(facesContext);
        }
    }

    //quick and dirty rendering of js
    private void renderJavaScriptForSingleFormPerView(FacesContext facesContext) throws IOException
    {
        StringBuffer result = new StringBuffer("<div id='messageArea'></div>");

        result.append("<script type=\"text/javascript\">");
        result.append("function validateRequired(){");
        result.append("var inputComponentIds = [");

        boolean start = true;
        for(String currentClientId : (List<String>)getSavedRequestInformation(UIInput.class.getName()))
        {
            if(start)
            {
                start = false;
            }
            else
            {
                result.append(",");
            }

            result.append("'" + currentClientId + "'");
        }
        result.append("];");

        result.append("for(currentIndex in inputComponentIds) {");
        result.append("var inputComponent = document.getElementById(inputComponentIds[currentIndex]);");
        result.append("if(inputComponent.value == '') { inputComponent.focus();");

        result.append("document.getElementById('messageArea').innerHTML = 'please finish the form!';");
        result.append("return false;}");
        result.append("}");
        result.append("return true");
        result.append("}");

        result.append("</script>");

        ResponseWriter writer = facesContext.getResponseWriter();
        writer.write(result.toString());
    }

    private void saveForCurrentRequest(String key, Object value)
    {
        Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

        if(!requestMap.containsKey(key))
        {
            requestMap.put(key, new ArrayList());
        }

        List values = (List)requestMap.get(key);
        values.add(value);
    }

    private List getSavedRequestInformation(String key)
    {
        return (List)FacesContext.getCurrentInstance().getExternalContext().getRequestMap().get(key);
    }
}