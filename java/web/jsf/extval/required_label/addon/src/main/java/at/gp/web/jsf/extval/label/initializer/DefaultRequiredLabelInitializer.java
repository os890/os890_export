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
package at.gp.web.jsf.extval.label.initializer;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.el.ValueExpression;
import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

import org.apache.myfaces.extensions.validator.util.ReflectionUtils;

import at.gp.web.jsf.extval.label.RequiredLabelAddonConfiguration;

/**
 * The default implementation for a RequiredLabelInitializer interface that by default adds a '*' in front of the text and adds the ExtValRequiredLabel css
 * style. The functionality of the class can be customized by web.xml initialization parameters or a custom implementation of
 * RequiredLabelAddonConfiguration.
 * 
 * @author Rudy De Busscher
 */
public class DefaultRequiredLabelInitializer implements RequiredLabelInitializer
{

    private static final String COLON = ":";

    /**
     * The logger.
     */
    protected final Logger logger = Logger.getLogger(getClass().getName());

    /**
     * The required marker.
     */
    private String requiredMarker;

    /**
     * Where to place the marker.
     */
    private String placeMarker;

    /**
     * The required css class.
     */
    private String requiredStyleClass;

    /**
     * contains marker HTML or XML and should escaping be switched off.
     */
    private boolean setEscapeToFalse;

    /**
     * During the initialization of the class, the customization values are read from the parameters REQUIRED_MARKER, PLACE_MARKER and REQUIRED_CLASS.
     */
    public DefaultRequiredLabelInitializer()
    {
        RequiredLabelAddonConfiguration addonConfiguration = RequiredLabelAddonConfiguration.get();
        this.requiredStyleClass = addonConfiguration.getRequiredStyleClass();
        this.placeMarker = addonConfiguration.getPlaceMarker();
        this.requiredMarker = addonConfiguration.getRequiredMarker();

        defineEscapeValue(requiredMarker);
    }

    /**
     * Defines if the escape property needs to be set (to false).
     * 
     * @param requiredMarker the value of the required marker
     */
    private void defineEscapeValue(String requiredMarker)
    {
        setEscapeToFalse = requiredMarker.indexOf('<') > -1 || requiredMarker.indexOf('&') > -1;

    }

    /**
     * {@inheritDoc}
     */
    public void configureLabel(FacesContext facesContext, UIOutput uiOutput)
    {
        applyRequiredMarker(facesContext, uiOutput);
    }

    /**
     * Indicate that the marker is required.
     * 
     * @param facesContext the faces context
     * @param uiOutput the ui label component
     */
    protected void applyRequiredMarker(FacesContext facesContext, UIOutput uiOutput)
    {
        ValueExpression expression = uiOutput.getValueExpression("value");

        if (expression != null)
        {
            applyRequiredMarkerUsingExpression(facesContext, uiOutput, expression.getExpressionString());
        }
        else
        {
            applyRequiredMarkerUsingValue(facesContext, uiOutput, (String) uiOutput.getValue());
        }
        if (setEscapeToFalse)
        {
            doSetEscapeToFalse(uiOutput);
        }
        applyStyleClass(uiOutput);
    }

    /**
     * Do set escape to false.
     * 
     * @param uiComponent the ui label component
     */
    private void doSetEscapeToFalse(UIOutput uiComponent)
    {
        // UIOutput itself has not the property, only some subclasses.
        Method method = ReflectionUtils.tryToGetMethod(uiComponent.getClass(), "setEscape", boolean.class);

        if (method != null)
        {
            ReflectionUtils.tryToInvokeMethod(uiComponent, method, Boolean.FALSE);
        }
    }

