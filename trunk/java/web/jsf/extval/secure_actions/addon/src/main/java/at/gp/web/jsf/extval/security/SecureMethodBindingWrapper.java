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
package at.gp.web.jsf.extval.security;

import at.gp.web.jsf.extval.security.util.SecureActionUtils;

import javax.faces.component.StateHolder;
import javax.faces.component.UIComponentBase;
import javax.faces.context.FacesContext;
import javax.faces.el.EvaluationException;
import javax.faces.el.MethodBinding;
import javax.faces.el.MethodNotFoundException;

/**
 * @author Gerhard Petracek
 */
public class SecureMethodBindingWrapper extends MethodBinding implements StateHolder
{
    private MethodBinding originalMethodBinding;
    private boolean isTransient = false;

    public SecureMethodBindingWrapper()
    {
    }

    public SecureMethodBindingWrapper(MethodBinding originalMethodBinding)
    {
        this.originalMethodBinding = originalMethodBinding;
    }

    public String getExpressionString()
    {
        return originalMethodBinding.getExpressionString();
    }

    public Class getType(FacesContext facesContext)
            throws MethodNotFoundException
    {
        return originalMethodBinding.getType(facesContext);
    }

    public Object invoke(FacesContext facesContext, Object[] objects)
            throws EvaluationException, MethodNotFoundException
    {
        if (SecureActionUtils.allowAction(facesContext, getExpressionString(), false))
        {
            return originalMethodBinding.invoke(facesContext, objects);
        }
        return null;
    }

    public Object saveState(FacesContext facesContext)
    {
        return new Object[]
                {
                        UIComponentBase.saveAttachedState(facesContext, originalMethodBinding)
                        //maybe there will be further parts to save
                };
    }

    public void restoreState(FacesContext facesContext, Object states)
    {
        Object[] state = (Object[]) states;

        originalMethodBinding = (MethodBinding) UIComponentBase.restoreAttachedState(facesContext, state[0]);
    }

    public boolean isTransient()
    {
        return isTransient;
    }

    public void setTransient(boolean b)
    {
        isTransient = b;
    }

    public MethodBinding getOriginalMethodBinding()
    {
        return originalMethodBinding;
    }
}
