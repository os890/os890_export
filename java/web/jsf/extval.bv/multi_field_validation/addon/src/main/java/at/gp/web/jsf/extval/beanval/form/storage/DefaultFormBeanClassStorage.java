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
package at.gp.web.jsf.extval.beanval.form.storage;

import at.gp.web.jsf.extval.beanval.form.validation.FormBean;
import org.apache.myfaces.extensions.validator.core.el.ELHelper;
import org.apache.myfaces.extensions.validator.core.el.ValueBindingExpression;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.context.FacesContext;
import java.util.*;

/**
 * @author Gerhard Petracek
 */
public class DefaultFormBeanClassStorage implements FormBeanClassStorage
{
    private Map<Class, Set<FormBeanDescriptor>> formBeanClassMapping = new HashMap<Class, Set<FormBeanDescriptor>>();

    public void add(Class beanClass, Class<? extends FormBean> formBeanClass)
    {
        add(beanClass, formBeanClass, "*");
    }

    public void add(Class beanClass, Class<? extends FormBean> formBeanClass, String viewId)
    {
        add(beanClass, formBeanClass, viewId, new String[]{});
    }

    public void add(Class beanClass, Class<? extends FormBean> formBeanClass, String viewId, String[] conditions, Class... groups)
    {
        if (!isCompatibleViewId(FacesContext.getCurrentInstance(), viewId))
        {
            return;
        }

        if (!this.formBeanClassMapping.containsKey(beanClass))
        {
            this.formBeanClassMapping.put(beanClass, new HashSet<FormBeanDescriptor>());
        }

        FormBeanDescriptor formBeanDescriptor = DefaultFormBeanDescriptor.create(formBeanClass, conditions, groups);
        Set<FormBeanDescriptor> formBeanDescriptors = this.formBeanClassMapping.get(beanClass);
        if (!formBeanDescriptors.contains(formBeanDescriptor))
        {
            formBeanDescriptors.add(formBeanDescriptor);
        }
    }

    public List<FormBeanDescriptor> getFormBeanClasses(Class beanClass)
    {
        if (this.formBeanClassMapping.containsKey(beanClass))
        {
            List<FormBeanDescriptor> result = new ArrayList<FormBeanDescriptor>();
            result.addAll(this.formBeanClassMapping.get(beanClass));

            findFormBeansForValidation(result);

            return Collections.unmodifiableList(result);
        }

        return Collections.emptyList();
    }

    private List<FormBeanDescriptor> findFormBeansForValidation(List<FormBeanDescriptor> formBeanDescriptors)
    {
        List<FormBeanDescriptor> result = new ArrayList<FormBeanDescriptor>();
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELHelper elHelper = ExtValUtils.getELHelper();

        for (FormBeanDescriptor formBeanDescriptor : formBeanDescriptors)
        {
            if (isFormBeanViewDescriptorCompatible(facesContext, elHelper, formBeanDescriptor))
            {
                result.add(formBeanDescriptor);
            }
        }
        return result;
    }

    private boolean isFormBeanViewDescriptorCompatible(FacesContext facesContext, ELHelper elHelper, FormBeanDescriptor formBeanDescriptor)
    {
        return allConditionsValid(facesContext, elHelper, formBeanDescriptor.getConditions());
    }

    private boolean isCompatibleViewId(FacesContext facesContext, String viewId)
    {
        return (facesContext.getViewRoot() != null && facesContext.getViewRoot().getViewId().equals(viewId)) || "*".equals(viewId);
    }

    private boolean allConditionsValid(FacesContext facesContext, ELHelper elHelper, String[] conditions)
    {
        for (String condition : conditions)
        {
            if (Boolean.FALSE.equals(elHelper.getValueOfExpression(facesContext, new ValueBindingExpression(condition))))
            {
                return false;
            }
        }

        return true;
    }
}