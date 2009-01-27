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

import at.gp.web.jsf.extval.config.annotation.*;
import at.gp.web.jsf.extval.config.annotation.RendererInterceptor;
import at.gp.web.jsf.extval.config.annotation.ValidationExceptionInterceptor;
import org.apache.myfaces.extensions.validator.core.ExtValContext;
import org.apache.myfaces.extensions.validator.core.CustomInformation;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticInMemoryConfiguration;
import org.apache.myfaces.extensions.validator.core.initializer.configuration.StaticConfigurationNames;
import org.apache.myfaces.extensions.validator.core.interceptor.*;
import org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.scannotation.AnnotationDB;
import org.scannotation.WarUrlFinder;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.List;

/**
 * @author Gerhard Petracek
 */
public class AnnotationBasedConfigStartupListener extends AbstractStartupListener
{
    private static final long serialVersionUID = 395947458264828231L;

    protected void init()
    {
        AnnotationDB annotationDB = getAnnotationScanner();

        try
        {
            String baseResource = getBasePackage();
            List<String> baseResources = getBasePackages();

            if(baseResource != null)
            {
                addBaseResource(baseResource, annotationDB);
            }
            else if (baseResources != null)
            {
                for(String currentBaseResource : baseResources)
                {
                    addBaseResource(currentBaseResource, annotationDB);
                }
            }
            else
            {
                annotationDB.scanArchives(WarUrlFinder.findWebInfLibClasspaths((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()));
                annotationDB.scanArchives(WarUrlFinder.findWebInfClassesPath((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()));
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
        addValidationExceptionInterceptors(annotationDB);
        addProcessedInformationRecorders(annotationDB);
        addRendererInterceptors(annotationDB);
        addInformationProviderBean(annotationDB);
    }

    private void addBaseResource(String baseResource, AnnotationDB annotationDB) throws IOException
    {
        ClassLoader result = getClass().getClassLoader();
        Enumeration<URL> urls = result.getResources(baseResource);

        while (urls.hasMoreElements())
        {
            annotationDB.scanArchives(findResourceBase(urls.nextElement(), baseResource));
        }
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
        return ExtValContext.getContext().getInformationProviderBean().get(CustomInformation.BASE_PACKAGE).replace(".", "/");
    }

    protected List<String> getBasePackages()
    {
        return null;
    }

    private void addAdvancedValidationStrategies(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(AdvancedValidationStrategy.class.getName());

        if(result == null)
        {
            return;
        }

        AdvancedValidationStrategy currentAnnotation;
        StaticInMemoryConfiguration config;
        for (String validationStrategyName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(validationStrategyName).getDeclaredAnnotations())
            {
                if (annotation instanceof AdvancedValidationStrategy)
                {
                    currentAnnotation = (AdvancedValidationStrategy) annotation;

                    for (Class targetAnnotationClass : currentAnnotation.annotationClass())
                    {
                        config = new StaticInMemoryConfiguration();
                        config.addMapping(targetAnnotationClass.getName(), validationStrategyName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG, config);
                    }

                    config = new StaticInMemoryConfiguration();
                    config.addMapping(validationStrategyName, currentAnnotation.messageResolverClass().getName());
                    ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_CONFIG, config);

                    config = new StaticInMemoryConfiguration();
                    config.addMapping(validationStrategyName, currentAnnotation.metaDataTransformerClass().getName());
                    ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_META_DATA_TRANSFORMER_CONFIG, config);
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
        StaticInMemoryConfiguration config;
        for (String validationStrategyName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(validationStrategyName).getDeclaredAnnotations())
            {
                if (annotation instanceof ValidationStrategy)
                {
                    currentAnnotation = (ValidationStrategy) annotation;

                    for (Class targetAnnotation : currentAnnotation.value())
                    {
                        config = new StaticInMemoryConfiguration();
                        config.addMapping(targetAnnotation.getName(), validationStrategyName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG, config);
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
        StaticInMemoryConfiguration config;
        for (String validationStrategyName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(validationStrategyName).getDeclaredAnnotations())
            {
                if (annotation instanceof MetaDataValidationStrategy)
                {
                    currentAnnotation = (MetaDataValidationStrategy) annotation;

                    for (String metaDataKey : currentAnnotation.value())
                    {
                        config = new StaticInMemoryConfiguration();
                        config.addMapping(metaDataKey, validationStrategyName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.META_DATA_TO_VALIDATION_STRATEGY_CONFIG, config);
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
        StaticInMemoryConfiguration config;
        for (String messageResolverName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(messageResolverName).getDeclaredAnnotations())
            {
                if (annotation instanceof MessageResolver)
                {
                    currentAnnotation = (MessageResolver) annotation;

                    for (Class validationStrategy : currentAnnotation.validationStrategyClasses())
                    {
                        config = new StaticInMemoryConfiguration();
                        config.addMapping(validationStrategy.getName(), messageResolverName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_MESSAGE_RESOLVER_CONFIG, config);
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
        StaticInMemoryConfiguration config;
        for (String metaDataTransformerName : result)
        {
            for (Annotation annotation : ClassUtils.tryToLoadClassForName(metaDataTransformerName).getDeclaredAnnotations())
            {
                if (annotation instanceof MetaDataTransformer)
                {
                    currentAnnotation = (MetaDataTransformer) annotation;

                    for (Class validationStrategy : currentAnnotation.validationStrategyClasses())
                    {
                        config = new StaticInMemoryConfiguration();
                        config.addMapping(validationStrategy.getName(), metaDataTransformerName);
                        ExtValContext.getContext().addStaticConfiguration(StaticConfigurationNames.VALIDATION_STRATEGY_TO_META_DATA_TRANSFORMER_CONFIG, config);
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

    private void addValidationExceptionInterceptors(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(ValidationExceptionInterceptor.class.getName());

        if(result == null)
        {
            return;
        }

        Object validationExceptionInterceptor;
        for (String validationExceptionInterceptorName : result)
        {
            validationExceptionInterceptor = ClassUtils.tryToInstantiateClassForName(validationExceptionInterceptorName);

            if (validationExceptionInterceptor != null && validationExceptionInterceptor instanceof org.apache.myfaces.extensions.validator.core.interceptor.ValidationExceptionInterceptor)
            {
                ExtValContext.getContext().addValidationExceptionInterceptor((org.apache.myfaces.extensions.validator.core.interceptor.ValidationExceptionInterceptor) validationExceptionInterceptor);
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

    private void addInformationProviderBean(AnnotationDB annotationDB)
    {
        Set<String> result = annotationDB.getAnnotationIndex().get(InformationProviderBean.class.getName());

        if(result == null)
        {
            return;
        }

        if(result.size() > 1 && logger.isWarnEnabled())
        {
            logger.warn("multiple information provider beans found. the nature of this artifact just allows one provider");
        }

        Object informationProviderBean;
        for (String informationProviderBeanName : result)
        {
            informationProviderBean = ClassUtils.tryToInstantiateClassForName(informationProviderBeanName);

            if (informationProviderBean != null && informationProviderBean instanceof org.apache.myfaces.extensions.validator.core.InformationProviderBean)
            {
                FacesContext.getCurrentInstance().getExternalContext().getApplicationMap()
                    .put(org.apache.myfaces.extensions.validator.core.InformationProviderBean.BEAN_NAME, informationProviderBean);
                break;
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
