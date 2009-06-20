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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Gerhard Petracek
 * @since 1.x.3
 */
public class TransactionalModelValidationContext
{
    protected final Log logger = LogFactory.getLog(getClass());

    public static final String KEY = TransactionalModelValidationContext.class.getName();
    private List<ModelValidationEntry> modelValidationEntries = new ArrayList<ModelValidationEntry>();
    private List<RevertableProperty> revertableProperties = new ArrayList<RevertableProperty>();

    private TransactionalModelValidationContext()
    {
    }

    public static TransactionalModelValidationContext getContext()
    {
        Map requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestMap();

        if (!requestMap.containsKey(KEY))
        {
            requestMap.put(KEY, new TransactionalModelValidationContext());
        }

        return (TransactionalModelValidationContext) requestMap.get(KEY);
    }

    public void addModelValidationEntry(ModelValidationEntry modelValidationEntry)
    {
        this.modelValidationEntries.add(modelValidationEntry);
    }

    public void addRevertableProperty(RevertableProperty revertableProperty)
    {
        this.revertableProperties.add(revertableProperty);
    }

    public List<ModelValidationEntry> getModelValidationEntries()
    {
        return modelValidationEntries;
    }

    public void rollback()
    {
        if(this.logger.isDebugEnabled())
        {
            this.logger.debug("revert new model values");
        }
        
        for (RevertableProperty revertableProperty : this.revertableProperties)
        {
            revertableProperty.revert();
        }
    }
}
