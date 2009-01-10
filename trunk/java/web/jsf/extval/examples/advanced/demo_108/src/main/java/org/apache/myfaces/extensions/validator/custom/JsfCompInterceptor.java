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

import org.apache.myfaces.extensions.validator.core.interceptor.AbstractRendererInterceptor;

import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.component.UIForm;
import javax.faces.render.Renderer;
import java.io.IOException;

import net.sf.jsfcomp.clientvalidators.scriptgenerator.ScriptGenerator;
import net.sf.jsfcomp.clientvalidators.validationsummary.ValidationSummary;

/**
 * this class is optional - it shows how to add jsf-comp artifacts automatically
 * you can continue to use the tags instead
 *
 * @author Gerhard Petracek
 */
public class JsfCompInterceptor extends AbstractRendererInterceptor {
    @Override
    public void beforeEncodeBegin(FacesContext facesContext, UIComponent uiComponent, Renderer wrapped) throws IOException {
        if (uiComponent instanceof UIForm) {
            ScriptGenerator scriptGenerator = new ScriptGenerator();
            scriptGenerator.setForm(uiComponent.getId());
            uiComponent.getChildren().add(scriptGenerator);
            uiComponent.getChildren().add(new ValidationSummary());
        }
    }

}
