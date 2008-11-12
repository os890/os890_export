package at.gp.web.jsf.extval.validation;

import org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;
import org.apache.myfaces.extensions.validator.core.metadata.CommonMetaDataKeys;

import java.util.Map;
import java.util.HashMap;
import java.lang.annotation.Annotation;

/**
 * @author Gerhard Petracek
 */
public class CustomRequiredValidatorMetaDataTransformer implements MetaDataTransformer
{
    public Map<String, Object> convertMetaData(MetaDataEntry metaDataEntry)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("required", true);
        return result;
    }
}
