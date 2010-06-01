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
package at.gp.web.jsf.extval.beanval.form.startup;

import at.gp.web.jsf.extval.beanval.form.interceptor.FormRecordingRendererInterceptor;
import at.gp.web.jsf.extval.beanval.form.recorder.FormValidationUserInputRecorder;
import at.gp.web.jsf.extval.beanval.form.storage.*;
import org.apache.myfaces.extensions.validator.beanval.storage.mapper.BeanValidationGroupStorageNameMapper;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.factory.AbstractNameMapperAwareFactory;
import org.apache.myfaces.extensions.validator.core.factory.FactoryNames;
import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.core.storage.PropertyStorage;
import org.apache.myfaces.extensions.validator.core.storage.StorageManager;
import org.apache.myfaces.extensions.validator.core.storage.StorageManagerHolder;

/**
 * @author Gerhard Petracek
 */
public class FormValidationStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = -2271706096393237184L;

    protected void init()
    {
        ExtValContext.getContext().addProcessedInformationRecorder(new FormValidationUserInputRecorder());
        ExtValContext.getContext().registerRendererInterceptor(new FormRecordingRendererInterceptor());

        initStorageManagerAndNameMappers();
    }

    private void initStorageManagerAndNameMappers()
    {
        StorageManagerHolder storageManagerHolder =
                (ExtValContext.getContext()
                        .getFactoryFinder()
                        .getFactory(FactoryNames.STORAGE_MANAGER_FACTORY, StorageManagerHolder.class));

        //processed-information
        DefaultProcessedInformationStorageManager processedInfoStorageManager =
                new DefaultProcessedInformationStorageManager();
        processedInfoStorageManager.register(new ProcessedInformationStorageNameMapper());
        storageManagerHolder.setStorageManager(ProcessedInformationStorage.class, processedInfoStorageManager, false);

        //form-bean classes
        DefaultFormBeanClassStorageManager crossValidationStorageManager =
                new DefaultFormBeanClassStorageManager();
        crossValidationStorageManager.register(new FormBeanClassStorageNameMapper());
        storageManagerHolder.setStorageManager(FormBeanClassStorage.class, crossValidationStorageManager, false);

        //register name-mapper for FormValidationPropertyStorageNameMapper

        StorageManager storageManager = getStorageManagerHolder().getStorageManager(PropertyStorage.class);

        if (storageManager instanceof AbstractNameMapperAwareFactory)
        {
            ((AbstractNameMapperAwareFactory<String>) storageManager).register(new FormValidationPropertyStorageNameMapper());
        }
        else
        {
            this.logger.warning(storageManager.getClass().getName() +
                    " has to implement AbstractNameMapperAwareFactory " + getClass().getName() +
                    " couldn't register " + BeanValidationGroupStorageNameMapper.class.getName());
        }
    }

    protected StorageManagerHolder getStorageManagerHolder()
    {
        return (ExtValContext.getContext()
                .getFactoryFinder()
                .getFactory(FactoryNames.STORAGE_MANAGER_FACTORY, StorageManagerHolder.class));
    }

}
