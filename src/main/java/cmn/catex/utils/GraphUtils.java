/**
 * © Copyright (c) Christopher Norman 2025
 */
package cmn.catex.utils;

import htsjdk.io.IOPath;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.nio.Attribute;
import org.jgrapht.nio.DefaultAttribute;
import org.jgrapht.nio.dot.DOTExporter;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class GraphUtils {
    private static String graphvizAppPath = "/opt/homebrew/bin/dot"; // Graphviz installation path

    public enum GraphFileType {
        PNG,
        PDF
    }

    public static <E> void writeGraphToDOT(final Graph<E, DefaultEdge> graph, final IOPath path){
        // Export the graph to DOT format
        final DOTExporter<E, DefaultEdge> exporter = new DOTExporter<>();
        exporter.setVertexAttributeProvider((final E e) -> {
            final Map<String, Attribute> map = new LinkedHashMap<>();
            map.put("label", DefaultAttribute.createAttribute(e.toString()));
            return map;
        });
        exporter.exportGraph(graph, path.getOutputStream());
    }

    public static void writeDOTToExternal(final IOPath ioDotPath, final IOPath ioOutputPath, final GraphFileType graphFileType) {
        try {
            final String graphTypeArg = switch (graphFileType) {
                case PNG -> "-Tpng";
                case PDF -> "-Tpdf";
            };
            final Process process = new ProcessBuilder(
                    graphvizAppPath,
                    graphTypeArg,
                    ioDotPath.getRawInputString(),
                    "-o",
                    ioOutputPath.getRawInputString()).start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("Graphviz process failed with exit code: " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
