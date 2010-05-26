/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package at.gp.web.jsf.extval.bv.validation.clientside.script;

import at.gp.web.jsf.extval.bv.validation.clientside.validator.ClientValidationStrategy;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;

import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Gerhard Petracek
 */
public class HtmlJSScriptBuilder implements ScriptBuilder
{
    private static final String JS_VALIDATOR_CLASS_NAME = "ExtValClientValidator";
    private static final String VALIDATION_METHOD_PREFIX = "validate";
    private static final String ON_VIOLATION_METHOD_PREFIX = "onViolation";
    private Map<String, Class<? extends ClientValidationStrategy>> availableClientValidationStrategies
            = new HashMap<String, Class<? extends ClientValidationStrategy>>();

    private Map<String, Map<String, ClientValidationStrategy>> clientValidationStrategyMap
            = new HashMap<String, Map<String, ClientValidationStrategy>>();
    private UIComponent formComponent;

    private FacesContext facesContext = FacesContext.getCurrentInstance();

    public HtmlJSScriptBuilder()
    {
        //noinspection unchecked
        this.availableClientValidationStrategies = (Map<String, Class<? extends ClientValidationStrategy>>)
                ExtValContext.getContext().getGlobalProperty(ClientValidationStrategy.class.getName());
    }

    public String buildScriptStart()
    {
        return "<script type=\"text/javascript\" language=\"Javascript\">";
    }

    public String buildValidationScript(
            String clientId, Map<String, Object> transformedMetaData, String validationErrorMessageTarget)
    {
        String functionName = createUniqueMethodName();

        StringBuilder validationScript = new StringBuilder("");
        String jsFunction = null;

        for (String key : transformedMetaData.keySet())
        {
            ClientValidationStrategy clientValidationStrategy = resolveClientValidator(key);

            if (clientValidationStrategy != null)
            {
                clientValidationStrategy.setClientId(clientId);
                clientValidationStrategy.setMetaData(key, transformedMetaData.get(key));
                clientValidationStrategy.setViolationMessageTarget(validationErrorMessageTarget);

                addClientValidationStrategy(functionName, key, clientValidationStrategy);
                jsFunction = clientValidationStrategy.getClientScript();
            }

            if (jsFunction != null)
            {
                validationScript.append(JS_VALIDATOR_CLASS_NAME);
                validationScript.append(".");
                validationScript.append(functionName);
                validationScript.append("_");
                validationScript.append(key);
                validationScript.append(" = function(){");
                validationScript.append(extractFunctionality(jsFunction));
                validationScript.append("};");
            }
        }

        return validationScript.toString();
    }

    public Object buildResetScript(String clientId, Map<String, Object> transformedMetaData, String validationErrorMessageTarget)
    {
        StringBuilder result = new StringBuilder("");
        Map<String, String> clientIdToMessageTargetMapping = new HashMap<String, String>();

        for (String key : transformedMetaData.keySet())
        {
            ClientValidationStrategy clientValidationStrategy = resolveClientValidator(key);

            if (clientValidationStrategy != null)
            {
                clientIdToMessageTargetMapping.put(clientId, validationErrorMessageTarget);
            }
        }

        for (Map.Entry<String, String> messageTargetEntry : clientIdToMessageTargetMapping.entrySet())
        {
            result.append("currentNode = document.getElementById('");
            result.append(messageTargetEntry.getKey());
            result.append("');");
            result.append("if(currentNode){currentNode.style.border = '1px solid black';}");

            result.append("currentNode = document.getElementById('");
            result.append(messageTargetEntry.getValue());
            result.append("');");
            result.append("if(currentNode){currentNode.innerHTML = '';}");
        }

        return result.toString();
    }

    private void addClientValidationStrategy(
            String functionName, String key, ClientValidationStrategy clientValidationStrategy)
    {
        Map<String, ClientValidationStrategy> clientValidationStrategies =
                this.clientValidationStrategyMap.get(functionName);

        if (clientValidationStrategies == null)
        {
            clientValidationStrategies = new HashMap<String, ClientValidationStrategy>();
            this.clientValidationStrategyMap.put(functionName, clientValidationStrategies);
        }

        //TODO
        if (clientValidationStrategy.getClientScript() != null)
        {
            clientValidationStrategies.put(key, clientValidationStrategy);
        }
    }

    //just a simple implementation - override it to use a more sophisticated one
    @SuppressWarnings({"unchecked"})
    protected ClientValidationStrategy resolveClientValidator(String key)
    {
        Class<? extends ClientValidationStrategy> clientValidationStrategyClass =
                this.availableClientValidationStrategies.get(key);

        if (clientValidationStrategyClass != null)
        {
            return ClassUtils.tryToInstantiateClass(clientValidationStrategyClass);
        }

        return null;
    }

    private String extractFunctionality(String jsValidationScript)
    {
        return jsValidationScript.substring(jsValidationScript.indexOf("{") + 1, jsValidationScript.lastIndexOf("}"));
    }

