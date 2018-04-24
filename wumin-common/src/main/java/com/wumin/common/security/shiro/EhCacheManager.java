package com.wumin.common.security.shiro;

import java.io.ByteArrayInputStream;  
import java.io.IOException;  
import java.io.InputStream;  
  
import org.apache.commons.io.IOUtils;  
import org.apache.shiro.cache.Cache;  
import org.apache.shiro.cache.CacheException;  
import org.apache.shiro.cache.CacheManager;  
import org.apache.shiro.cache.ehcache.EhCache;  
import org.apache.shiro.config.ConfigurationException;  
import org.apache.shiro.io.ResourceUtils;  
import org.apache.shiro.util.Destroyable;  
import org.apache.shiro.util.Initializable;  
import org.slf4j.Logger;  
import org.slf4j.LoggerFactory;  

/**
 * shiro 1.3.2  getCacheManagerConfigFileInputStream获取配置文件的方式导致热部署时无法移除
 * @author walker
 *
 */
public class EhCacheManager implements CacheManager, Initializable, Destroyable {  
    
    private static final Logger LOGGER = LoggerFactory.getLogger(EhCacheManager.class);

    protected net.sf.ehcache.CacheManager manager;

    private boolean cacheManagerImplicitlyCreated = false;

    private String cacheManagerConfigFile = "classpath:org/apache/shiro/cache/ehcache/ehcache.xml";

    public EhCacheManager() {
    }

    public net.sf.ehcache.CacheManager getCacheManager() {
        return manager;
    }

    public void setCacheManager(net.sf.ehcache.CacheManager manager) {
        this.manager = manager;
    }

    public String getCacheManagerConfigFile() {
        return this.cacheManagerConfigFile;
    }

    public void setCacheManagerConfigFile(String classpathLocation) {
        this.cacheManagerConfigFile = classpathLocation;
    }

    protected InputStream getCacheManagerConfigFileInputStream() {
        String configFile = getCacheManagerConfigFile();
//        try {
//            return ResourceUtils.getInputStreamForPath(configFile);
//        } catch (IOException e) {
//            throw new ConfigurationException("Unable to obtain input stream for cacheManagerConfigFile [" +
//                    configFile + "]", e);
//        }
        
        InputStream inputStream = null;  
        try {  
            inputStream = ResourceUtils.getInputStreamForPath(configFile);  
            byte[] bytes = IOUtils.toByteArray(inputStream);  
            InputStream in = new ByteArrayInputStream(bytes);  
            return in;  
        } catch (IOException e) {  
            throw new ConfigurationException("Unable to obtain input stream for cacheManagerConfigFile [" + configFile + "]", e);  
        } finally {  
            IOUtils.closeQuietly(inputStream);  
        }  
    }

    public final <K, V> Cache<K, V> getCache(String name) throws CacheException {

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Acquiring EhCache instance named [" + name + "]");
        }

        try {
            net.sf.ehcache.Ehcache cache = ensureCacheManager().getEhcache(name);
            if (cache == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Cache with name '{}' does not yet exist.  Creating now.", name);
                }
                this.manager.addCache(name);

                cache = manager.getCache(name);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Added EhCache named [" + name + "]");
                }
            } else {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Using existing EHCache named [" + cache.getName() + "]");
                }
            }
            return new EhCache<K, V>(cache);
        } catch (net.sf.ehcache.CacheException e) {
            throw new CacheException(e);
        }
    }

    public final void init() throws CacheException {
        ensureCacheManager();
    }

    private net.sf.ehcache.CacheManager ensureCacheManager() {
        try {
            if (this.manager == null) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("cacheManager property not set.  Constructing CacheManager instance... ");
                }
                this.manager = new net.sf.ehcache.CacheManager(getCacheManagerConfigFileInputStream());
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("instantiated Ehcache CacheManager instance.");
                }
                cacheManagerImplicitlyCreated = true;
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("implicit cacheManager created successfully.");
                }
            }
            return this.manager;
        } catch (Exception e) {
            throw new CacheException(e);
        }
    }

    public void destroy() {
        if (cacheManagerImplicitlyCreated) {
            try {
                net.sf.ehcache.CacheManager cacheMgr = getCacheManager();
                cacheMgr.shutdown();
            } catch (Throwable t) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Unable to cleanly shutdown implicitly created CacheManager instance.  " +
                            "Ignoring (shutting down)...", t);
                }
            } finally {
                this.manager = null;
                this.cacheManagerImplicitlyCreated = false;
            }
        }
    }
 
}  