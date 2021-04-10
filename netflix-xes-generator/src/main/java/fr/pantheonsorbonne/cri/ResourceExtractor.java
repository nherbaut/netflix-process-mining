package fr.pantheonsorbonne.cri;

import java.util.function.BiConsumer;

public interface ResourceExtractor<R, T> {

	public BiConsumer<R, T> getConsumer();

	
}