package fr.pantheonsorbonne.cri.cache;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisCachedResource implements Closeable, CachedResource {

	private final RedisClient redisClient;
	private final StatefulRedisConnection<String, String> connection;
	private final RedisCommands<String, String> syncCommands;
	private final static Logger LOGGER = LoggerFactory.getLogger(RedisCachedResource.class);

	public RedisCachedResource(String redisConnectionString) {
		redisClient = RedisClient.create(redisConnectionString);
		connection = redisClient.connect();
		syncCommands = connection.sync();

	}

	@Override
	public <T> Optional<T> lookup(String key, Class<T> klass) {
		String fullKey = klass.getSimpleName() + ":" + key;
		String obj = syncCommands.get(fullKey);
		if (obj == null) {
			LOGGER.trace("{} miss", key);
			return Optional.empty();
		} else if (obj.equals("")) {
			LOGGER.trace("{}, hit (empty)", key);
			return null;
		} else {
			LOGGER.trace("{} hit ", key);
			return Optional.of(new Gson().fromJson(obj, klass));
		}
	}

	@Override
	public <T> void cache(String key, T obj) {
		syncCommands.set(obj.getClass().getSimpleName() + ":" + key, new Gson().toJson(obj));
	}

	@Override
	public <T> void cache(String key, Class<T> klass) {
		syncCommands.set(klass.getSimpleName() + ":" + key, "");
	}

	@Override
	public void close() throws IOException {

		connection.close();
		redisClient.shutdown();

	}

}