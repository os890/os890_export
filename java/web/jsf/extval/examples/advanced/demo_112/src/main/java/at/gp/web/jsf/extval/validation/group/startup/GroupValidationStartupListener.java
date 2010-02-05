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
package at.gp.web.jsf.extval.validation.group.startup;

import at.gp.web.jsf.extval.validation.group.Group;
import at.gp.web.jsf.extval.validation.group.interceptor.SimpleGroupValidationInterceptor;
import at.gp.web.jsf.extval.validation.group.interceptor.SimpleGroupValidationMetaDataExtractionInterceptor;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.factory.AbstractNameMapperAwareFactory;
import org.apache.myfaces.extensions.validator.core.factory.FactoryNames;
import org.apache.myfaces.extensions.validator.core.mapper.NameMapper;
import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.storage.DefaultGroupStorage;
import org.apache.myfaces.extensions.validator.core.storage.GroupStorage;
import org.apache.myfaces.extensions.validator.core.storage.StorageManager;
import org.apache.myfaces.extensions.validator.core.storage.StorageManagerHolder;

public class GroupValidationStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = 2306693064667956047L;

    protected void init()
    {
        ExtValContext.getContext().addPropertyValidationInterceptor(new SimpleGroupValidationInterceptor());
        ExtValContext.getContext().addMetaDataExtractionInterceptor(new SimpleGroupValidationMetaDataExtractionInterceptor());

        setupNamedGroupStorage();
    }

    @SuppressWarnings({"unchecked"})
    private void setupNamedGroupStorage()
    {
        StorageManagerHolder storageManagerHolder =
                (ExtValContext.getContext()
                        .getFactoryFinder()
                        .getFactory(FactoryNames.STORAGE_MANAGER_FACTORY, StorageManagerHolder.class));

        StorageManager storageManager = storageManagerHolder.getStorageManager(GroupStorage.class);

        if (storageManager instanceof AbstractNameMapperAwareFactory)
        {
            ((AbstractNameMapperAwareFactory<String>) storageManager)
                    .register(new NameMapper<String>()
                    {

                        public String createName(String source)
                        {
                            return (Group.class.getName().equals(source)) ?
                                    DefaultGroupStorage.class.getName() : null;
                        }
                    });
        }
    }
}
