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

import at.gp.web.jsf.extval.beanval.form.util.FormValidationUtils;
import at.gp.web.jsf.extval.beanval.form.validation.FormBean;
import org.apache.myfaces.extensions.validator.core.storage.PropertyStorage;

import javax.validation.groups.Default;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Gerhard Petracek
 */
class DefaultFormBeanDescriptor implements EditableFormBeanDescriptor
{
    private Class<? extends FormBean> formBeanClass;

    private Set<String> conditions = new HashSet<String>();

    private Set<Class> groups = new HashSet<Class>();

    private boolean defaultGroupAddedAutomatically;

    public static FormBeanDescriptor create(Class<? extends FormBean> formBeanClass)
    {
        return create(formBeanClass, new String[]{});
    }

    public static FormBeanDescriptor create(Class<? extends FormBean> formBeanClass, String[] conditions, Class... groups)
    {
        return new DefaultFormBeanDescriptor(formBeanClass, conditions, groups);
    }

    private DefaultFormBeanDescriptor(Class<? extends FormBean> formBeanClass, String[] conditions, Class... groups)
    {
        this.formBeanClass = formBeanClass;

        PropertyStorage propertyStorage = FormValidationUtils.getFormValidationPropertyStorage();

        addFields(formBeanClass, propertyStorage);
        addMethods(formBeanClass, propertyStorage);

        update(conditions, groups);
    }

    private void addFields(Class<? extends FormBean> formBeanClass, PropertyStorage propertyStorage)
    {
        for (Field field : formBeanClass.getDeclaredFields())
        {
            if (!propertyStorage.containsField(formBeanClass, field.getName()))
            {
                propertyStorage.storeField(formBeanClass, field.getName(), field);
            }
        }
    }

    private void addMethods(Class<? extends FormBean> formBeanClass, PropertyStorage propertyStorage)
    {
        for (Method method : formBeanClass.getDeclaredMethods())
        {
            if (method.getName().startsWith("set") || method.getName().startsWith("add"))
            {
                if (!propertyStorage.containsMethod(formBeanClass, method.getName()))
                {
                    propertyStorage.storeMethod(formBeanClass, method.getName(), method);
                }
            }
        }
    }

    private void update(String[] conditions, Class... groups)
    {
        if (conditions != null)
        {
            Collections.addAll(this.conditions, conditions);
        }

        if (groups != null)
        {
            for (Class group : groups)
            {
                if (Default.class.isAssignableFrom(group))
                {
                    this.defaultGroupAddedAutomatically = false;
                }
                this.groups.add(group);
            }
        }

        restoreConsistency();
    }

    //to allow the generation of #equals and #hashCode
    private void restoreConsistency()
    {
        if (this.conditions.isEmpty())
        {
            this.conditions.add("#{true}");
        }

        if (this.groups.isEmpty())
        {
            this.groups.add(Default.class);
            this.defaultGroupAddedAutomatically = true;
        }
    }

    public Class<? extends FormBean> getFormBeanClass()
    {
        return this.formBeanClass;
    }

    public Field getField(String property)
    {
        PropertyStorage propertyStorage = FormValidationUtils.getFormValidationPropertyStorage();

        return propertyStorage.getField(getFormBeanClass(), property);
    }

    public Method getMethod(String property)
    {
        PropertyStorage propertyStorage = FormValidationUtils.getFormValidationPropertyStorage();

        return propertyStorage.getMethod(getFormBeanClass(), property);
    }

    public void addConditions(String[] conditions)
    {
        update(conditions);
    }

    public void addGroups(Class[] groups)
    {
        update(null, groups);
    }

    public String[] getConditions()
    {
        List<String> result = new ArrayList<String>();

        for (String condition : this.conditions)
        {
            if (!"#{true}".equals(condition))
            {
                result.add(condition);
            }
        }

        return result.toArray(new String[result.size()]);
    }

    public Class[] getGroups()
    {
        List<Class> result = new ArrayList<Class>();

        for (Class group : this.groups)
        {
            if (Default.class.isAssignableFrom(group) && this.defaultGroupAddedAutomatically)
            {
                continue;
            }
            result.add(group);
        }
        return result.toArray(new Class[result.size()]);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (!(o instanceof DefaultFormBeanDescriptor))
        {
            return false;
        }

        DefaultFormBeanDescriptor that = (DefaultFormBeanDescriptor) o;

        if (!conditions.equals(that.conditions))
        {
            return false;
        }
        if (!formBeanClass.equals(that.formBeanClass))
        {
            return false;
        }
        //noinspection RedundantIfStatement
        if (!groups.equals(that.groups))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = formBeanClass.hashCode();
        result = 31 * result + conditions.hashCode();
        result = 31 * result + groups.hashCode();
        return result;
    }
}
