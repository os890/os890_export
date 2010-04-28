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
package at.gp.web.jsf.extval.beanval.label.interceptor;

import org.apache.myfaces.extensions.validator.beanval.BeanValidationModuleValidationInterceptor;

import javax.faces.component.UIComponent;
import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIOutput;
import javax.faces.component.html.HtmlOutputLabel;
import javax.faces.context.FacesContext;
import javax.el.ValueExpression;

/**
 * @author Gerhard Petracek
 * @author Rudy De Busscher
 */
public class BeanValidationAwareLabelRendererInterceptor extends BeanValidationModuleValidationInterceptor
{
    private static final String REQUIRED_MARKER = "* ";

    @Override
    protected boolean processComponent(UIComponent uiComponent)
    {
        return super.processComponent(uiComponent) || uiComponent instanceof HtmlOutputLabel;
    }

    @Override
    protected void initComponent(FacesContext facesContext, UIComponent uiComponent)
    {
        if (uiComponent instanceof EditableValueHolder)
        {
            super.initComponent(facesContext, uiComponent);
        }
        else
        {
            UIComponent targetComponent = findLabeledEditableComponent(facesContext, uiComponent);

            if (targetComponent != null)
            {
                super.initComponent(facesContext, targetComponent);

                if (((EditableValueHolder) targetComponent).isRequired())
                {
                    applyRequiredMarker(facesContext, (UIOutput) uiComponent);
                }

            }
        }
    }

    private static void applyRequiredMarker(FacesContext facesContext, UIOutput uiComponent)
    {
        ValueExpression expression = uiComponent.getValueExpression("value");

        if (expression != null)
        {
            String expressionString = expression.getExpressionString();
            if (!expressionString.startsWith(REQUIRED_MARKER))
            {
                uiComponent.setValueExpression("value",
                        facesContext.getApplication().getExpressionFactory()
                                .createValueExpression(
                                facesContext.getELContext(), REQUIRED_MARKER + expressionString, String.class));
            }
        }
        else
        {
            String value = (String) uiComponent.getValue();
            if (!value.startsWith(REQUIRED_MARKER))
            {
                uiComponent.setValue(REQUIRED_MARKER + value);
            }
        }
    }

    public UIComponent findLabeledEditableComponent(FacesContext facesContext, UIComponent outputLabel)
    {
        String target = ((HtmlOutputLabel) outputLabel).getFor();
        if (target == null)
        {
            return null;
        }

        UIComponent result = outputLabel.findComponent(target);

        if (result instanceof EditableValueHolder)
        {
            return result;
        }

        this.logger.finest(outputLabel.getClientId(facesContext) + " doesn't reference an editable component");

        return null;
    }
}