    /**
     * Indicate that the marker is required when the label value is set by literal.
     * 
     * @param facesContext the faces context
     * @param uiComponent the ui label component
     * @param value the text value of the label text
     */
    protected void applyRequiredMarkerUsingValue(FacesContext facesContext, UIOutput uiComponent, String value)
    {
        if (isPlaceMarkerBefore(placeMarker))
        {
            applyRequiredMarkerBeforeValue(facesContext, uiComponent, value);
        }
        if (isPlaceMarkerBeforeColon(placeMarker))
        {
            applyRequiredMarkerBeforeColonValue(facesContext, uiComponent, value);
        }
        if (isPlaceMarkerAfter(placeMarker))
        {
            applyRequiredMarkerAfterValue(facesContext, uiComponent, value);
        }
    }

    /**
     * Indicate that the marker is required when the label value is set by an EL expression.
     * 
     * @param facesContext the faces context
     * @param uiComponent the ui label component
     * @param expressionString the expression string
     */
    protected void applyRequiredMarkerUsingExpression(FacesContext facesContext, UIOutput uiComponent, String expressionString)
    {
        if (isPlaceMarkerBefore(placeMarker))
        {
            applyRequiredMarkerBeforeExpression(facesContext, uiComponent, expressionString);
        }
        if (isPlaceMarkerBeforeColon(placeMarker))
        {
            applyRequiredMarkerBeforeColonExpression(facesContext, uiComponent, expressionString);
        }
        if (isPlaceMarkerAfter(placeMarker))
        {
            applyRequiredMarkerAfterExpression(facesContext, uiComponent, expressionString);
        }
    }

    /**
     * Apply style class to the label.
     * 
     * @param uiComponent the ui label component
     */
    private void applyStyleClass(UIOutput uiComponent)
    {
        Method method = ReflectionUtils.tryToGetMethod(uiComponent.getClass(), "getStyleClass");
        if (method != null)
        {
            String styleClass = (String) ReflectionUtils.tryToInvokeMethod(uiComponent, method);
            // We assume that a setter exist when a getter is present.
            method = ReflectionUtils.tryToGetMethod(uiComponent.getClass(), "setStyleClass", String.class);
            ReflectionUtils.tryToInvokeMethod(uiComponent, method, insertRequiredStyleClass(styleClass));
        }

    }

    /**
     * Add the required style class to the current value.
     * 
     * @param styleClass the current value of the style class
     * @return the new value for the style class property.
     */
    private String insertRequiredStyleClass(String styleClass)
    {
        Set<String> classes = new HashSet<String>();
        if (styleClass != null && styleClass.length() > 0)
        {
            classes.addAll(Arrays.asList(styleClass.split(" ")));
        }
        classes.add(requiredStyleClass);
        StringBuilder builder = new StringBuilder();
        for (String _class : classes)
        {
            builder.append(_class).append(' ');
        }
        return builder.toString();
    }

