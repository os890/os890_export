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
package at.gp.web.jsf.extval.validation.model.transactional.util;

import org.apache.myfaces.extensions.validator.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
public class ModelValidationUtils
{
    public static Object invokeGetter(Object baseObject, String propertyName)
    {
        propertyName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

        Method method = ReflectionUtils.tryToGetMethod(baseObject.getClass(), "get" + propertyName);

        if (method == null)
        {
            method = ReflectionUtils.tryToGetMethod(baseObject.getClass(), "is" + propertyName);
        }

        return ReflectionUtils.tryToInvokeMethod(baseObject, method);
    }

    public static void invokeSetter(Object baseObject, String propertyName, Object parameter)
    {
        propertyName = propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);

        Method method = ReflectionUtils.tryToGetMethod(baseObject.getClass(), "get" + propertyName);

        if (method == null)
        {
            method = ReflectionUtils.tryToGetMethod(baseObject.getClass(), "is" + propertyName);
        }

        Class targetType = method.getReturnType();

        ReflectionUtils.tryToInvokeMethod(baseObject, getSetterMethod(baseObject.getClass(), "set" + propertyName, targetType), parameter);
    }

    private static Method getSetterMethod(Class targetClass, String methodName, Class targetType)
    {
        try
        {
            return targetClass.getMethod(methodName, targetType);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalStateException("add-on restriction - please report and/or provide a fix", e);
        }
    }
}
