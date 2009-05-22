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
package at.gp.web.jsf.extval.validation.clientside.script;

import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;

import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.component.UICommand;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlOutputText;
import javax.faces.context.FacesContext;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

import at.gp.web.jsf.extval.validation.clientside.ClientValidationStrategy;
import at.gp.web.jsf.extval.validation.clientside.MessageResolverAware;

/**
 * @author Gerhard Petracek
 * @since 1.x.2
 */
public class HtmlJSScriptBuilder implements ScriptBuilder
{
    private static final String JS_VALIDATOR_CLASS_NAME = "ExtValClientValidator";
    private static final String VALIDATION_METHOD_PREFIX = "validate";
    private static final String ON_VIOLATION_METHOD_PREFIX = "onViolation";
    private Map<String, Map<String, ClientValidationStrategy>> clientValidationStrategyMap
            = new HashMap<String, Map<String, ClientValidationStrategy>>();
    private UIComponent formComponent;

    public String buildScriptStart()
    {
        return "<script type=\"text/javascript\" language=\"Javascript\">";
    }

    public String buildValidationScript(
            String clientId, MetaDataEntry metaDataEntry, String validationErrorMessageTarget)
    {
        String functionName = createUniqueMethodName();

        ValidationStrategy validationStrategy = ExtValUtils.getValidationStrategyForMetaDataEntry(metaDataEntry);
        MetaDataTransformer transformer = ExtValUtils.getMetaDataTransformerForValidationStrategy(validationStrategy);

        String validationScript = "";
        String jsFunction;
        Map<String, Object> transformedMetaData = transformer.convertMetaData(metaDataEntry);
        for(String key : transformedMetaData.keySet())
        {
            ClientValidationStrategy clientValidationStrategy = resolveClientValidator(key);
            clientValidationStrategy.setClientId(clientId);
            clientValidationStrategy.setMetaData(key, transformedMetaData.get(key));
            clientValidationStrategy.setViolationMessageTarget(validationErrorMessageTarget);

            if(clientValidationStrategy instanceof MessageResolverAware)
            {
                ((MessageResolverAware)clientValidationStrategy)
                        .setMessageResolver(ExtValUtils.getMessageResolverForValidationStrategy(validationStrategy));
            }

            addClientValidationStrategy(functionName, key, clientValidationStrategy);

            jsFunction = clientValidationStrategy.getClientScript();

            if(jsFunction != null)
            {
                validationScript += JS_VALIDATOR_CLASS_NAME + "."
                        + functionName + "_" + key + " = function(){" +
                        extractFunctionality(jsFunction)  + "};";
            }
        }

        return validationScript;
    }

    private void addClientValidationStrategy(
            String functionName, String key, ClientValidationStrategy clientValidationStrategy)
    {
        Map<String, ClientValidationStrategy> clientValidationStrategies =
                this.clientValidationStrategyMap.get(functionName);

        if(clientValidationStrategies == null)
        {
            clientValidationStrategies = new HashMap<String, ClientValidationStrategy>();
            this.clientValidationStrategyMap.put(functionName, clientValidationStrategies);
        }

        //TODO
        clientValidationStrategies.put(key, clientValidationStrategy);
    }

    //just a simple implementation - override it to use a more sophisticated one
    @SuppressWarnings({"unchecked"})
    protected ClientValidationStrategy resolveClientValidator(String metaDataKey)
    {

        Map<String, Class<? extends ClientValidationStrategy>> clientValidationStrategyMap =
                (Map<String, Class<? extends ClientValidationStrategy>>)ExtValContext.getContext()
                        .getGlobalProperty(ClientValidationStrategy.class.getName());

        ClientValidationStrategy clientValidationStrategy = (ClientValidationStrategy)
                ClassUtils.tryToInstantiateClass(clientValidationStrategyMap.get(metaDataKey));

        if(clientValidationStrategy != null)
        {
            return clientValidationStrategy;
        }

        //TODO logging - warn
        return new ClientValidationStrategy() {

            public void setClientId(String clientId)
            {
            }

            public void setMetaData(String key, Object value)
            {
            }

            public void setViolationMessageTarget(String clientId)
            {
            }

            public String getClientScript()
            {
                return null;
            }

            public String getOnViolationScript()
            {
                return null;
            }
        };
    }

