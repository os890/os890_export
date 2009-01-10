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
package org.apache.myfaces.extensions.validator.custom;

import org.apache.myfaces.extensions.validator.core.metadata.CommonMetaDataKeys;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.AbstractValidationErrorMessageResolver;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.JsfUtils;
import org.apache.myfaces.extensions.validator.util.ReflectionUtils;
import org.apache.myfaces.extensions.validator.baseval.strategy.JpaValidationStrategy;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import java.util.Map;

import net.sf.jsfcomp.clientvalidators.requiredfieldvalidator.RequiredFieldValidator;

/**
 * just a simple example to show the jsf-comp client-side-validation integration.
 * (http://jsf-comp.sourceforge.net/components/clientvalidators/index.html)
 * it illustrates jpa annotation -> required client-side validation via jsf-comp.
 * to support other constellations as well it would require a bit more effort.
 * <p/>
 * it doesn't cover skip validation and much more.
 * a real world constellation which covers all aspects is available at:
 * org.apache.myfaces.extensions.validator.trinidad.initializer.component.TrinidadComponentInitializer
 * <p/>
 * please inform the myfaces team, if you are interested in a full jsf-comp client-side-validation support
 *
 * @author Gerhard Petracek
 */
public class ComponentInitializer implements org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer {

    public void configureComponent(FacesContext facesContext, UIComponent uiComponent, Map<String, Object> metaData) {
        if (Boolean.TRUE.equals(metaData.get(CommonMetaDataKeys.REQUIRED))) {
            configureRequired(facesContext, uiComponent);
        }
    }

    private void configureRequired(FacesContext facesContext, UIComponent uiComponent) {
        RequiredFieldValidator requiredFieldValidator = new RequiredFieldValidator();   //alternative see [1]
        requiredFieldValidator.setComponentToValidate(uiComponent.getId());
        requiredFieldValidator.setErrorMessage(getRequiredMessage(facesContext, uiComponent));
        requiredFieldValidator.setHighlight(true);
        requiredFieldValidator.setDisplay("none");

        uiComponent.getParent().getChildren().add(requiredFieldValidator);
    }
    //[1] (RequiredFieldValidator)facesContext.getApplication().createComponent("net.sf.jsfcomp.clientvalidators.requiredfieldvalidator")

    /*
     * to differ between jpa field_required and something like @Required more effort is required
     * so it isn't implemented here - it's just a demo implementation
     */

    private String getRequiredMessage(FacesContext facesContext, UIComponent uiComponent) {
        String key = "field_required";
        String message;

        MessageResolver messageResolver = ExtValUtils.getMessageResolverForValidationStrategy(new JpaValidationStrategy());

        //re-use the error message defined for jpa (extval mechanism)
        message = messageResolver.getMessage(key, facesContext.getViewRoot().getLocale());

        String marker = AbstractValidationErrorMessageResolver.MISSING_RESOURCE_MARKER;

        if ((marker + key + marker).equals(message)) {
            //no message defined -> use jsf message for required fields
            return createJsfStdMessage(uiComponent);
        }

        return message;
    }

    private String createJsfStdMessage(UIComponent uiComponent) {
        String message = JsfUtils.getDefaultFacesMessageBundle().getString("javax.faces.component.UIInput.REQUIRED");

        String label = (String) ReflectionUtils.tryToInvokeMethod(uiComponent, ReflectionUtils.tryToGetMethod(uiComponent.getClass(), "getLabel"));

        return label == null ? message : message.replace("{0}", label);
    }
}
