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
package at.gp.web.jsf.extval.config.java;

import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer;
import org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy;
import org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver;
import org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder;
import org.apache.myfaces.extensions.validator.core.interceptor.RendererInterceptor;
import org.apache.myfaces.extensions.validator.core.interceptor.MetaDataExtractionInterceptor;
import org.apache.myfaces.extensions.validator.core.interceptor.ValidationExceptionInterceptor;
import org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticInMemoryConfiguration;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticConfigurationNames;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.util.ClassUtils;

/**
 * @author Gerhard Petracek
 */
public abstract class AbstractExtValModule
{
    private ExtValContext extValContext;
    private ExtValConfig config = new DefaultExtValConfig();

    public AbstractExtValModule()
    {
        this.extValContext = ExtValContext.getContext();
    }

    public AbstractExtValModule(ExtValConfig extValConfig)
    {
        this();
        this.config = extValConfig;
    }

    protected abstract void configure();

    void install()
    {
        invokeSetupListenersBefore();
        configure();
        configureExtVal();
        invokeSetupListenersAfter();
    }

    public AbstractExtValModule add(Class... classesToAdd)
    {
        invokeUpdateListenersBefore();

        for(Class classToAdd : classesToAdd)
        {
            addClass(classToAdd);
        }

        invokeUpdateListenersAfter();

        return this;
    }

    public AbstractExtValModule instance(Object... instancesToAdd)
    {
        invokeUpdateListenersBefore();

        for(Object instanceToAdd : instancesToAdd)
        {
            addInstance(instanceToAdd);
        }

        invokeUpdateListenersAfter();

        return this;
    }

    public Binding bind(Class classToMap)
    {
        invokeUpdateListenersBefore();

        Binding bindingEntry = this.config.createBinding(new ConfigEntry(classToMap));
        bindingEntry.setCallback(new Command() {

            public void execute()
            {
                invokeUpdateListenersAfter();
            }
        });

        return bindingEntry;
    }

    public void addConfigurationListeners(ConfigurationListener... configurationListeners)
    {
        for(ConfigurationListener configurationListener : configurationListeners)
        {
            this.config.addConfigurationListener(configurationListener);
        }
    }

    private void addClass(Class classToAdd)
    {
        addInstance(ClassUtils.tryToInstantiateClass(classToAdd));
    }

    private void addInstance(Object instanceToAdd)
    {
        if(instanceToAdd instanceof ComponentInitializer)
        {
            this.config.addComponentInitializer((ComponentInitializer) instanceToAdd);
        }
        else if(instanceToAdd instanceof RendererInterceptor)
        {
            this.config.addRendererInterceptor((RendererInterceptor) instanceToAdd);
        }
        else if(instanceToAdd instanceof MetaDataExtractionInterceptor)
        {
            this.config.addMetaDataExtractionInterceptor((MetaDataExtractionInterceptor) instanceToAdd);
        }
        else if(instanceToAdd instanceof ProcessedInformationRecorder)
        {
            this.config.addProcessedInformationRecorder((ProcessedInformationRecorder) instanceToAdd);
        }
        else if(instanceToAdd instanceof ValidationExceptionInterceptor)
        {
            this.config.addValidationExceptionInterceptor((ValidationExceptionInterceptor) instanceToAdd);
        }
    }

    private void configureExtVal()
    {
        invokeInitListenersBefore();

        addComponentInitializers();
        addRendererInterceptors();
        addMetaDataExtractionInterceptors();
        addProcessedInformationRecorders();
        addValidationExceptionInterceptors();

        addValidationStrategies();
        addMessageResolvers();
        addMetaDataTransformers();

        invokeInitListenersAfter();
    }

    private void addComponentInitializers()
    {
        for(ComponentInitializer initializer : this.config.getComponentInitializers())
        {
            this.extValContext.addComponentInitializer(initializer);
        }
    }

    private void addRendererInterceptors()
    {
        for(RendererInterceptor interceptor : this.config.getRendererInterceptors())
        {
            this.extValContext.registerRendererInterceptor(interceptor);
        }
    }

    private void addMetaDataExtractionInterceptors()
    {
        for(MetaDataExtractionInterceptor interceptor : this.config.getMetaDataExtractionInterceptors())
        {
            this.extValContext.addMetaDataExtractionInterceptor(interceptor);
        }
    }

    private void addProcessedInformationRecorders()
    {
        for(ProcessedInformationRecorder recorder : this.config.getProcessedInformationRecorders())
        {
            this.extValContext.addProcessedInformationRecorder(recorder);
        }
    }

    private void addValidationExceptionInterceptors()
    {
        for(ValidationExceptionInterceptor interceptor : this.config.getValidationExceptionInterceptors())
        {
            this.extValContext.addValidationExceptionInterceptor(interceptor);
        }
    }

