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
package at.gp.web.jsf.extval.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.apache.myfaces.extensions.validator.baseval.annotation.Length;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Gerhard Petracek
 */
@Entity
public class Car implements Vehicle
{
    protected final Log logger = LogFactory.getLog(Car.class);

    @Id
    private Long id;

    @Length(minimum = 2)
    @Column(nullable = false, length = 20)
    private String manufacturer;

    @Length(minimum = 2)
    @Column(nullable = false, length = 20)
    private String modelIdentification;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getManufacturer()
    {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer)
    {
        //just to demonstrate the rollback
        this.logger.info("setManufacturer: " + manufacturer);

        this.manufacturer = manufacturer;
    }

    public String getModelIdentification()
    {
        return modelIdentification;
    }

    public void setModelIdentification(String modelIdentification)
    {
        //just to demonstrate the rollback
        this.logger.info("setModelIdentification: " + modelIdentification);

        this.modelIdentification = modelIdentification;
    }
}