/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.ignite.peerclassloader;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.internal.util.typedef.G;
import org.apache.ignite.portable.testjar.GridPortableTestClass1;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

import javax.cache.processor.EntryProcessor;
import javax.cache.processor.MutableEntry;
import java.io.File;

/**
 * Tests for checking deserialization for classes loaded via PeerClassLoader.
 */
public class PeerClassLoaderTest extends GridCommonAbstractTest {
    /** Name of the classpath property */
    private static final String JAVA_CLASS_PATH = "java.class.path";

    /** {@inheritDoc} */
    @Override protected void afterTest() throws Exception {
        super.afterTest();

        G.stopAll(true);
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String igniteInstanceName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(igniteInstanceName);
        cfg.setPeerClassLoadingEnabled(true);

        return cfg;
    }

    /** {@inheritDoc} */
    @Override protected boolean isMultiJvm() {
        return true;
    }

    /**
     * @throws Exception if failed.
     */
    public void testFailsWhenClassAlreadyLoadedByPeerLoader() throws Exception {
        Ignite ignite = startGrid(0);

        String classPath = getClassPath();

        String changedClassPath = deleteJarFromClassPath(classPath,"test1-1.1.jar");

        setClassPath(changedClassPath);

        startGrid(1);

        IgniteCache<Object, Object> cache = ignite.getOrCreateCache("testCache");

        cache.invoke("key", new TestEntryProcessor());
    }

    /**
     * Get classpath from system property
     *
     * @return String current classpath
     */
    private String getClassPath() {
        return System.getProperty(JAVA_CLASS_PATH);
    }

    /**
     * Set classpath system property
     *
     * @param classPath String classpath to set
     */
    private void setClassPath(String classPath) {
        System.setProperty(JAVA_CLASS_PATH, classPath);
    }

    /**
     * Delete path of the jar from the classpath string
     *
     * @param classPath String classpath need to be changed
     * @param excludeJar String jar, which path should be deleted from the classpath
     * @return String changed classpath
     */
    private String deleteJarFromClassPath(String classPath, String excludeJar) {
        String[] paths = classPath.split(File.pathSeparator);

        StringBuilder sb = new StringBuilder();

        for(String path: paths) {
            if(path.contains(excludeJar))
                continue;

            sb.append(path).append(File.pathSeparator);
        }

        return sb.toString();
    }

    /**
     * EntryProcessor which will be invoked on all grids
     */
    private static class TestEntryProcessor implements EntryProcessor<Object, Object, Object> {
        /** {@inheritDoc} */
        @Override public Object process(MutableEntry<Object, Object> entry, Object... args) {
            Object val = new GridPortableTestClass1();

            entry.setValue(val);

            return val;
        }
    }
}