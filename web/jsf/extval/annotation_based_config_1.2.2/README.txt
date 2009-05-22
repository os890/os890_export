Dependencies:
 - scannotation https://scannotation.svn.sourceforge.net/svnroot/
   \_ javassist http://www.jboss.org/javassist/downloads/


The default package which is scanned is the custom package of ExtVal (org.apache.myfaces.extensions.validator.custom).
It's possible to customize it via:
 - web.xml context-param
 - custom information provider bean
 - override getBasePackage of AnnotationBasedConfigStartupListener

Usage instructions:
Annotate your classes and place them within (a sub-package of) the base package.

Available annotations:
 - AdvancedValidationStrategy
 - ComponentInitializer
 - InformationProviderBean
 - MessageResolver
 - MetaDataTransformer
 - MetaDataValidationStrategy
 - ProcessedInformationRecorder
 - RendererInterceptor
 - StartupListener
 - ValidationExceptionInterceptor
 - ValidationStrategy

since extval 1.x.2:
 - MetaDataExtractionInterceptor
 - NameMapper
 - FacesMessageFactory

StartupListener:
Annotation for sub-classes of
org.apache.myfaces.extensions.validator.core.startup.AbstractStartupListener

ValidationStrategy or MetaDataValidationStrategy:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.validation.strategy.ValidationStrategy

MessageResolver:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.validation.message.resolver.MessageResolver

ComponentInitializer:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.initializer.component.ComponentInitializer

MetaDataTransformer:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer

ProcessedInformationRecorder:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.recorder.ProcessedInformationRecorder

RendererInterceptor:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.interceptor.RendererInterceptor

AdvancedValidationStrategy:
It's like the ValidationStrategy annotation.
The difference: It bundles information so you don't need the single MessageResolver and MetaDataTransformer annotations.

InformationProviderBean:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.InformationProviderBean

ValidationExceptionInterceptor:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.interceptor.ValidationExceptionInterceptor

MetaDataExtractionInterceptor
:

Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.interceptor.MetaDataExtractionInterceptor

NameMapper:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.mapper.NameMapper

 examples:
    @NameMapper(from = Annotation.class, to = ValidationStrategy.class)
    public class MyMetaDataToValidationStrategyNameMapper extends AbstractMetaDataToValidationStrategyNameMapper {...}

    @NameMapper(from = ValidationStrategy.class, to = MessageResolver.class)
    public class MyValidationStrategyToMessageResolverNameMapper extends AbstractValidationStrategyToMsgResolverNameMapper {...}

    @NameMapper(from = ValidationStrategy.class, to = MetaDataTransformer.class)
    public class MyValidationStrategyToMetaDataTransformerNameMapper extends AbstractValidationStrategyToMetaDataTransformerNameMapper {...}

FacesMessageFactory:
Annotation for implementations or sub-classes of implementations of
org.apache.myfaces.extensions.validator.core.factory.FacesMessageFactory
