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
package at.gp.web.jsf.extval.validation;

import org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver;

import java.util.Locale;

/**
 * @author Gerhard Petracek
 */
public class CustomMessageResolver implements MessageResolver
{
    private MessageResolver wrapped = new org.apache.myfaces.extensions.validator.crossval.message.resolver.DefaultValidationErrorMessageResolver();

    public String getMessage(String key, Locale locale)
    {
        if(key.endsWith("detail"))
        {
            //the re-used target message of the current example doesn't include a label
            //"-1" ensures no duplicated values (if there are other msgs with e.g. {0})
            return "{-1} - " + this.wrapped.getMessage(key, locale);
        }
        else
        {
            return this.wrapped.getMessage(key, locale);
        }
    }
}