    /**
     * Add the required marker in front of the label text when using an EL expression. When marker is already present, it isn't added twice.
     * 
     * @param facesContext the faces context
     * @param uiComponent the ui label component
     * @param expressionString the expression string for the label text.
     */
    protected void applyRequiredMarkerBeforeExpression(FacesContext facesContext, UIOutput uiComponent, String expressionString)
    {
        if (!expressionString.startsWith(requiredMarker))
        {
            uiComponent.setValueExpression("value", facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(),
                    requiredMarker + expressionString, String.class));
        }

    }

    /**
     * Add the required marker before the last colon when using an EL expression. When marker is already present, it isn't added twice. Also, when there is
     * no colon found in the literal part of the expression, the marker is added at the and of the text.
     * 
     * @param facesContext the faces context
     * @param uiComponent the ui label component
     * @param expressionString the expression string for the label text.
     */
    protected void applyRequiredMarkerBeforeColonExpression(FacesContext facesContext, UIOutput uiComponent, String expressionString)
    {
        String trimmed = expressionString.trim();

        if (!trimmed.endsWith(requiredMarker + COLON) && trimmed.endsWith(COLON))
        {
            uiComponent.setValueExpression("value", facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(),
                    insertMarkerBeforeColon(expressionString), String.class));
        }
        else if (!trimmed.endsWith(COLON) && !trimmed.endsWith(requiredMarker))
        {
            applyRequiredMarkerAfterExpression(facesContext, uiComponent, expressionString);
        }

    }

    /**
     * Add the required marker before the last colon when using an EL expression. When marker is already present, it isn't added twice.
     * 
     * @param facesContext the faces context
     * @param uiComponent the ui label component
     * @param expressionString the expression string for the label text.
     */
    protected void applyRequiredMarkerAfterExpression(FacesContext facesContext, UIOutput uiComponent, String expressionString)
    {
        if (!expressionString.endsWith(requiredMarker))
        {
            uiComponent.setValueExpression("value", facesContext.getApplication().getExpressionFactory().createValueExpression(facesContext.getELContext(),
                    expressionString + requiredMarker, String.class));
        }

    }

    /**
     * Add the required marker in front of the label text when using a fixed value. When marker is already present, it isn't added twice.
     * 
     * @param facesContext the faces context
     * @param uiComponent the ui label component
     * @param labelValue the label value
     */
    protected void applyRequiredMarkerBeforeValue(FacesContext facesContext, UIOutput uiComponent, String labelValue)
    {
        if (!labelValue.startsWith(requiredMarker))
        {
            uiComponent.setValue(requiredMarker + labelValue);
        }

    }

    /**
     * Add the required marker before the last colon when using a fixed value When marker is already present, it isn't added twice.
     * 
     * @param facesContext the faces context
     * @param uiComponent the ui label component
     * @param labelValue the label value
     */
    protected void applyRequiredMarkerBeforeColonValue(FacesContext facesContext, UIOutput uiComponent, String labelValue)
    {
        String trimmed = labelValue.trim();

        if (!trimmed.endsWith(requiredMarker + COLON) && trimmed.endsWith(COLON))
        {
            uiComponent.setValue(insertMarkerBeforeColon(labelValue));
        }
        else if (!trimmed.endsWith(COLON) && !trimmed.endsWith(requiredMarker))
        {
            applyRequiredMarkerAfterValue(facesContext, uiComponent, labelValue);
        }

    }

    /**
     * Add the required marker after the text when using a fixed value. When marker is already present, it isn't added twice.
     * 
     * @param facesContext the faces context
     * @param uiComponent the ui label component
     * @param labelValue the label value
     */
    protected void applyRequiredMarkerAfterValue(FacesContext facesContext, UIOutput uiComponent, String labelValue)
    {
        if (!labelValue.endsWith(requiredMarker))
        {
            uiComponent.setValue(labelValue + requiredMarker);
        }

    }

    /**
     * Insert marker before colon.
     * 
     * @param labelValue the label value
     * @return the result
     */
    private String insertMarkerBeforeColon(String labelValue)
    {
        StringBuilder label = new StringBuilder(labelValue);
        int colonPos = label.lastIndexOf(COLON);
        label.deleteCharAt(colonPos);
        label.insert(colonPos, requiredMarker + COLON);
        return label.toString();
    }

    /**
     * Checks if we need to place marker before text.
     * 
     * @param placeMarker the place marker parameter value
     * @return true, if is place marker before
     */
    private static boolean isPlaceMarkerBefore(String placeMarker)
    {
        return "BEFORE".equalsIgnoreCase(placeMarker);
    }

    /**
     * Checks if we need to place marker after text.
     * 
     * @param placeMarker the place marker parameter value
     * @return true, if is place marker after
     */
    private static boolean isPlaceMarkerAfter(String placeMarker)
    {
        return "AFTER".equalsIgnoreCase(placeMarker);
    }

    /**
     * Checks if we need to place marker before colon at the end of the text.
     * 
     * @param placeMarker the place marker parameter value
     * @return true, if is place marker before colon
     */
    private static boolean isPlaceMarkerBeforeColon(String placeMarker)
    {
        return "BEFORE_COLON".equalsIgnoreCase(placeMarker);
    }
}
