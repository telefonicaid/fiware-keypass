package es.tid.fiware.iot.ac;

/*
 * Copyright 2014 Telefonica Investigación y Desarrollo, S.A.U
 *
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

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AcConfig extends Configuration {

    @NotEmpty
    @JsonProperty("tenantHeader")
    private String tenantHeader;

    @NotEmpty
    @JsonProperty("correlatorHeader")
    private String correlatorHeader;

    @JsonProperty("steelSkinPepMode")
    private boolean steelSkinPepMode;

    @Valid
    @NotNull
    @JsonProperty("database")
    private DataSourceFactory database = new DataSourceFactory();

    @Valid
    @JsonProperty("pdpCache")
    private CacheConfig pdpCacheConfig = new CacheConfig();

    public String getTenantHeader() {
        return tenantHeader;
    }

    public String getCorrelatorHeader() {
        return correlatorHeader;
    }

    public boolean getSteelSkinPepMode() {
        return steelSkinPepMode;
    }

    public DataSourceFactory getDataSourceFactory() {
        return database;
    }

    public CacheConfig getPdpCacheConfig() {
        return pdpCacheConfig;
    }

    public void setTenantHeader(String tenantHeader) {
        this.tenantHeader = tenantHeader;
    }

    public void setCorrelatorHeader(String correlatorHeader) {
        this.correlatorHeader = correlatorHeader;
    }

    public void setSteelSkinPepMode(boolean steelSkinPepMode) {
        this.steelSkinPepMode = steelSkinPepMode;
    }

    public void setDatabase(DataSourceFactory database) {
        this.database = database;
    }

    public void setPdpCacheConfig(CacheConfig pdpCacheConfig) {
        this.pdpCacheConfig = pdpCacheConfig;
    }
}
