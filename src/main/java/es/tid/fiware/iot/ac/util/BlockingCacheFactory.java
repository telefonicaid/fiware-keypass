package es.tid.fiware.iot.ac.util;

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

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;

/**
 * Creates caches with blocking decoration.
 */
public class BlockingCacheFactory implements CacheFactory {

    private final CacheManager manager;

    public BlockingCacheFactory(int timeToLiveSeconds, int maxEntriesLocalHeap) {
        Configuration cfg = new Configuration();
        CacheConfiguration.CacheDecoratorFactoryConfiguration cdfc =
                new CacheConfiguration.CacheDecoratorFactoryConfiguration();
        cdfc.setClass(BlockingCacheDecoratorFactory.class.getName());
        CacheConfiguration defaultCacheCfg = new CacheConfiguration();
        defaultCacheCfg.addCacheDecoratorFactory(cdfc);
        defaultCacheCfg.setTimeToLiveSeconds(timeToLiveSeconds);
        defaultCacheCfg.setMaxEntriesLocalHeap(maxEntriesLocalHeap);
        cfg.addDefaultCache(defaultCacheCfg);

        manager = CacheManager.create(cfg);

    }

    @Override
    public Ehcache get(String name) {
        return manager.addCacheIfAbsent(name);
    }

}
