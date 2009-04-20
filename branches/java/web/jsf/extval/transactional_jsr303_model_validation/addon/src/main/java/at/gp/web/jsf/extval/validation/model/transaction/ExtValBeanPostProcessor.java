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
package at.gp.web.jsf.extval.validation.model.transaction;

import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.BeansException;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import at.gp.web.jsf.extval.validation.model.transaction.annotation.NoneTransactionalBean;
import at.gp.web.jsf.extval.validation.model.transaction.annotation.TransactionalBean;

@NoneTransactionalBean
public class ExtValBeanPostProcessor implements BeanPostProcessor
{
    private String markerClassName;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException
    {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException
    {
        if(bean == null || bean.getClass().isAnnotationPresent(NoneTransactionalBean.class))
        {
            return bean;
        }

        boolean secureBean = false;

        if(bean.getClass().isAnnotationPresent(TransactionalBean.class))
        {
            secureBean = true;
        }
        else if(this.markerClassName != null)
        {
            secureBean = analyzeMarkerClass(bean);
        }

        /*
        if(this.beanPatternExpression != null)
        {
            secureBean = analyzeBeanPattern(bean);
        }
        */

        return secureBean ? ExtValTransactionalModelValidationContext.getCurrentInstance().secureBean(bean) : bean;
    }

    private boolean analyzeMarkerClass(Object bean)
    {
        Class markerClass = ClassUtils.tryToLoadClassForName(this.markerClassName);

        if(markerClass == null)
        {
            return false;
        }

        if(markerClass.isAnnotation())
        {
            return bean.getClass().isAnnotationPresent(markerClass);
        }
        else
        {
            return bean.getClass().isAssignableFrom(markerClass);
        }
    }

    @ToDo(Priority.HIGH)
    private boolean analyzeBeanPattern(Object bean)
    {
        return false;
    }

    public void setMarkerClassName(String markerClassName)
    {
        this.markerClassName = markerClassName;
    }

    /*
    public void setBeanPatternExpression(String beanPatternExpression)
    {
        this.beanPatternExpression = beanPatternExpression;
    }
    */
}
