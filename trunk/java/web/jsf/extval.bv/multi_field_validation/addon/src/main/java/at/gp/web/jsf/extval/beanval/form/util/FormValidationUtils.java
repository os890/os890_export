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
package at.gp.web.jsf.extval.beanval.form.util;

import at.gp.web.jsf.extval.beanval.form.storage.FormBeanClassStorage;
import at.gp.web.jsf.extval.beanval.form.storage.FormValidationPropertyStorageNameMapper;
import at.gp.web.jsf.extval.beanval.form.storage.ProcessedInformationStorage;
import at.gp.web.jsf.extval.beanval.form.validation.FormBean;
import at.gp.web.jsf.extval.beanval.form.validation.FormValidator;
import org.apache.myfaces.extensions.validator.core.storage.PropertyStorage;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

/**
 * @author Gerhard Petracek
 */
public class FormValidationUtils
{
    public static void registerFormValidators(Class beanClass)
    {
        registerFormValidators(getOrInitFormBeanClassStorage(), beanClass);
    }

    public static void registerFormValidators(FormBeanClassStorage formBeanClassStorage, Class<?> beanClass)
    {
        if (beanClass.isAnnotationPresent(FormValidator.class))
        {
            processFormValidator(formBeanClassStorage, beanClass, beanClass.getAnnotation(FormValidator.class));
        }

        if (beanClass.isAnnotationPresent(FormValidator.List.class))
        {
            for (FormValidator formValidator : beanClass.getAnnotation(FormValidator.List.class).value())
            {
                processFormValidator(formBeanClassStorage, beanClass, formValidator);
            }
        }
    }

    private static void processFormValidator(FormBeanClassStorage formBeanClassStorage, Class beanClass, FormValidator formValidator)
    {
        for (String viewId : formValidator.viewIds())
        {
            processFormBeans(formBeanClassStorage, viewId, beanClass, formValidator);
        }
    }

    private static void processFormBeans(FormBeanClassStorage formBeanClassStorage, String viewId, Class beanClass, FormValidator formValidator)
    {
        for(Class<? extends FormBean> currentFormBean : formValidator.formValidationBeanClass())
        {
            if (currentFormBean.equals(FormBean.class))
            {
                if (beanClass.isAssignableFrom(FormBean.class))
                {
                    //noinspection unchecked
                    formBeanClassStorage.add(beanClass, (Class<? extends FormBean>) beanClass, viewId, formValidator.conditions(), formValidator.groups());
                }
                else
                {
                    throw new IllegalStateException("FormValidator#responsibleFor has to ref. a class which in-/directly implements " + FormBean.class.getName());
                }
            }
            else
            {
                formBeanClassStorage.add(beanClass, currentFormBean, viewId, formValidator.conditions(), formValidator.groups());
            }
        }
    }

    public static ProcessedInformationStorage getOrInitProcessedInformationStorage()
    {
        return ExtValUtils.getStorage(ProcessedInformationStorage.class, ProcessedInformationStorage.class.getName());
    }

    public static FormBeanClassStorage getOrInitFormBeanClassStorage()
    {
        return ExtValUtils.getStorage(FormBeanClassStorage.class, FormBeanClassStorage.class.getName());
    }

    public static PropertyStorage getFormValidationPropertyStorage()
    {
        return ExtValUtils.getStorage(PropertyStorage.class, FormValidationPropertyStorageNameMapper.PROPERTY_STORYGE_KEY);
    }
}
