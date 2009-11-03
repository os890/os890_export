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
package at.gp.web.jsf.extval.validation.metadata.priority;

import org.apache.myfaces.extensions.validator.core.storage.DefaultFacesMessageStorage;
import org.apache.myfaces.extensions.validator.core.validation.message.FacesMessageHolder;

import java.util.Comparator;

/**
 * in order to avoid sorting according to the message text (to honor validation priority)
 *
 * @author Gerhard Petracek
 */
public class PriorityAwareFacesMessageStorage extends DefaultFacesMessageStorage
{
    @Override
    protected Comparator<FacesMessageHolder> getFacesMessageComparator()
    {
        return new Comparator<FacesMessageHolder>() {
            public int compare(FacesMessageHolder holder1, FacesMessageHolder holder2)
            {
                if(holder1.getFacesMessage().getSeverity() == null)
                {
                    return 1;
                }
                if(holder1.getFacesMessage().getSeverity().equals(holder2.getFacesMessage().getSeverity()))
                {
                    return 0;
                }

                if(holder1.getFacesMessage().getSeverity().getOrdinal() >
                        holder2.getFacesMessage().getSeverity().getOrdinal())
                {
                    return -1;
                }
                else
                {
                    return 1;
                }
            }
        };
    }
}
