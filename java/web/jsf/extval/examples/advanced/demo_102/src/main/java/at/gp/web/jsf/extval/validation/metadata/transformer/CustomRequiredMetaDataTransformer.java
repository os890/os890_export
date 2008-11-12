package at.gp.web.jsf.extval.validation.metadata.transformer;

import org.apache.myfaces.extensions.validator.core.metadata.transformer.MetaDataTransformer;
import org.apache.myfaces.extensions.validator.core.metadata.MetaDataEntry;

import java.util.Map;
import java.util.HashMap;

/**
 * @author Gerhard Petracek
 */
public class CustomRequiredMetaDataTransformer implements MetaDataTransformer
{
    public Map<String, Object> convertMetaData(MetaDataEntry metaDataEntry)
    {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("required", true);
        return result;
    }
}
