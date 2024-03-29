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
package at.gp.web.jsf.extval.validation.message.resolver;

import org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.DefaultValidationErrorMessageResolver;

import java.util.Locale;

/**
 * Message Resolver per Validation Strategy
 *
 * @author Gerhard Petracek
 */
public class CustomRequiredValidationErrorMessageResolver implements MessageResolver
{
    private MessageResolver defaultMessageResolver = new DefaultValidationErrorMessageResolver();

    //delegates to the default resolver - no result -> use the key as text (= no i18n - it's just an example)
    public String getMessage(String key, Locale locale)
    {
        String message;

        message = this.defaultMessageResolver.getMessage(key, locale);

        if((message.startsWith("???") && message.endsWith("???")))
        {
            message = null;
        }

        return message == null ? key.replace("_detail", "").replace("_", " ") : message;
    }
}
