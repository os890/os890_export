/*
 * Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package at.gp.web.jsf.extval.beanval.label.initializer;

import javax.faces.component.UIOutput;
import javax.faces.context.FacesContext;

/**
 * The Interface RequiredLabelInitializer that can be used to define how a label that is used for a required field
 * should be adjusted for display to reflect the requirement of the field it belongs to.
 *
 * @author Rudy De Busscher
 */
public interface RequiredLabelInitializer
{
    /**
     * Configure label because it belongs to a required field.
     *
     * @param facesContext the faces context
     * @param uiComponent  the ui label component
     */
    void configureLabel(FacesContext facesContext, UIOutput uiComponent);
}