    protected String createUniqueMethodName()
    {
        return VALIDATION_METHOD_PREFIX + "_" +
                this.formComponent.getClientId(this.facesContext) + "_" +
                this.facesContext.getViewRoot().createUniqueId();
    }

    /**
     * target: <br/>
     * function ExtValClientValidator(){}; <br/>
     * ExtValClientValidator.validate__[currentFormId] = function(formId) {<br/>
     * var isValid = true;
     * <p/>
     * if(!ExtValClientValidator.validate__[...]) <br/>
     * isValid = false;
     * //...
     * return isValid;
     *
     * @return the final script which executes all validation methods
     */
    public String buildScriptEnd()
    {
        //TODO
        StringBuilder validationScript = new StringBuilder("function ");
        validationScript.append(JS_VALIDATOR_CLASS_NAME);
        validationScript.append("(){};");
        validationScript.append(JS_VALIDATOR_CLASS_NAME);
        validationScript.append(".");
        validationScript.append(getCurrentFormValidationMethodName());
        validationScript.append(" = function(formId){");
        validationScript.append("var isValid = true;");
        validationScript.append("ExtValClientValidator.resetStyles();");

        Map<String, ClientValidationStrategy> entry;
        for (String functionName : this.clientValidationStrategyMap.keySet())
        {
            entry = this.clientValidationStrategyMap.get(functionName);

            for (String key : entry.keySet())
            {
                validationScript.append("if(!");
                validationScript.append(JS_VALIDATOR_CLASS_NAME);
                validationScript.append(".");
                validationScript.append(functionName);
                validationScript.append("_");
                validationScript.append(key);
                validationScript.append("()){");
                validationScript.append("isValid = false;");
                validationScript.append(JS_VALIDATOR_CLASS_NAME);
                validationScript.append(".");
                validationScript.append(ON_VIOLATION_METHOD_PREFIX);
                validationScript.append("_");
                validationScript.append(functionName);
                validationScript.append("_");
                validationScript.append(key);
                validationScript.append("();}");
            }
        }

        validationScript.append("return isValid;};");

        String jsFunction;
        for (String functionName : this.clientValidationStrategyMap.keySet())
        {
            entry = this.clientValidationStrategyMap.get(functionName);
            for (String metaDataKey : entry.keySet())
            {
                jsFunction = entry.get(metaDataKey).getOnViolationScript();

                if (jsFunction != null)
                {
                    validationScript.append(JS_VALIDATOR_CLASS_NAME);
                    validationScript.append(".");
                    validationScript.append(ON_VIOLATION_METHOD_PREFIX);
                    validationScript.append("_");
                    validationScript.append(functionName);
                    validationScript.append("_");
                    validationScript.append(metaDataKey);
                    validationScript.append(" = function(){");
                    validationScript.append(extractFunctionality(jsFunction));
                    validationScript.append("};");
                }
            }
        }

        validationScript.append("</script>");

        return validationScript.toString();
    }

    public void addClientSideValidation(UIComponent uiComponent)
    {
        if (uiComponent instanceof UICommand)
        {
            addScriptInvokation((UICommand) uiComponent);
        }
        else if (uiComponent instanceof UIForm)
        {
            this.formComponent = uiComponent;
            addScriptSection();
        }
    }

    public UIOutput getMarkerComponent()
    {
        return new HtmlOutputText();
    }

    /**
     * target: onsubmit="...;return ExtValClientValidator.validate__currentFormId('currentFormId');"
     *
     * @param uiCommand command component which isn't immediate
     */
    private void addScriptInvokation(UICommand uiCommand)
    {
        String onClick = (String) ReflectionUtils.tryToInvokeMethod(
                uiCommand, ReflectionUtils.tryToGetMethod(uiCommand.getClass(), "getOnclick"));
        Method setOnsubmit = ReflectionUtils.tryToGetMethod(uiCommand.getClass(), "setOnclick", String.class);

        String methodInvokation = "ExtValClientValidator." + getCurrentFormValidationMethodName() + "('"
                + this.formComponent.getClientId(this.facesContext) + "')";
        if (onClick == null)
        {
            ReflectionUtils.tryToInvokeMethod(uiCommand, setOnsubmit, "return " + methodInvokation /*+ ";"*/);
            return;
        }

        if (onClick.contains("ExtValClientValidator." + getCurrentFormValidationMethodName()))
        {
            return;
        }

        if (onClick.startsWith("return "))
        {
            onClick = onClick.replace("return ", "");
        }

        if (!onClick.trim().endsWith(";"))
        {
            onClick += ";";
        }

        onClick += "return " + methodInvokation + ";";

        ReflectionUtils.tryToInvokeMethod(uiCommand, setOnsubmit, onClick);
    }

    private void addScriptSection()
    {
        if (this.formComponent.findComponent(HtmlJSOutput.ID) == null)
        {
            this.formComponent.getChildren().add(new HtmlJSOutput());
        }
    }

    private String getCurrentFormValidationMethodName()
    {
        return "validate__" + this.formComponent.getClientId(this.facesContext).replace(":", "_");
    }
}
