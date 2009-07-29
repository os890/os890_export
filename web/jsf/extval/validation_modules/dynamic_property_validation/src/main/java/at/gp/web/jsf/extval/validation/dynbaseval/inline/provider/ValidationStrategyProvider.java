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
package at.gp.web.jsf.extval.validation.dynbaseval.inline.provider;

import org.apache.myfaces.extensions.validator.core.validation.parameter.ValidationParameter;
import org.apache.myfaces.extensions.validator.core.validation.parameter.ParameterKey;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
public class ValidationStrategyProvider implements ValidationParameter
{
    @ParameterKey
    public Class getKey()
    {
        return ValidationStrategyProvider.class;
    }

    /**
     * it isn't allowed to have an abstract class in this case - so a dummy impl. is required
     */
    public ValidationStrategy getValidationStrategy()
    {
        throw new IllegalStateException("you have to override this method");
    }
}