    private String extractFunctionality(String jsValidationScript)
    {
        return jsValidationScript.substring(jsValidationScript.indexOf("{") + 1, jsValidationScript.lastIndexOf("}"));
    }

    protected String createUniqueMethodName()
    {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return VALIDATION_METHOD_PREFIX + "_" +
                this.formComponent.getClientId(facesContext) + "_" + facesContext.getViewRoot().createUniqueId();
    }

    /**
     * target: <br/>
     * function ExtValClientValidator(){}; <br/>
     * ExtValClientValidator.validate__[currentFormId] = function(formId) {<br/>
     *   var isValid = true;
     *   <p/>
     *   if(!ExtValClientValidator.validate__[...]) <br/>
     *     isValid = false;
     *   //...
     *   return isValid;
     *
     * @return the final script which executes all validation methods
     */
    public String buildScriptEnd()
    {
        //TODO don't use && call all validation methods -> if one returns false -> return false;
        String validationScript = "function " + JS_VALIDATOR_CLASS_NAME + "(){};" +
                JS_VALIDATOR_CLASS_NAME + "." + getCurrentFormValidationMethodName() + " = function(formId){";
        validationScript += "var isValid = true;";

        Map<String, ClientValidationStrategy> entry;
        for(String functionName : this.clientValidationStrategyMap.keySet())
        {
            entry = this.clientValidationStrategyMap.get(functionName);

            for(String key : entry.keySet())
            {
                validationScript += "if(!" + JS_VALIDATOR_CLASS_NAME + "." + functionName + "_" + key + "()){" +
                        "isValid = false;" +
                        JS_VALIDATOR_CLASS_NAME + "."
                        + ON_VIOLATION_METHOD_PREFIX + "_" + functionName + "_" + key + "();}";
            }
        }

        validationScript += "return isValid;};";

        String jsFunction;
        for(String functionName : this.clientValidationStrategyMap.keySet())
        {
            entry = this.clientValidationStrategyMap.get(functionName);
            for(String metaDataKey : entry.keySet())
            {
                jsFunction = entry.get(metaDataKey).getOnViolationScript();

                if(jsFunction != null)
                {
                    validationScript += JS_VALIDATOR_CLASS_NAME
                            + "." + ON_VIOLATION_METHOD_PREFIX + "_" + functionName + "_" + metaDataKey
                            + " = function(){"
                            + extractFunctionality(jsFunction)
                            + "};";
                }
            }
        }

        return validationScript + "</script>";
    }

    public void addClientSideValidation(UIComponent uiComponent)
    {
        if(uiComponent instanceof UICommand)
        {
            addScriptInvokation((UICommand)uiComponent);
        }
        else if(uiComponent instanceof UIForm)
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
     * @param uiCommand command component which isn't immediate
     */
    private void addScriptInvokation(UICommand uiCommand)
    {
        String onClick = (String)ReflectionUtils.tryToInvokeMethod(
                uiCommand, ReflectionUtils.tryToGetMethod(uiCommand.getClass(), "getOnclick"));
        Method setOnsubmit = ReflectionUtils.tryToGetMethod(uiCommand.getClass(), "setOnclick", String.class);

        String methodInvokation = "ExtValClientValidator." + getCurrentFormValidationMethodName() + "('"
                + this.formComponent.getClientId(FacesContext.getCurrentInstance()) + "')";
        if (onClick == null)
        {
            ReflectionUtils.tryToInvokeMethod(uiCommand, setOnsubmit, "return " + methodInvokation + ";");
            return;
        }

        if(onClick.contains("ExtValClientValidator." + getCurrentFormValidationMethodName()))
        {
            return;
        }

        if (onClick.startsWith("return "))
        {
            onClick = onClick.replace("return ", "");
        }

        if(!onClick.trim().endsWith(";"))
        {
            onClick += ";";
        }

        onClick += "return " + methodInvokation + ";";

        ReflectionUtils.tryToInvokeMethod(uiCommand, setOnsubmit, onClick);
    }

    private void addScriptSection()
    {
        if(this.formComponent.findComponent(HtmlJSOutput.ID) == null)
        {
            this.formComponent.getChildren().add(new HtmlJSOutput());
        }
    }

    private String getCurrentFormValidationMethodName()
    {
        return "validate__" + this.formComponent.getClientId(FacesContext.getCurrentInstance()).replace(":", "_");
    }
}
