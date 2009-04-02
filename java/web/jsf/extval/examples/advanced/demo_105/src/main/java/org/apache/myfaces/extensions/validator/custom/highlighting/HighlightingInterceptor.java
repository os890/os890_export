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
package org.apache.myfaces.extensions.validator.custom.highlighting;

import org.apache.myfaces.extensions.validator.core.interceptor.ValidationExceptionInterceptor;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlInputText;
import javax.faces.validator.ValidatorException;

/**
 * example for setting a style-class for invalid input text fields
 *
 * @author Gerhard Petracek
 * @since 1.x.1
 */
public class HighlightingInterceptor implements ValidationExceptionInterceptor
{
    public boolean afterThrowing(UIComponent uiComponent, MetaDataEntry metaDataEntry, Object convertedObject, ValidatorException validatorException, ValidationStrategy validatorExceptionSource)
    {
        if(uiComponent instanceof HtmlInputText)
        {
            //instead of overriding it you can impl. a smarter mechanism
            ((HtmlInputText)uiComponent).setStyleClass("invalid_input");
        }

        //add further input components here
        
        return true;
    }
}
