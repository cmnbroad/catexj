/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex.cat;

import cmn.catex.utils.GraphUtils;
import htsjdk.io.IOPath;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DirectedMultigraph;

import java.util.*;

/**
 * Finite, immutable category. Identity morphisms are implicit and are not modeled explicitly.
 *
 * @param <S> type of originating source object
 * @param <E> type of elements in this category
 */
public class FiniteCategory<S, E> implements Category<S, E> {
    private final S source;
    private Set<E> elements = new LinkedHashSet<>();
    private FiniteMorphisms<E> morphisms;

    public FiniteCategory(final S source, final List<E> elements, final FiniteMorphisms<E> morphisms) {
        this.source = source;
        elements.forEach(e -> {
            this.elements.add(e);
        });
        this.morphisms = new FiniteMorphisms<>(morphisms);
    }

    public Map<E, List<E>> getMorphismMap() {
        return morphisms.getMorphismMap();
    }

    public Set<E> getElements() {
        return elements;
    }

    @Override
    public S getSource() {
        return source;
    }

    public Graph<E, DefaultEdge> asGraph() {
        final DirectedMultigraph<E, DefaultEdge> graph = new DirectedMultigraph<>(DefaultEdge.class);
        for (final E element : getElements()) {
            graph.addVertex(element);
        }
        getMorphismMap().entrySet().forEach(entry ->
                entry.getValue().forEach(value -> graph.addEdge(entry.getKey(), value)));
        return graph;
    }

    // write this category to a path as a DOT file
    public void writeAsGraph(final IOPath dotPath) {
        GraphUtils.writeGraphToDOT(asGraph(), dotPath);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FiniteCategory<?, ?> that = (FiniteCategory<?, ?>) o;
        return Objects.equals(source, that.source) && Objects.equals(elements, that.elements) && Objects.equals(morphisms, that.morphisms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, elements, morphisms);
    }
}
