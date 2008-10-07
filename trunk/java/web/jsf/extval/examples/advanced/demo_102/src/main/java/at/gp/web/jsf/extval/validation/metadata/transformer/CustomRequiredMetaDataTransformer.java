package at.gp.web.jsf.extval.validation.metadata.transformer;

import org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer;

import java.util.Map;
import java.util.HashMap;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */
public class CustomRequiredMetaDataTransformer implements MetaDataTransformer
{
    public Map<String, Object> extractMetaData(Annotation annotation)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("required", true);
        return result;
    }
}
