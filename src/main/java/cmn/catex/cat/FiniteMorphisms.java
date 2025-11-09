/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex.cat;

import java.util.*;

/**
 * Mutable (for now) mapping of finite morphisms between elements.
 *
 * @param <E> typeof element
 */
public class FiniteMorphisms<E> {
    private final Map<E, List<E>> morphisms = new LinkedHashMap<>();

    public FiniteMorphisms() {
        // Default constructor
    }

    public FiniteMorphisms(final Map<E, List<E>> other) {
        other.entrySet().forEach(
                (entry -> { entry.getValue().forEach(value -> this.add(entry.getKey(), value)); }));
    }

    public FiniteMorphisms(final FiniteMorphisms<E> other) {
        // make a copy; it would be more efficient to copy the entire list at once, but this is simpler
        other.getMorphismMap().forEach(
                (key, values) -> {values.forEach(value -> this.add(key, value));}
        );
    }

    public void add(final E key, final E value) {
        morphisms.merge(
                key,
                new ArrayList<>(Collections.singletonList(value)), // copy temporary immutable list
                (oldList, newList) -> {
                    oldList.addAll(newList);
                    return oldList;
                });
    }

    public Map<E, List<E>> getMorphismMap() { return this.morphisms; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiniteMorphisms<?> that = (FiniteMorphisms<?>) o;
        return Objects.equals(morphisms, that.morphisms);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(morphisms);
    }
}
