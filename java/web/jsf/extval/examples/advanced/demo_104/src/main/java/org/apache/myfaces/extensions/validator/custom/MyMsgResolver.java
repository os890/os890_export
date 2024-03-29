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

import at.gp.web.jsf.extval.config.annotation.MessageResolver;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.DefaultValidationErrorMessageResolver;


/**
 * @author Gerhard Petracek
 */
@MessageResolver(validationStrategyClasses = MyRequiredValidator.class)
public class MyMsgResolver extends DefaultValidationErrorMessageResolver
{
    public MyMsgResolver()
    {
        //the var name of the resource-bundle defined in: faces-config.xml
        setMessageBundleVarName("messages");
    }

    /*
     * for jsf 1.1 and 1.2
     */
    //protected String getCustomBaseName()
    //{
    //    return "at.gp.web.jsf.extval.bundle.messages";
    //}
}