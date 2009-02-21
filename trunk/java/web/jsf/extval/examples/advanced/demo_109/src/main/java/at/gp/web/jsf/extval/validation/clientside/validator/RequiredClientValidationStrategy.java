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
package at.gp.web.jsf.extval.validation.clientside.validator;

import org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver;
import at.gp.web.jsf.extval.validation.clientside.ClientValidationStrategy;
import at.gp.web.jsf.extval.validation.clientside.MessageResolverAware;

/**
 * @author Gerhard Petracek
 * @since 1.x.2
 */
public class RequiredClientValidationStrategy implements ClientValidationStrategy, MessageResolverAware
{
    private String clientId;
    //TODO
    private MessageResolver messageResolver;
    private String messageTarget;

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public void setMetaData(String key, Object value)
    {
    }

    public void setViolationMessageTarget(String clientId)
    {
        this.messageTarget = clientId;
    }

    public String getClientScript()
    {
        return "function validateRequired() {" +
                "var inputField = document.getElementById('" + clientId + "');return inputField.value;" +
                "};";
    }

    public String getOnViolationScript()
    {
        return "function onRequiredViolation() {" +
                "var inputField = document.getElementById('" + clientId + "');" +
                "if(inputField){inputField.style.border = '1px solid red';}" +
                "var messageArea = document.getElementById('" + messageTarget + "');" +
                "if(messageArea){messageArea.innerHTML = 'input required';}" +
                "};";
    }

    public void setMessageResolver(MessageResolver messageResolver)
    {
        this.messageResolver = messageResolver;
    }
}
