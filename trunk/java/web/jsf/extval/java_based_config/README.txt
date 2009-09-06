module based configuration layer for easy java based configuration.
it's an alternative to the annotation based config add-on

the configuration approach is inspired by google guice

allows to configure:
 - ValidationStrategy
 - MessageResolver
 - MetaDataTransformer
 - ValidationExceptionInterceptor
 - ComponentInitializer
 - RendererInterceptor
 - MetaDataExtractionInterceptor
 - ProcessedInformationRecorder

use e.g. a custom startup listener to register your modules

example:

ExtValModuleRegistry.startConfig()
  .modules(new RequiredValidationModul())
  .modules(new DynamicValidationModul()).endConfig();

or:
ExtValModuleRegistry.startConfig()
  .modules(new RequiredValidationModul(), new DynamicValidationModul()).endConfig();

example for a config module:

public class RequiredValidationModul extends AbstractExtValModule
{
    /*
     * you don't have to think about the order
     * just the constellation has to make sense
     * that means you cannot bind a global artifact like a component initializer to a validation strategy
     * you can bind together a validation strategy with:
     *  - constraint
     *  - message resolver
     *  - meta data transformer
     *  since you always need the validation strategy - it makes sense to start with the validation strategy
     */
    protected void configure()
    {
        bind(RequiredValidator.class).to(Required.class);
        //or
        //bind(Required.class).to(RequiredValidator.class);
        //or
        //bind(RequiredValidator.class)
                .to(Required.class, RequiredMsgResolver.class, RequiredMetaDataTransformer.class);

        add(RequiredValidationExceptionInterceptor.class).add(RequiredComponentInitializer.class);
        //or as instance:
        instance(new RequiredValidationExceptionInterceptor()).instance(new RequiredComponentInitializer());
    }
}
