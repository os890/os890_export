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
package at.gp.web.jsf.extval.beanval.form;

import at.gp.web.jsf.extval.beanval.form.storage.FormBeanClassStorage;
import at.gp.web.jsf.extval.beanval.form.storage.FormBeanDescriptor;
import at.gp.web.jsf.extval.beanval.form.storage.ProcessedInformationStorageEntry;
import static at.gp.web.jsf.extval.beanval.form.util.FormValidationUtils.getOrInitFormBeanClassStorage;
import static at.gp.web.jsf.extval.beanval.form.util.FormValidationUtils.getOrInitProcessedInformationStorage;
import at.gp.web.jsf.extval.beanval.form.validation.FormBean;
import org.apache.myfaces.extensions.validator.beanval.ExtValBeanValidationContext;
import org.apache.myfaces.extensions.validator.core.storage.FacesMessageStorage;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;
import org.apache.myfaces.extensions.validator.util.ClassUtils;
import org.apache.myfaces.extensions.validator.util.ExtValUtils;
import org.apache.myfaces.extensions.validator.util.JsfUtils;

import javax.faces.application.FacesMessage;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.validation.ConstraintViolation;
import javax.validation.ValidatorFactory;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gerhard Petracek
 */
public class FormValidationPhaseListener implements PhaseListener
{
    private static final long serialVersionUID = 5114256514092459482L;

    private boolean isInitialized = false;

    @ToDo(value = Priority.LOW, description = "if there is an exception, add a faces-message with the exception and call fc.renderResponse()")
    public void afterPhase(PhaseEvent phaseEvent)
    {
        @Deprecated
        boolean validationExecuted = false;

        for (String formId : getOrInitProcessedInformationStorage().getFormIds())
        {
            validationExecuted = validateForm(formId);
        }

        if (validationExecuted)
        {
            addFacesMessages();
            resetStorages();
        }
    }

    private boolean validateForm(String formId)
    {
        Set<FormBeanDescriptor> formBeanDescriptors = findClassesOfFormBeans(formId);

        List<FormBeanHolder> formBeans = addObjectsToValidate(formId, formBeanDescriptors);

        ValidatorFactory validatorFactory = ExtValBeanValidationContext.getCurrentInstance().getValidatorFactory();

        Set<ConstraintViolation<FormBean>> violations;

        boolean validationExecuted = false;

        for (FormBeanHolder formBeanHolder : formBeans)
        {
            violations = validatorFactory.usingContext()
                    .messageInterpolator(ExtValBeanValidationContext.getCurrentInstance().getMessageInterpolator())
                    .constraintValidatorFactory(validatorFactory.getConstraintValidatorFactory())
                    .traversableResolver(validatorFactory.getTraversableResolver())
                    .getValidator()
                    .validate(formBeanHolder.getFormBean(), formBeanHolder.getGroups());

            processViolations(violations);

            validationExecuted = true;
        }

        return validationExecuted;
    }

    private Set<FormBeanDescriptor> findClassesOfFormBeans(String formId)
    {
        Set<FormBeanDescriptor> result = new HashSet<FormBeanDescriptor>();

        for (ProcessedInformationStorageEntry processedInformationStorageEntry : getOrInitProcessedInformationStorage().getEntries(formId))
        {
            result.addAll(getFormBeanClassesForBaseBean(processedInformationStorageEntry));
        }

        return result;
    }

    private List<FormBeanDescriptor> getFormBeanClassesForBaseBean(ProcessedInformationStorageEntry processedInformationStorageEntry)
    {
        List<FormBeanDescriptor> result = new ArrayList<FormBeanDescriptor>();

        result.addAll(getOrInitFormBeanClassStorage().getFormBeanClasses(processedInformationStorageEntry.getRootBean().getClass()));

        if (!processedInformationStorageEntry.getRootBean().equals(processedInformationStorageEntry.getLeafBean()))
        {
            result.addAll(getOrInitFormBeanClassStorage().getFormBeanClasses(processedInformationStorageEntry.getLeafBean().getClass()));
        }

        return result;
    }

    private List<FormBeanHolder> addObjectsToValidate(String formId, Set<FormBeanDescriptor> formBeanDescriptors)
    {
        List<FormBeanHolder> result = new ArrayList<FormBeanHolder>(formBeanDescriptors.size());

        for (FormBeanDescriptor formBeanDescriptor : formBeanDescriptors)
        {
            result.add(transferValuesToFormBean(formId, formBeanDescriptor));
        }
        return result;
    }

