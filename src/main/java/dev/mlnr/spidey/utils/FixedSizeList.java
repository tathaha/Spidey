package dev.mlnr.spidey.utils;

import java.util.LinkedList;

public class FixedSizeList<T> extends LinkedList<T> {
	private final int maxSize;

	public FixedSizeList(int size) {
		this.maxSize = size;
	}

	@Override
	public boolean add(T value) {
		if (size() == maxSize) {
			removeLast();
		}
		addFirst(value);
		return true;
	}
}