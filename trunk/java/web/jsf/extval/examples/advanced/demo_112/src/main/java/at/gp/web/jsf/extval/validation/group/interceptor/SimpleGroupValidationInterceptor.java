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
package at.gp.web.jsf.extval.validation.group.interceptor;

import at.gp.web.jsf.extval.validation.group.ExtValGroupValidation;
import at.gp.web.jsf.extval.validation.group.Group;
import org.apache.myfaces.extensions.validator.core.interceptor.PropertyValidationInterceptor;
import org.apache.myfaces.extensions.validator.core.storage.GroupStorage;
import org.apache.myfaces.extensions.validator.internal.UsageCategory;
import org.apache.myfaces.extensions.validator.internal.UsageInformation;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;

import javax.faces.component.EditableValueHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@UsageInformation(UsageCategory.INTERNAL)
public class SimpleGroupValidationInterceptor implements PropertyValidationInterceptor
{
    public boolean beforeValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, Map<String, Object> properties)
    {
        EditableValueHolder inputComponent = (EditableValueHolder) uiComponent;
        List<Class> groupsToValidate = new ArrayList<Class>();

        String groupName;
        Class group;
        for (Validator validator : inputComponent.getValidators())
        {
            if (validator instanceof ExtValGroupValidation)
            {
                groupName = ((ExtValGroupValidation) validator).getValue().trim();
                group = ClassUtils.tryToLoadClassForName(groupName);

                if (group == null || !(Group.class.isAssignableFrom(group)))
                {
                    throw new IllegalStateException(groupName + " is no valid group");
                }

                groupsToValidate.add(group);
            }
        }

        GroupStorage groupStorage = ExtValUtils.getStorage(GroupStorage.class, Group.class.getName());

        for (Class currentGroup : groupsToValidate)
        {
            //use a dummy id to re-use existing implementations
            groupStorage.addGroup(currentGroup, FacesContext.getCurrentInstance().getViewRoot().getViewId(), "[current]");
        }
        return true;
    }

    public void afterValidation(FacesContext facesContext, UIComponent uiComponent, Object convertedObject, Map<String, Object> properties)
    {
        ExtValUtils.resetStorage(GroupStorage.class, Group.class.getName());
    }
}
