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

import org.apache.myfaces.extensions.validator.core.metadata.CommonMetaDataKeys;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver;
import at.gp.web.jsf.extval.validation.clientside.ClientValidationStrategy;
import at.gp.web.jsf.extval.validation.clientside.MessageResolverAware;

/**
 * @author Gerhard Petracek
 * @since 1.x.2
 */
public class LengthClientValidationStrategy implements ClientValidationStrategy, MessageResolverAware
{
    private String clientId;
    private String metaDataKey;
    private Object metaDataValue;
    //TODO
    private MessageResolver messageResolver;
    private String messageTarget;

    public void setClientId(String clientId)
    {
        this.clientId = clientId;
    }

    public void setMetaData(String key, Object value)
    {
        this.metaDataKey = key;
        this.metaDataValue = value;
    }

    public void setViolationMessageTarget(String clientId)
    {
        this.messageTarget = clientId;
    }

    public String getClientScript()
    {
        String jsValidation = "true";
        if(CommonMetaDataKeys.MIN_LENGTH.equals(this.metaDataKey))
        {
            jsValidation = "return inputField.value && inputField.value.length >= " + this.metaDataValue;
        }
        else if(CommonMetaDataKeys.MAX_LENGTH.equals(this.metaDataKey))
        {
            jsValidation = "if(inputField.value) return inputField.value.length <= " + this.metaDataValue + ";"
                    + " else return true";
        }

        return "function validateLength() {" +
                "var inputField = document.getElementById('" + clientId + "');" +
                jsValidation + ";" +
                "};";
    }

    public String getOnViolationScript()
    {
        return "function onLengthViolation() {" +
                "var inputField = document.getElementById('" + clientId + "');" +
                "if(inputField){inputField.style.border = '1px solid red';}" +
                "var messageArea = document.getElementById('" + messageTarget + "');" +
                "if(messageArea){messageArea.innerHTML = 'wrong text length';}" +
                "};";
    }

    public void setMessageResolver(MessageResolver messageResolver)
    {
        this.messageResolver = messageResolver;
    }
}
