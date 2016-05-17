package org.jerry.frameworks.base.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.StringUtils;

/**
 * 基础cache切面
 *
 * <p>Date : 16/5/17</p>
 * <p>Time : 下午1:38</p>
 *
 * @author jerry
 */
public class BaseCacheAspect implements InitializingBean {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private CacheManager cacheManager;
    private Cache cache;
    protected String cacheName;

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        cache = cacheManager.getCache(cacheName);
    }

    public void clear() {
        logger.debug("cacheName:{}, cache clear.", cacheName);
        cache.clear();
    }

    public void evict(String key) {
        logger.debug("cacheName:{}, cacheKey:{}", cacheName, key);
        cache.evict(key);
    }

    public <T> T get(Object key) {
        logger.debug("cacheName:{}, get key:{}", cacheName, key);
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        Cache.ValueWrapper value = cache.get(key);
        if (value == null) {
            return null;
        }
        return (T)value;
    }

    public void put(String key, Object value) {
        logger.debug("cacheName:{}, put key:{}", cacheName, key);
        this.cache.put(key, value);
    }
}
