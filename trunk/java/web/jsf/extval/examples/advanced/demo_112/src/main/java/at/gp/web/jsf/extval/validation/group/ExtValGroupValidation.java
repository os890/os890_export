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
package at.gp.web.jsf.extval.validation.group;

import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

public class ExtValGroupValidation implements Validator, StateHolder
{
    private boolean isTransient;
    private String value;

    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException
    {
        //do nothing - just used for groups
    }

    public String getValue()
    {
        return value;
    }

    public void setValue(String value)
    {
        this.value = value;
    }

    public Object saveState(FacesContext facesContext)
    {
        Object values[] = new Object[1];
        values[0] = this.value;
        return values;
    }

    public void restoreState(FacesContext facesContext, Object state)
    {
        Object values[] = (Object[])state;
		this.value = (String) values[0];
    }

    public boolean isTransient()
    {
        return this.isTransient;
    }

    public void setTransient(boolean isTransient)
    {
        this.isTransient = isTransient;
    }
}
