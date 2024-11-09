package com.lucasj.gamedev.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class ConcurrentList<T> implements Iterable<T>{

	private java.util.List <T> list;
	private java.util.List <T> toAdd;
	private java.util.List <T> toRemove;
	
	public ConcurrentList() {
		list = new ArrayList<>();
		toAdd = new ArrayList<>();
		toRemove = new ArrayList<>();
//		allLists.add((ConcurrentList<Object>) this);
	}
	
	public ConcurrentList(List<T> list) {
		this.list = list;
		toAdd = new ArrayList<>();
		toRemove = new ArrayList<>();
	}
	
	public void update() {
		this.list.addAll(toAdd);
		this.list.removeAll(toRemove);
		toAdd.clear();
		toRemove.clear();
	}
	
	public void add(T obj) {
		toAdd.add(obj);
	}
	
	public void remove(T obj) {
		toRemove.add(obj);
	}

	public boolean contains(Object arg0) {
		return list.contains(arg0);
	}

	public boolean containsAll(Collection<?> arg0) {
		return list.containsAll(arg0);
	}

	public boolean equals(Object arg0) {
		return list.equals(arg0);
	}

	public void forEach(Consumer<? super T> arg0) {
		list.forEach(arg0);
	}

	public T get(int arg0) {
		return list.get(arg0);
	}

	public T getFirst() {
		return list.getFirst();
	}

	public T getLast() {
		return list.getLast();
	}

	public int hashCode() {
		return list.hashCode();
	}

	public int indexOf(Object arg0) {
		return list.indexOf(arg0);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<T> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object arg0) {
		return list.lastIndexOf(arg0);
	}

	public ListIterator<T> listIterator() {
		return list.listIterator();
	}

	public ListIterator<T> listIterator(int arg0) {
		return list.listIterator(arg0);
	}

	public Stream<T> parallelStream() {
		return list.parallelStream();
	}

	public boolean retainAll(Collection<?> arg0) {
		return list.retainAll(arg0);
	}

	public java.util.List<T> reversed() {
		return list.reversed();
	}

	public T set(int arg0, T arg1) {
		return list.set(arg0, arg1);
	}

	public int size() {
		return list.size();
	}

	public void sort(Comparator<? super T> arg0) {
		list.sort(arg0);
	}

	public Spliterator<T> spliterator() {
		return list.spliterator();
	}

	public Stream<T> stream() {
		return list.stream();
	}

	public java.util.List<T> subList(int arg0, int arg1) {
		return list.subList(arg0, arg1);
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(IntFunction<T[]> generator) {
		return list.toArray(generator);
	}

	public <T> T[] toArray(T[] arg0) {
		return list.toArray(arg0);
	}
	
	public List<T> toList() {
		return this.list;
	}

	public void clear() {
		list.clear();
		toAdd.clear();
		toRemove.clear();
	}
	
}
