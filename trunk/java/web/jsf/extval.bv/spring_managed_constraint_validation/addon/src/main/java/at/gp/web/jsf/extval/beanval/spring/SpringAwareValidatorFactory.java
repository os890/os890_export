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
package at.gp.web.jsf.extval.beanval.spring;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.apache.myfaces.extensions.validator.beanval.util.BeanValidationUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.validation.*;

/**
 * @author Gerhard Petracek
 */
public class SpringAwareValidatorFactory implements ValidatorFactory
{
    private ValidatorFactory validatorFactory;
    private SpringApplicationContextProvider applicationContextProvider;

    public SpringAwareValidatorFactory()
    {
        this.validatorFactory = BeanValidationUtils.getDefaultValidatorFactory();
        this.applicationContextProvider = (SpringApplicationContextProvider) ExtValUtils.getELHelper().getBean("springApplicationContextProvider");
    }

    public Validator getValidator()
    {
        return this.validatorFactory.getValidator();
    }

    public ValidatorContext usingContext()
    {
        return this.validatorFactory.usingContext();
    }

    public MessageInterpolator getMessageInterpolator()
    {
        return this.validatorFactory.getMessageInterpolator();
    }

    public TraversableResolver getTraversableResolver()
    {
        return this.validatorFactory.getTraversableResolver();
    }

    public ConstraintValidatorFactory getConstraintValidatorFactory()
    {
        return new ConstraintValidatorFactory()
        {
            @SuppressWarnings({"unchecked"})
            public <T extends ConstraintValidator<?, ?>> T getInstance(Class<T> targetClass)
            {
                Object result = null;

                try
                {
                    result = applicationContextProvider.getApplicationContext().getBean(targetClass);
                }
                catch (NoSuchBeanDefinitionException e)
                {
                    //do nothing
                }

                if(result == null)
                {
                    return validatorFactory.getConstraintValidatorFactory().getInstance(targetClass);
                }
                return (T)result;
            }
        };
    }

    public <T> T unwrap(Class<T> tClass)
    {
        return this.validatorFactory.unwrap(tClass);
    }
}
