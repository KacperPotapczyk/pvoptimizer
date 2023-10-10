package com.github.kacperpotapczyk.pvoptimizer.service;

public interface Mapper<T, R> {

    R map(T input);
}