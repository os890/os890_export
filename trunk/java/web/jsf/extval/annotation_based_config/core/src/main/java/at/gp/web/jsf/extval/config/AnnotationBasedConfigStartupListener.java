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
package at.gp.web.jsf.extval.config;

import at.gp.web.jsf.extval.config.annotation.AdvancedValidationStrategy;
import at.gp.web.jsf.extval.config.annotation.ComponentInitializer;
import at.gp.web.jsf.extval.config.annotation.MessageResolver;
import at.gp.web.jsf.extval.config.annotation.MetaDataTransformer;
import at.gp.web.jsf.extval.config.annotation.ProcessedInformationRecorder;
import at.gp.web.jsf.extval.config.annotation.RendererInterceptor;
import at.gp.web.jsf.extval.config.annotation.StartupListener;
import at.gp.web.jsf.extval.config.annotation.ValidationStrategy;
import at.gp.web.jsf.extval.config.annotation.MetaDataValidationStrategy;
import org.apache.myfaces.extensions.validator.core.CustomInfo;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.interceptor.AbstractRendererInterceptor;
import org.apache.myfaces.extensions.validator.core.loader.StaticInMemoryMappingConfig;
import org.apache.myfaces.extensions.validator.core.loader.StaticMappingConfigLoaderNames;
import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.scannotation.AnnotationDB;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public class AnnotationBasedConfigStartupListener extends AbstractStartupListener
{
    protected void init()
    {
        AnnotationDB annotationDB = getAnnotationScanner();

        ClassLoader result = getClass().getClassLoader();

        try
        {
            String baseResource = getBasePackage();
            Enumeration<URL> urls = result.getResources(baseResource);

            while (urls.hasMoreElements())
            {
                annotationDB.scanArchives(findResourceBase(urls.nextElement(), baseResource));
            }
        }
        catch (IOException e)
        {
            logger.warn("annotation based config failed", e);
        }

        addAdvancedValidationStrategies(annotationDB);
        addValidationStrategies(annotationDB);
        addMetaDataValidationStrategies(annotationDB);
        addMessageResolvers(annotationDB);
        addMetaDataTransformers(annotationDB);
        addStartupListeners(annotationDB);
        addComponentInitializers(annotationDB);
        addProcessedInformationRecorders(annotationDB);
        addRendererInterceptors(annotationDB);
    }

    protected AnnotationDB getAnnotationScanner()
    {
        AnnotationDB annotationDB = new AnnotationDB();

        annotationDB.setScanClassAnnotations(true);
        annotationDB.setScanFieldAnnotations(false);
        annotationDB.setScanMethodAnnotations(false);
        annotationDB.setScanParameterAnnotations(false);

        return annotationDB;
    }

    protected String getBasePackage()
    {
        return ExtValContext.getContext().getInformationProviderBean().get(CustomInfo.BASE_PACKAGE).replace(".", "/");
    }

    private void addAdvancedValidationStrategies(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(AdvancedValidationStrategy.class.getName());

        if(result == null)
        {
            return;
        }

        AdvancedValidationStrategy currentAnnotation;
        StaticInMemoryMappingConfig config;
        for (String validationStrategyName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(validationStrategyName).getDeclaredAnnotations())
            {
                if (annotation instanceof AdvancedValidationStrategy)
                {
                    currentAnnotation = (AdvancedValidationStrategy) annotation;

                    for (Class targetAnnotation : currentAnnotation.value())
                    {
                        config = new StaticInMemoryMappingConfig();
                        config.addMapping(targetAnnotation.getName(), validationStrategyName);
                        ExtValContext.getContext().addStaticMappingConfigLoader(StaticMappingConfigLoaderNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG_LOADER, config);
                    }

                    config = new StaticInMemoryMappingConfig();
                    config.addMapping(validationStrategyName, currentAnnotation.messageResolverClass().getName());
                    ExtValContext.getContext().addStaticMappingConfigLoader(StaticMappingConfigLoaderNames.VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_CONFIG_LOADER, config);

                    config = new StaticInMemoryMappingConfig();
                    config.addMapping(validationStrategyName, currentAnnotation.metaDataTransformerClass().getName());
                    ExtValContext.getContext().addStaticMappingConfigLoader(StaticMappingConfigLoaderNames.VALIDATION_STRATEGY_TO_META_DATA_TRANSFORMER_CONFIG_LOADER, config);
                }
            }
        }
    }

    private void addValidationStrategies(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(ValidationStrategy.class.getName());

        if(result == null)
        {
            return;
        }

        ValidationStrategy currentAnnotation;
        StaticInMemoryMappingConfig config;
        for (String validationStrategyName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(validationStrategyName).getDeclaredAnnotations())
            {
                if (annotation instanceof ValidationStrategy)
                {
                    currentAnnotation = (ValidationStrategy) annotation;

                    for (Class targetAnnotation : currentAnnotation.value())
                    {
                        config = new StaticInMemoryMappingConfig();
                        config.addMapping(targetAnnotation.getName(), validationStrategyName);
                        ExtValContext.getContext().addStaticMappingConfigLoader(StaticMappingConfigLoaderNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG_LOADER, config);
                    }
                }
            }
        }
    }

    private void addMetaDataValidationStrategies(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(MetaDataValidationStrategy.class.getName());

        if(result == null)
        {
            return;
        }

        MetaDataValidationStrategy currentAnnotation;
        StaticInMemoryMappingConfig config;
        for (String validationStrategyName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(validationStrategyName).getDeclaredAnnotations())
            {
                if (annotation instanceof MetaDataValidationStrategy)
                {
                    currentAnnotation = (MetaDataValidationStrategy) annotation;

                    for (String metaDataKey : currentAnnotation.value())
                    {
                        config = new StaticInMemoryMappingConfig();
                        config.addMapping(metaDataKey, validationStrategyName);
                        ExtValContext.getContext().addStaticMappingConfigLoader(StaticMappingConfigLoaderNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG_LOADER, config);
                    }
                }
            }
        }
    }

    private void addMessageResolvers(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(MessageResolver.class.getName());

        if(result == null)
        {
            return;
        }

        MessageResolver currentAnnotation;
        StaticInMemoryMappingConfig config;
        for (String messageResolverName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(messageResolverName).getDeclaredAnnotations())
            {
                if (annotation instanceof MessageResolver)
                {
                    currentAnnotation = (MessageResolver) annotation;

                    for (Class validationStrategy : currentAnnotation.validationStrategyClasses())
                    {
                        config = new StaticInMemoryMappingConfig();
                        config.addMapping(validationStrategy.getName(), messageResolverName);
                        ExtValContext.getContext().addStaticMappingConfigLoader(StaticMappingConfigLoaderNames.VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_CONFIG_LOADER, config);
                    }
                }
            }
        }
    }

    private void addMetaDataTransformers(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(MetaDataTransformer.class.getName());

        if(result == null)
        {
            return;
        }

        MetaDataTransformer currentAnnotation;
        StaticInMemoryMappingConfig config;
        for (String metaDataTransformerName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(metaDataTransformerName).getDeclaredAnnotations())
            {
                if (annotation instanceof MetaDataTransformer)
                {
                    currentAnnotation = (MetaDataTransformer) annotation;

                    for (Class validationStrategy : currentAnnotation.validationStrategyClasses())
                    {
                        config = new StaticInMemoryMappingConfig();
                        config.addMapping(validationStrategy.getName(), metaDataTransformerName);
                        ExtValContext.getContext().addStaticMappingConfigLoader(StaticMappingConfigLoaderNames.VALIDATION_STRATEGY_TO_META_DATA_TRANSFORMER_CONFIG_LOADER, config);
                    }
                }
            }
        }
    }

    private void addStartupListeners(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(StartupListener.class.getName());

        if(result == null)
        {
            return;
        }

        Object startupListener;
        Method initMethod;
        for (String startupListenerName : result)
        {
            startupListener = ClassUtils.tryToInstantiateClassForName(startupListenerName);

            if (startupListener == null || !(startupListener instanceof AbstractStartupListener))
            {
                continue;
            }

            try
            {
                initMethod = startupListener.getClass().getDeclaredMethod("init");
                initMethod.setAccessible(true);
                initMethod.invoke(startupListener);
            }
            catch (Exception e)
            {
                logger.warn("a problem occurred during startup", e);
            }
        }
    }

    private void addComponentInitializers(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(ComponentInitializer.class.getName());

        if(result == null)
        {
            return;
        }

        Object componentInitializer;
        for (String componentInitializerName : result)
        {
            componentInitializer = ClassUtils.tryToInstantiateClassForName(componentInitializerName);

            if (componentInitializer != null && componentInitializer instanceof org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer)
            {
                ExtValContext.getContext().addComponentInitializer((org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer) componentInitializer);
            }
        }
    }

    private void addProcessedInformationRecorders(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(ProcessedInformationRecorder.class.getName());

        if(result == null)
        {
            return;
        }

        Object processedInformationRecorder;
        for (String processedInformationRecorderName : result)
        {
            processedInformationRecorder = ClassUtils.tryToInstantiateClassForName(processedInformationRecorderName);

            if (processedInformationRecorder != null && processedInformationRecorder instanceof org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder)
            {
                ExtValContext.getContext().addProcessedInformationRecorder((org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder) processedInformationRecorder);
            }
        }
    }

    private void addRendererInterceptors(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(RendererInterceptor.class.getName());

        if(result == null)
        {
            return;
        }

        Object rendererInterceptor;
        for (String rendererInterceptorName : result)
        {
            rendererInterceptor = ClassUtils.tryToInstantiateClassForName(rendererInterceptorName);

            if (rendererInterceptor != null && rendererInterceptor instanceof AbstractRendererInterceptor)
            {
                ExtValContext.getContext().registerRendererInterceptor((AbstractRendererInterceptor) rendererInterceptor);
            }
        }
    }

    private static URL findResourceBase(URL url, String baseResource)
    {
        String targetString = url.toString();
        targetString = targetString.substring(0, targetString.lastIndexOf(baseResource));

        try
        {
            return new URL(targetString);
        }
        catch (MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }
}
