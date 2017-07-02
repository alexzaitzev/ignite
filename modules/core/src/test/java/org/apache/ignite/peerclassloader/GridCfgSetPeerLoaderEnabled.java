package org.apache.ignite.peerclassloader;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;

/**
 * Tests for checking deserialization for classes loaded via PeerClassLoader
 */
public class GridCfgSetPeerLoaderEnabled extends GridCommonAbstractTest {

    /** */
    private static Ignite nodeWithPeerLoader;

    /** */
    private static Ignite nodeWithoutPeerLoader;

    /** {@inheritDoc} */
    @Override
    protected void beforeTestsStarted() throws Exception {
        super.beforeTestsStarted();

        IgniteConfiguration cfg = CreateIgniteConfiguration(true);
        nodeWithPeerLoader = startGrid("nodeWithPeerLoader", cfg);

        cfg = CreateIgniteConfiguration(false);
        nodeWithoutPeerLoader = startGrid("nodeWithoutPeerLoader", cfg);
    }

    /**
     * @throws Exception if failed.
     * */
    public void testFailsWhenClassAlreadyLoadedByPeerLoader() throws Exception {

        Boo data = new Boo(1);
        IgniteCache<Integer, Boo> cache  = nodeWithPeerLoader.createCache("testCache");
        cache.put(data.GetId(), data);

        Foo obj = new Foo(2, data);
        IgniteCache<Integer, Foo> cache1  = nodeWithoutPeerLoader.createCache("testCache1");
        cache1.put(obj.GetId(), obj);

        Foo result = cache1.get(obj.GetId());

        assertEquals(obj.GetData().GetId(), result.GetData().GetId());
    }

    private IgniteConfiguration CreateIgniteConfiguration(boolean enablePeerLoader) {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setPeerClassLoadingEnabled(enablePeerLoader);
        cfg.setPeerClassLoadingLocalClassPathExclude(Boo.class.getName());

        return cfg;
    }
}

class Boo
{
    private Integer id;

    public Boo(Integer id) {
        this.id = id;
    }

    public Integer GetId()
    {
        return this.id;
    }
}


class Foo
{
    private Integer id;

    private Boo data;

    public Foo(Integer id, Boo data) {
        this.id = id;
        this.data = data;
    }

    public Integer GetId()
    {
        return this.id;
    }

    public Boo GetData()
    {
        return this.data;
    }
}
