package com.github.kacperpotapczyk.pvoptimizer.optimizer.service.mapper;

/**
 * One way mapping interface.
 * @param <T> input type
 * @param <R> output type
 */
public interface Mapper<T, R> {

    /**
     * Mapping method between types.
     * @param input input object of type T
     * @return output object of type R
     */
    R map(T input);
}