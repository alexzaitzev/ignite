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
package org.apache.ignite.testsuites;

import org.apache.ignite.IgniteSystemProperties;
import org.apache.ignite.internal.processors.cache.persistence.db.file.IgnitePdsPageReplacementTest;

/**
 * Page replacement light variant of test for native direct IO (wastes real IOPs on agents)
 */
public class IgnitePdsReplacementNativeIoTest extends IgnitePdsPageReplacementTest {

    /** {@inheritDoc} */
    @Override protected long getTestTimeout() {
        return 15 * 60 * 1000;
    }

    /** {@inheritDoc} */
    @Override protected int getPagesNum() {
        // 1k - passed, 20k - passed, 64k - failed
        return 20 * 1024;
    }

    /** {@inheritDoc} */
    @Override public void testPageReplacement() throws Exception {
        System.setProperty(IgniteSystemProperties.IGNITE_USE_ASYNC_FILE_IO_FACTORY, "false");

        super.testPageReplacement();
    }
}
