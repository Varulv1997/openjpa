/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package org.apache.openjpa.persistence.inheritance.jointable.onetomany;


import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.openjpa.jdbc.conf.JDBCConfiguration;
import org.apache.openjpa.jdbc.sql.DB2Dictionary;
import org.apache.openjpa.jdbc.sql.DBDictionary;
import org.apache.openjpa.jdbc.sql.DerbyDictionary;
import org.apache.openjpa.persistence.common.apps.Part;
import org.apache.openjpa.persistence.common.apps.PartPK;
import org.apache.openjpa.persistence.common.apps.Shirt;
import org.apache.openjpa.persistence.common.apps.Textile;
import org.apache.openjpa.persistence.common.apps.TextilePK;
import org.apache.openjpa.persistence.common.apps.Trousers;
import org.apache.openjpa.persistence.test.SingleEMFTestCase;

/**
 * Tests persisting a domain model where {@code MapsId} is used for a
 * entity that uses auto-generated identity.
 * <br>
 * The test is created with a reported error with following domain model:
 * <ol>
 * <LI> The domain model used a Joined Inheritance of Textile->(Shirt, Trousers)
 * <LI> Textile used auto-assigned primary key
 * <LI> A Shirt has Parts. 
 * <LI> Part used @Maps id annotation to refer the Shirt it belongs to.
 * </ol>   
 * and following configuration
 * <ol>
 * <li> the schema was defined with SQL DDL script and included foreign
 * key constraints.
 * <li> {@code openjpa.jdbc.MappingDefaults} was not configured
 * </ol>
 * <p>
 * Under the above conditions, the {@code INSERT} SQL for Shirt was
 * generated twice during flush: once to obtain the primary key from
 * the database and (erroneously) second time while flushing a Part
 * via its @MapsId relation.
 * 
 * @see Shirt
 * @see TextTile
 * @see and other classes of the domain model 
 * 
 * @author Pinaki Poddar
 *
 */
public class TestMapsIdWithAutoGeneratedKey extends SingleEMFTestCase {
    boolean disabled = true;
    
    public void setUp() {
        super.setUp(DROP_TABLES, 
        	Textile.class, TextilePK.class,
        	Shirt.class, Trousers.class,
        	Part.class, PartPK.class);
        
        DBDictionary dic = ((JDBCConfiguration)emf.getConfiguration()).getDBDictionaryInstance();
        if (dic.supportsAutoAssign && (dic instanceof DB2Dictionary || dic instanceof DerbyDictionary)) {
            disabled = false;
        }
    }


    public void testPersistShirtWithPart() {
        if (disabled) {
            return;
        }
        
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        int nPart = 3;
        tx.begin();

        Shirt shirt = new Shirt();
        String name = "Shirt: " + System.currentTimeMillis();
        shirt.setTxeName(name);
        String size = "L";
        shirt.setSzeId(size);
        
        int pid = (int) System.currentTimeMillis();
        for (int i = 0; i < nPart; i++) {
	        Part part = new Part();
	        part.setPartName("Part");
	        part.getId().setPartNumber(pid++);
	        part.setShirt(shirt);
	        shirt.getParts().add(part);
        }
        em.persist(shirt);
        tx.commit();
        em.close();
        
        int sid = shirt.getTextileId();
        em = emf.createEntityManager();
        shirt = em.find(Shirt.class, sid);
        assertNotNull(shirt);
        assertNotNull(shirt.getParts());
        assertEquals(nPart, shirt.getParts().size());
        
    }
}
