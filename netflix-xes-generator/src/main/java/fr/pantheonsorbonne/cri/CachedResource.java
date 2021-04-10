package fr.pantheonsorbonne.cri;

import java.util.Optional;

interface CachedResource {

	<T> Optional<T> lookup(String key, Class<T> klass);

	<T> void cache(String key, T obj);

	<T> void cache(String key, Class<T> klass);

}