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
package at.gp.web.jsf.extval.validation.metadata.provider;

import org.scannotation.AnnotationDB;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.core.ExtValContext;

import java.util.*;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;

import at.gp.web.jsf.extval.validation.metadata.provider.annotation.MetaDataProvider;

/**
 * @author Gerhard Petracek
 */
@ToDo(value = Priority.MEDIUM, description = "add functionality: scan specific package (global property)")
public class MetaDataProviderStorage
{
    public static final String KEY = MetaDataProviderStorage.class.getName() + ":KEY";

    protected final Log logger = LogFactory.getLog(getClass());

    //to allow the usage without scannotation
    private static Object annotationDB;
    private static MetaDataProviderStorage instance;
    private Map<Class, List<Class>> metaDataProviderStorage = new HashMap<Class, List<Class>>();
    private Map<Class, List<Class>> customMetaDataProviderStorage = new HashMap<Class, List<Class>>();

    private MetaDataProviderStorage()
    {
        Object customPackage = null;

        try
        {
            customPackage = ExtValContext.getContext().getGlobalProperty(KEY);

            if(customPackage instanceof String)
            {
                List<Class> result = processTarget((String)customPackage);

                MetaDataProvider metaDataProvider;
                for(Class currentClass : result)
                {
                    if(currentClass.isAnnotationPresent(MetaDataProvider.class))
                    {
                        metaDataProvider = (MetaDataProvider)currentClass.getAnnotation(MetaDataProvider.class);
                        addMetaDataProvider(metaDataProvider.value(), currentClass, this.customMetaDataProviderStorage);
                    }
                }
            }
        }
        catch (Throwable t)
        {
            if(this.logger.isWarnEnabled())
            {
                this.logger.warn("unable to setup annotation based metadata provider for " + customPackage, t);
            }
        }
    }

    public static MetaDataProviderStorage getInstance()
    {
        if(instance == null)
        {
            instance = new MetaDataProviderStorage();
        }
        return instance;
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

    public List<Class> getMetaDataProviderClassFor(Class sourceClass)
    {
        if(this.metaDataProviderStorage.containsKey(sourceClass))
        {
            return this.metaDataProviderStorage.get(sourceClass);
        }

        try
        {
            for(Class foundProvider : processTarget(sourceClass.getPackage().getName()))
            {
                addMetaDataProvider(sourceClass, foundProvider, this.metaDataProviderStorage);
            }

            List<Class> result = new ArrayList<Class>();
            if(this.customMetaDataProviderStorage.containsKey(sourceClass))
            {
                if(this.metaDataProviderStorage.containsKey(sourceClass))
                {
                    result = this.metaDataProviderStorage.get(sourceClass);
                }
                else
                {
                    this.metaDataProviderStorage.put(sourceClass, result);
                }

                result.addAll(this.customMetaDataProviderStorage.get(sourceClass));
            }
        }
        catch(Throwable t)
        {
            if(this.logger.isWarnEnabled())
            {
                this.logger.warn("unable to setup annotation based metadata provider for " + sourceClass.getName(), t);
            }

            this.metaDataProviderStorage.put(sourceClass, null);
        }

        return this.metaDataProviderStorage.get(sourceClass);
    }

    private List<Class> processTarget(String sourcePackage) throws Exception
    {
        synchronized (this)
        {
            List<String> scanResult = new ArrayList<String>();
            List<Class> result = new ArrayList<Class>();

            addBaseResource(sourcePackage.replace(".", "/"));
            scanResult.addAll(getAnnotationDB().getAnnotationIndex().get(MetaDataProvider.class.getName()));

            for(String className : scanResult)
            {
                result.add(ClassUtils.tryToLoadClassForName(className));
            }

            return result;
        }
    }

    private AnnotationDB getAnnotationDB()
    {
        if(annotationDB == null)
        {
            annotationDB = getAnnotationScanner();
        }

        return (AnnotationDB)annotationDB;
    }


    private void addMetaDataProvider(Class targetClass, Class metaDataProviderClass, Map<Class, List<Class>> storage)
    {
        if(!((MetaDataProvider)metaDataProviderClass.getAnnotation(MetaDataProvider.class)).value().equals(MetaDataProvider.class))
        {
            List<Class> providerList = new ArrayList<Class>();
            if(storage.containsKey(targetClass))
            {
                providerList = storage.get(targetClass);
            }
            else
            {
                storage.put(targetClass, providerList);
            }

            providerList.add(metaDataProviderClass);
        }
    }

    private void addBaseResource(String baseResource) throws IOException
    {
        ClassLoader result = getClass().getClassLoader();
        Enumeration<URL> urls = result.getResources(baseResource);

        AnnotationDB annotationDB = getAnnotationDB();
        while (urls.hasMoreElements())
        {
            annotationDB.scanArchives(findResourceBase(urls.nextElement(), baseResource));
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
