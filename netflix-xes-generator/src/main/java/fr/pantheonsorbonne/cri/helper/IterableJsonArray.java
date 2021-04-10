package fr.pantheonsorbonne.cri.helper;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

class IterableJsonArray<T> implements Iterator<T>, Iterable<T>, Closeable {

	private final JsonReader reader;
	final Class<T> typeParameterClass;
	private static final Gson GSON = new Gson();

	public IterableJsonArray(InputStream is, Class<T> typeParameterClass) {
		this.typeParameterClass = typeParameterClass;
		reader = new JsonReader(new InputStreamReader(is));
		try {
			reader.beginArray();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean hasNext() {
		try {
			return reader.hasNext();
		} catch (IOException e) {
			return false;
		}
	}

	@Override
	public T next() {
		return GSON.fromJson(reader, typeParameterClass);
	}

	@Override
	public void close() throws IOException {
		reader.endArray();
		this.reader.close();

	}

	@Override
	public Iterator<T> iterator() {
		return this;
	}

}