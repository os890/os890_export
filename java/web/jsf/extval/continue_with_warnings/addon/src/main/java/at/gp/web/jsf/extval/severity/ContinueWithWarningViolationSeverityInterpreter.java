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
package at.gp.web.jsf.extval.severity;

import org.apache.myfaces.extensions.validator.core.validation.parameter.ViolationSeverityInterpreter;
import org.apache.myfaces.extensions.validator.internal.Priority;
import org.apache.myfaces.extensions.validator.internal.ToDo;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

/**
 * @author Gerhard Petracek
 * @since x.x.3
 */
@ToDo(value = Priority.LOW, description = "possible additional feature - if warn mode is indicated by the" +
        "hidden field at the beginning and warn messages get ignored because there is no error at the 2nd try -" +
        "it's possible to detect this constellation and clear the extval faces-message-storage -" +
        "so there is no case where the ignored messages get displayed on the 2nd page..." +
        "(not implemented by default - because this is just one possible scenario")
public class ContinueWithWarningViolationSeverityInterpreter implements ViolationSeverityInterpreter
{
    public boolean severityBlocksNavigation(
            FacesContext facesContext, UIComponent uiComponent, FacesMessage.Severity severity)
    {
        tryToSwitchWarnMode(severity);
        return isStrongWarnMode(severity) ||
                FacesMessage.SEVERITY_ERROR.equals(severity) ||
                FacesMessage.SEVERITY_FATAL.equals(severity);
    }

    public boolean severityCausesValidatorException(
            FacesContext facesContext, UIComponent uiComponent, FacesMessage.Severity severity)
    {
        tryToSwitchWarnMode(severity);
        return FacesMessage.SEVERITY_ERROR.equals(severity) || FacesMessage.SEVERITY_FATAL.equals(severity);
    }

    public boolean severityCausesViolationMessage(
            FacesContext facesContext, UIComponent uiComponent, FacesMessage.Severity severity)
    {
        tryToSwitchWarnMode(severity);
        return true;
    }

    public boolean severityBlocksSubmit(
            FacesContext facesContext, UIComponent uiComponent, FacesMessage.Severity severity)
    {
        return FacesMessage.SEVERITY_ERROR.equals(severity) || FacesMessage.SEVERITY_FATAL.equals(severity);
    }

    public boolean severityShowsIndication(
            FacesContext facesContext, UIComponent uiComponent, FacesMessage.Severity severity)
    {
        return FacesMessage.SEVERITY_ERROR.equals(severity) || FacesMessage.SEVERITY_FATAL.equals(severity);
    }

    /*
     * private methods
     */
    private void tryToSwitchWarnMode(FacesMessage.Severity severity)
    {
        if (isStrongWarnMode(severity) && FacesMessage.SEVERITY_WARN.equals(severity))
        {
            switchToWeakWarnMode();
        }
        else if (FacesMessage.SEVERITY_ERROR.equals(severity) ||
                FacesMessage.SEVERITY_FATAL.equals(severity))
        {
            switchToStrongWarnMode();
        }
    }

    private void switchToStrongWarnMode()
    {
        WarnStateBean foundBean = WarnStateUtils.getOrCreateWarnStateBean();

        foundBean.setContinueWithWarnings(false);
        foundBean.lock();
    }

    private void switchToWeakWarnMode()
    {
        WarnStateUtils.getOrCreateWarnStateBean().setContinueWithWarnings(true);
    }

    private boolean isStrongWarnMode(FacesMessage.Severity severity)
    {
        return shouldBlockWarning(severity, getContinueWithWarningsFlag());
    }

    private boolean shouldBlockWarning(FacesMessage.Severity severity, boolean shouldContinueWithWarnings)
    {
        return (FacesMessage.SEVERITY_WARN.equals(severity) && !shouldContinueWithWarnings) ||
                FacesMessage.SEVERITY_ERROR.equals(severity) ||
                FacesMessage.SEVERITY_FATAL.equals(severity);
    }

    private boolean getContinueWithWarningsFlag()
    {
        Object foundBean = WarnStateUtils.tryToFindExistingWarnStateBean();

        if (!(foundBean instanceof WarnStateBean))
        {
            return false;
        }

        WarnStateBean warnStateBean = (WarnStateBean) foundBean;
        return warnStateBean.isContinueWithWarnings();
    }
}
