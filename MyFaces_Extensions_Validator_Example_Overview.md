## very IMPORTANT: Each example shows several features - for most requirements there are several possible solutions!!! The shown features aren't always the simplest approach. However, it's easier to use simple demo-cases to show a feature... Furthermore, not all features are covered by the examples. The MyFaces team is able to answer all questions. ##

## please use primarily the tagged versions. the trunk is always for the unreleased trunk version of myfaces extval! ##

# Basic examples #

  * **000**
    * Minimal Setup
    * JPA based validation
  * **001**
    * Convention for custom startup listener
    * Custom JPA validation error messages
    * Usage of cross-validation (for local properties) with customized validation error message
    * Convention for custom message bundle
  * **002**
    * The simplest possible case of a custom validation (without message resolver,...)
  * **003**
    * Custom validation without name convention (without message resolver,...)
    * Usage of AbstractValidationStrategy
    * Convention of static strategy mapping (via properties file)
  * **004**
    * Custom validation + usage of the message resolver mechanism
    * Shared message bundle via custom information provider bean (without convention)
    * Usage of custom information provider bean
    * No duplicated 'Validation' in the name of the validation strategy, if the annotation ends with 'Validation'
  * **005**
    * Custom validation + usage of the message resolver mechanism
    * Shared message bundle via custom **module** message resolver
  * **006**
    * Package convention to avoid having all artifacts in the same package
    * Custom message resolver per validation strategy
  * **007**
    * Convention for custom name mappers
    * Custom message resolver via custom name mapper

  * **008**
    * ExtVal validators as Managed-Bean
    * Reuse existing ExtVal message resolver via dependency injection

  * **009**
    * Bypass validation via annotations (ExtVal 1.x.2-SNAPSHOT+ required)
    * Bypass add-on available at: http://code.google.com/p/os890/source/browse/#svn/trunk/java/web/jsf/extval/bypass_validation

# Advanced examples #

  * **101**
    * Custom validation + usage of the message resolver-, meta-data transformer- and component initializer mechanism (simple cases)
    * Convention for custom factories

  * **102**
    * Custom validation + usage of the message resolver-, meta-data transformer- and component initializer mechanism
    * Convention for package structure
  * **103**
    * Use ExtVal for other purposes (secure required)
    * Startup listener via faces config
  * **104**
    * Alternative configuration approach
    * Replacing existing validation strategies
  * **105**
    * Custom JPA validation messages via message resolver
    * demo for highlighting validated fields without additional/special components
  * **106**
    * Dependency injection (e.g. via Spring) support
    * Label for annotation
  * **107**
    * Cross-component validation within complex components
    * Adding a custom validation strategy via ExtVal java api
    * Custom label support
    * Usage of @RequiredIf
  * **108**
    * Example for jsf comp client-side validation integration - it's just a demo how to do it - no full implementation
  * **109 - in progress**
    * Client-side validation based on annotations
  * **110 (requires ExtVal x.x.3-SNAPSHOT)**
    * Usage of the advanced meta-data add-on
  * **111 (requires ExtVal x.x.3-SNAPSHOT)**
    * Usage of model aware and class level validation add-on
  * **112 (requires ExtVal x.x.3-SNAPSHOT)**
    * Implementation of custom concepts (in this example: a simple impl. for partial validation)