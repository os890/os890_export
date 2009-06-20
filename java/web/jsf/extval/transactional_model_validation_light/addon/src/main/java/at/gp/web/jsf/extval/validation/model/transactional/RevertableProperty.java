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
package at.gp.web.jsf.extval.validation.model.transactional;

import javax.faces.component.EditableValueHolder;
import at.gp.web.jsf.extval.validation.model.transactional.util.ModelValidationUtils;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
public class RevertableProperty
{
    EditableValueHolder component;
    private Object oldValue;
    private Object newValue;
    private Object baseObject;
    private String propertyName;

    public RevertableProperty(EditableValueHolder component, Object oldValue, Object newValue, Object baseObject, String propertyName)
    {
        this.component = component;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.baseObject = baseObject;
        this.propertyName = propertyName;
    }

    public void revert()
    {
        //to the following manually to support complex components...
        ModelValidationUtils.invokeSetter(this.baseObject, this.propertyName, this.oldValue);

        //TODO
        this.component.setSubmittedValue(this.newValue);
        this.component.setValid(false);
    }
}