    private void processViolations(Set<ConstraintViolation<FormBean>> violations)
    {
        for (ConstraintViolation<FormBean> violation : violations)
        {
            FacesMessage facesMessage = ExtValUtils.createFacesMessage(violation.getMessage(), violation.getMessage());

            if (facesMessage != null && facesMessage.getSummary() != null && facesMessage.getDetail() != null)
            {
                ExtValUtils.tryToAddViolationMessageForComponentId(null, facesMessage);
            }

            ExtValUtils.tryToBlocksNavigationForComponentId(null, facesMessage);
        }
    }

    private FormBeanHolder transferValuesToFormBean(String formId, FormBeanDescriptor formBeanDescriptor)
    {
        FormBean formBean = ClassUtils.tryToInstantiateClass(formBeanDescriptor.getFormBeanClass());

        if (formBean == null)
        {
            throw new IllegalStateException("can't create instance of " + formBeanDescriptor.getFormBeanClass().getName());
        }

        String propertyName;
        boolean isInComplexComponentValidation;
        for (ProcessedInformationStorageEntry entry : getOrInitProcessedInformationStorage().getEntries(formId))
        {
            if (entry.getClientId().contains(":"))
            {
                propertyName = entry.getClientId().substring(entry.getClientId().lastIndexOf(":") + 1);
            }
            else
            {
                propertyName = entry.getClientId();
            }

            isInComplexComponentValidation = entry.getFurtherEntries() != null;

            transferValue(formBean, formBeanDescriptor, propertyName, entry.getConvertedValue(), isInComplexComponentValidation);

            if (entry.getFurtherEntries() != null)
            {

                for (ProcessedInformationStorageEntry nextEntry : entry.getFurtherEntries())
                {
                    transferValue(formBean, formBeanDescriptor, propertyName, nextEntry.getConvertedValue(), isInComplexComponentValidation);
                }
            }
        }

        return new FormBeanHolder(formBean, formBeanDescriptor.getGroups());
    }

    @ToDo(value = Priority.BLOCKING, description = "try to use setter-method")
    private void transferValue(Object formBackingBean, FormBeanDescriptor formBeanDescriptor, String propertyName, Object value, boolean inComplexComponentValidation)
    {
        String prefix = "set";
        if (inComplexComponentValidation)
        {
            prefix = "add";
        }

        Method method = formBeanDescriptor.getMethod(prefix + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));

        try
        {
            if (method != null)
            {
                method.setAccessible(true);
                method.invoke(formBackingBean, value);
            }
        }
        catch (IllegalAccessException e)
        {
            throw new IllegalStateException(e);
        }
        catch (InvocationTargetException e)
        {
            throw new IllegalStateException(e);
        }
        finally
        {
            if (method == null)
            {
                Field field = formBeanDescriptor.getField(propertyName);

                //if the field is null: there are multiple form-beans for one form (for more specialized validation)
                if(field != null)
                {
                    field.setAccessible(true);
                    try
                    {
                        field.set(formBackingBean, value);
                    }
                    catch (IllegalAccessException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void beforePhase(PhaseEvent phaseEvent)
    {
        if (!isInitialized)
        {
            if (WebXmlParameter.DEACTIVATE_FORMVALIDATION != null
                    && WebXmlParameter.DEACTIVATE_FORMVALIDATION.equalsIgnoreCase("true"))
            {
                JsfUtils.deregisterPhaseListener(this);
            }
            else
            {
                isInitialized = true;
            }
        }
    }

    public PhaseId getPhaseId()
    {
        return PhaseId.PROCESS_VALIDATIONS;
    }

    private void addFacesMessages()
    {
        FacesMessageStorage facesMessageStorage = ExtValUtils.getStorage(
                FacesMessageStorage.class, FacesMessageStorage.class.getName());

        if (facesMessageStorage != null)
        {
            facesMessageStorage.addAll();
            ExtValUtils.resetStorage(FacesMessageStorage.class, FacesMessageStorage.class.getName());
        }
    }

    private void resetStorages()
    {
        resetProcessedInformationStorage();
        resetFormBeanClassStorage();
    }

    private void resetProcessedInformationStorage()
    {
        getOrInitProcessedInformationStorage().clear();
    }

    private void resetFormBeanClassStorage()
    {
        ExtValUtils.resetStorage(FormBeanClassStorage.class, FormBeanClassStorage.class.getName());
    }

    /*
     * generated
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        //noinspection RedundantIfStatement
        if (!(o instanceof FormValidationPhaseListener))
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
