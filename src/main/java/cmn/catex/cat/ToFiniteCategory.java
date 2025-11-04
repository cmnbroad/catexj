/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex.cat;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @param <S>
 * @param <E>
 */
// T: type of the underlying source object (i.e., interval, or graph)
// E: type of the elements in the category (i.e., intervals, or nodes)
public interface ToFiniteCategory<S, E> {
    FiniteCategory<S, E> toFiniteCategory(Function<E, List<E>> getElements, Function<S, Map<E, List<E>>> getMorphisms);
}
