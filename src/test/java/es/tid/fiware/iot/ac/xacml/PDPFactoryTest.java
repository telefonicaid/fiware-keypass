package es.tid.fiware.iot.ac.xacml;

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

import es.tid.fiware.iot.ac.util.Util;
import es.tid.fiware.iot.ac.util.Xml;
import org.testng.annotations.Test;
import org.wso2.balana.ParsingException;
import org.wso2.balana.Policy;

import static org.testng.AssertJUnit.assertEquals;

public class PDPFactoryTest {

    @Test
    public void testCreateValid() throws Exception {
        Policy p = new PDPFactory().create(Xml.toXml(
                Util.read(this.getClass(), "policy01.xml")));
        assertEquals("policy01", p.getId().toString());
    }

    @Test(expectedExceptions = ParsingException.class)
    void testCreateInvalid() throws Exception {
        Policy p = new PDPFactory().create(Xml.toXml(
                Util.read(this.getClass(), "policy04.xml")));
    }
}
