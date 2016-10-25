/*
 * (C) Copyright 2016-2016, by Dimitrios Michail and Contributors.
 *
 * JGraphT : a free Java graph-theory library
 *
 * This program and the accompanying materials are dual-licensed under
 * either
 *
 * (a) the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation, or (at your option) any
 * later version.
 *
 * or (per the licensee's choosing)
 *
 * (b) the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation.
 */
package org.jgrapht.alg.shortestpath;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.interfaces.ShortestPathAlgorithm.SingleSourcePaths;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.GraphWalk;

/**
 * A default implementation of {@link SingleSourcePaths} which uses linear space.
 * 
 * <p>
 * In order to keep space to linear, the paths are recomputed in each invocation of the
 * {@link #getPath(Object)} method. The complexity of {@link #getPath(Object)} is linear to the
 * number of edges of the path while the complexity of {@link #getWeight(Object)} is O(1).
 * 
 * @author Dimitrios Michail
 *
 * @param <V> the graph vertex type
 * @param <E> the graph edge type
 */
class TreeSingleSourcePaths<V, E>
    implements SingleSourcePaths<V, E>
{
    private Graph<V, E> g;
    private V source;
    private Map<V, Pair<Double, E>> map;

    /**
     * Construct a new instance.
     * 
     * @param g the graph
     * @param source the source vertex
     * @param distanceAndPredecessorMap a map which contains for each vertex the distance and the
     *        last edge that was used to discover the vertex. The map does not need to contain any
     *        entry for the source vertex.
     */
    public TreeSingleSourcePaths(
        Graph<V, E> g, V source, Map<V, Pair<Double, E>> distanceAndPredecessorMap)
    {
        this.g = Objects.requireNonNull(g, "Graph is null");
        this.source = Objects.requireNonNull(source, "Source vertex is null");
        this.map = Objects
            .requireNonNull(distanceAndPredecessorMap, "Distance and predecessor map is null");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Graph<V, E> getGraph()
    {
        return g;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getSourceVertex()
    {
        return source;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getWeight(V targetVertex)
    {
        Pair<Double, E> p = map.get(targetVertex);
        if (p == null) {
            if (source.equals(targetVertex)) {
                return 0d;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        } else {
            return p.getFirst();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GraphPath<V, E> getPath(V targetVertex)
    {
        if (source.equals(targetVertex)) {
            return new GraphWalk<>(g, source, targetVertex, null, Collections.emptyList(), 0d);
        }
        LinkedList<E> edgeList = new LinkedList<>();
        double weight = Double.POSITIVE_INFINITY;

        V cur = targetVertex;
        Pair<Double, E> p = map.get(cur);
        if (p != null) {
            weight = 0d;
        }
        while (p != null) {
            E e = p.getSecond();
            if (e == null) {
                break;
            }
            edgeList.addFirst(e);
            weight += g.getEdgeWeight(e);
            cur = Graphs.getOppositeVertex(g, e, cur);
            p = map.get(cur);
        }

        return new GraphWalk<>(g, source, targetVertex, null, edgeList, weight);
    }

}