    private void addValidationStrategies()
    {
        StaticInMemoryConfiguration staticConfig;
        String targetAnnotationName;
        String targetValidationStrategyName;

        for(Binding binding : config.getBindings())
        {
            staticConfig = new StaticInMemoryConfiguration();

            if(ValidationStrategy.class.isAssignableFrom(binding.getFrom().value()))
            {
                targetValidationStrategyName = binding.getFrom().value().getName();

                for(ConfigEntry targetEntry : binding.getTo())
                {
                    targetAnnotationName = null;
                    if(targetEntry.value().isAnnotation())
                    {
                        targetAnnotationName = targetEntry.value().getName();
                    }

                    if(targetAnnotationName != null && targetValidationStrategyName != null)
                    {
                        staticConfig.addMapping(targetAnnotationName, targetValidationStrategyName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG, staticConfig);
                    }
                }
            }
            else if(binding.getFrom().value().isAnnotation())
            {
                targetAnnotationName = binding.getFrom().value().getName();

                for(ConfigEntry targetEntry : binding.getTo())
                {
                    targetValidationStrategyName = null;
                    if(ValidationStrategy.class.isAssignableFrom(targetEntry.value()))
                    {
                        targetValidationStrategyName = targetEntry.value().getName();
                    }

                    if(targetAnnotationName != null && targetValidationStrategyName != null)
                    {
                        staticConfig.addMapping(targetAnnotationName, targetValidationStrategyName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG, staticConfig);
                    }
                }
            }
        }
    }

    private void addMessageResolvers()
    {
        StaticInMemoryConfiguration staticConfig;
        String targetMessageResolverName;
        String targetValidationStrategyName;

        for(Binding binding : config.getBindings())
        {
            staticConfig = new StaticInMemoryConfiguration();

            if(ValidationStrategy.class.isAssignableFrom(binding.getFrom().value()))
            {
                targetValidationStrategyName = binding.getFrom().value().getName();

                for(ConfigEntry targetEntry : binding.getTo())
                {
                    targetMessageResolverName = null;
                    if(MessageResolver.class.isAssignableFrom(targetEntry.value()))
                    {
                        targetMessageResolverName = targetEntry.value().getName();
                    }

                    if(targetMessageResolverName != null && targetValidationStrategyName != null)
                    {
                        staticConfig.addMapping(targetValidationStrategyName, targetMessageResolverName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_CONFIG, staticConfig);
                    }
                }
            }
            else if(MessageResolver.class.isAssignableFrom(binding.getFrom().value()))
            {
                targetMessageResolverName = binding.getFrom().value().getName();

                for(ConfigEntry targetEntry : binding.getTo())
                {
                    targetValidationStrategyName = null;
                    if(ValidationStrategy.class.isAssignableFrom(targetEntry.value()))
                    {
                        targetValidationStrategyName = targetEntry.value().getName();
                    }

                    if(targetMessageResolverName != null && targetValidationStrategyName != null)
                    {
                        staticConfig.addMapping(targetValidationStrategyName, targetMessageResolverName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_CONFIG, staticConfig);
                    }
                }
            }
        }
    }

    @ToDo(Priority.BLOCKING)
    private void addMetaDataTransformers()
    {
        StaticInMemoryConfiguration staticConfig;
        String targetMetaDataTransformerName;
        String targetValidationStrategyName;

        for(Binding binding : config.getBindings())
        {
            staticConfig = new StaticInMemoryConfiguration();

            if(ValidationStrategy.class.isAssignableFrom(binding.getFrom().value()))
            {
                targetValidationStrategyName = binding.getFrom().value().getName();

                for(ConfigEntry targetEntry : binding.getTo())
                {
                    targetMetaDataTransformerName = null;
                    if(MetaDataTransformer.class.isAssignableFrom(targetEntry.value()))
                    {
                        targetMetaDataTransformerName = targetEntry.value().getName();
                    }

                    if(targetMetaDataTransformerName != null && targetValidationStrategyName != null)
                    {
                        staticConfig.addMapping(targetValidationStrategyName, targetMetaDataTransformerName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_META_DATA_TRANSFORMER_CONFIG, staticConfig);
                    }
                }
            }
            else if(MetaDataTransformer.class.isAssignableFrom(binding.getFrom().value()))
            {
                targetMetaDataTransformerName = binding.getFrom().value().getName();

                for(ConfigEntry targetEntry : binding.getTo())
                {
                    targetValidationStrategyName = null;
                    if(ValidationStrategy.class.isAssignableFrom(targetEntry.value()))
                    {
                        targetValidationStrategyName = targetEntry.value().getName();
                    }

                    if(targetMetaDataTransformerName != null && targetValidationStrategyName != null)
                    {
                        staticConfig.addMapping(targetValidationStrategyName, targetMetaDataTransformerName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_META_DATA_TRANSFORMER_CONFIG, staticConfig);
                    }
                }
            }
        }
    }

    private void invokeSetupListenersBefore()
    {
        invokeListeners(new BeforeCommand(), ConfigurationListener.Type.SETUP);
    }

    private void invokeSetupListenersAfter()
    {
        invokeListeners(new AfterCommand(), ConfigurationListener.Type.SETUP);
    }

    private void invokeUpdateListenersBefore()
    {
        invokeListeners(new BeforeCommand(), ConfigurationListener.Type.UPDATE);
    }

    private void invokeUpdateListenersAfter()
    {
        invokeListeners(new AfterCommand(), ConfigurationListener.Type.UPDATE);
    }

    private void invokeInitListenersBefore()
    {
        invokeListeners(new BeforeCommand(), ConfigurationListener.Type.INIT);
    }

    private void invokeInitListenersAfter()
    {
        invokeListeners(new AfterCommand(), ConfigurationListener.Type.INIT);
    }

    private void invokeListeners(ConfigurationListenerAwareCommand command, ConfigurationListener.Type type)
    {
        for(ConfigurationListener configurationListener : this.config.getConfigurationListeners())
        {
            for(ConfigurationListener.Type currentType : configurationListener.getListenerTypes())
            {
                if(type.equals(currentType))
                {
                    command.listener(configurationListener).module(this).execute();
                    break;
                }
            }
        }
    }
}
