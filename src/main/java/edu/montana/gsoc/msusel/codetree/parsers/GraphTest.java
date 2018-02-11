package edu.montana.gsoc.msusel.codetree.parsers;

import com.google.common.graph.*;

public class GraphTest {

    public static void main(String args[]) {
        MutableValueGraph<String, String> valGraph = ValueGraphBuilder.directed().build();
        MutableGraph<String> graph = GraphBuilder.directed().build();

        valGraph.addNode("A");
        valGraph.addNode("B");
        valGraph.putEdgeValue("A", "B", "one");
        valGraph.putEdgeValue("A", "B", "two");

        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");
        graph.putEdge("A", "C");
        graph.putEdge("A", "B");
        graph.putEdge("B", "C");

        System.out.println(generateDot(valGraph));
        System.out.println();
        System.out.println(generateDot(graph));
    }

    public static String generateDot(Graph<String> graph) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        for (EndpointPair<String> pair : graph.edges()) {
            builder.append("  " + pair.source() + " -> " + pair.target() + "\n");
        }
        builder.append("}");

        return builder.toString();
    }

    public static String generateDot(ValueGraph<String, String> graph) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph {\n");
        for (EndpointPair<String> pair : graph.edges()) {
            builder.append("  " + pair.nodeU() + " -> " + pair.nodeV() + "\n");
        }
        builder.append("}");

        return builder.toString();
    }
}
