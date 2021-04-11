package fr.pantheonsorbonne.cri.cache;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ConcurrentMapCachedResource implements CachedResource {
	ConcurrentMap<String, Object> cache = new ConcurrentHashMap<String, Object>();

	@SuppressWarnings("unchecked")
	@Override
	public <T> Optional<T> lookup(String key, Class<T> klass) {
		Object obj = cache.get(key);
		if (obj == null) {
			return Optional.empty();
		} else if (obj instanceof String && "".equals(obj)) {
			return Optional.ofNullable(null);
		} else if (klass.isInstance(obj)) {
			return Optional.of((T) obj);
		} else {
			throw new RuntimeException("invalid object in map");
		}
	}

	@Override
	public <T> void cache(String key, Class<T> klass) {
		cache.put(key, "");

	}

	@Override
	public <T> void cache(String key, T obj) {
		cache.put(key, obj);

	}

	@Override
	public void close() throws IOException {
		// noop

	}
}