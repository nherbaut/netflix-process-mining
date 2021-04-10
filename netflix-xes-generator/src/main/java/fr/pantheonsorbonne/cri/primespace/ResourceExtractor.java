package fr.pantheonsorbonne.cri.primespace;

import java.util.function.BiConsumer;

public interface ResourceExtractor<R, T> {

	public BiConsumer<R, T> getConsumer();

	
}