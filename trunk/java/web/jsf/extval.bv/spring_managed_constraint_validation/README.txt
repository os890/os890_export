Requirements:
 - ExtVal x.x.3-SNAPSHOT+ and the BV-Integration module

Typesafe dependency injection support based on Spring 3+
Inject beans into constraint validators and/or use a different constrain validator implementation.

Step 1:
Activate it via:
    <context:component-scan base-package="at.gp.web.jsf.extval.beanval.spring"/>
in your Spring configuration.

Step 2:
Implement your custom constraint validator (extend the default implementations e.g. SizeValidatorForString and configure it as Spring bean (e.g. @Component)

e.g.:
@Component
public class CustomSizeValidatorForString extends SizeValidatorForString
{
    @Autowired
    private CustomValidationService validationService;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext)
    {
        return validationService.isValid(value);
    }
}
