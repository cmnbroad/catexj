/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex.cat;

/**
 * @param <S> type of originating source object
 * @param <E> type of elements in this category
 */
public interface Category<S, E> {
    S getSource();
}
