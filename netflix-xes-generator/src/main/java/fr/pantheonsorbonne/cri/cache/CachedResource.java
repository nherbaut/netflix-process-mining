package fr.pantheonsorbonne.cri.cache;

import java.io.Closeable;
import java.util.Optional;

public interface CachedResource extends Closeable {

	<T> Optional<T> lookup(String key, Class<T> klass);

	<T> void cache(String key, T obj);

	<T> void cache(String key, Class<T> klass);

}