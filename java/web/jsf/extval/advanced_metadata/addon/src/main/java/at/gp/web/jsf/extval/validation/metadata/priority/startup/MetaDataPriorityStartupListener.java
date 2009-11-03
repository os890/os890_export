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
package at.gp.web.jsf.extval.validation.metadata.priority.startup;

import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.factory.FactoryNames;
import org.apache.myfaces.extensions.validator.core.factory.NameMapperAwareFactory;
import org.apache.myfaces.extensions.validator.core.storage.StorageManagerHolder;
import org.apache.myfaces.extensions.validator.core.storage.FacesMessageStorage;
import at.gp.web.jsf.extval.validation.metadata.priority.interceptor.MetaDataPriorityInterceptor;
import at.gp.web.jsf.extval.validation.metadata.priority.PriorityFacesMessageStorageNameMapper;

/**
 * @author Gerhard Petracek
 */
public class MetaDataPriorityStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = 395947458264828232L;

    protected void init()
    {
        if(logger.isInfoEnabled())
        {
            logger.info("adding support for @MetaDataProvider");
        }

        ExtValContext.getContext().addPropertyValidationInterceptor(new MetaDataPriorityInterceptor());

        customizeFacesMessageStorage();
    }

    @SuppressWarnings({"unchecked"})
    private void customizeFacesMessageStorage()
    {
        StorageManagerHolder storageManagerHolder =
                (ExtValContext.getContext()
                .getFactoryFinder()
                .getFactory(FactoryNames.STORAGE_MANAGER_FACTORY, StorageManagerHolder.class));

        ((NameMapperAwareFactory)storageManagerHolder.getStorageManager(FacesMessageStorage.class))
                .register(new PriorityFacesMessageStorageNameMapper());
    }
}
