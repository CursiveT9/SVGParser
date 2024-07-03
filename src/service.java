//import org.w3c.dom.*;
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import java.io.File;
//import java.util.HashMap;
//import java.util.Map;
//
//public class service {
//
//    public static void main(String[] args) {
//        try {
//            // Load and parse the SVG file
//            File svgFile = new File("/Users/enterprise/IdeaProjects/SVGParser/info.svg");
//            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//            Document doc = dBuilder.parse(svgFile);
//            doc.getDocumentElement().normalize();
//
//            // Define the symbols to look for
//            String[] symbols = {"осмотр состава", "стоянка", "окончание формирования",
//                    "опробование тормозов", "закрепление состава",
//                    "стоянка пассажирского поезда", "расстановка по фронтам", "выгрузка"};
//
//            // Initialize a map to store counts for each line and symbol
//            Map<String, Map<String, Integer>> lineSymbolCount = new HashMap<>();
//
//            // Get all lines (assuming <line> tags are used to represent the 24-hour schedules)
//            NodeList lineList = doc.getElementsByTagName("line");
//            for (int i = 0; i < lineList.getLength(); i++) {
//                Node lineNode = lineList.item(i);
//                if (lineNode.getNodeType() == Node.ELEMENT_NODE) {
//                    Element lineElement = (Element) lineNode;
//                    String lineId = lineElement.getAttribute("id");
//
//                    // Initialize the count map for this line
//                    Map<String, Integer> symbolCount = new HashMap<>();
//                    for (String symbol : symbols) {
//                        symbolCount.put(symbol, 0);
//                    }
//
//                    // Get all text elements associated with this line
//                    NodeList textList = lineElement.getElementsByTagName("text");
//                    for (int j = 0; j < textList.getLength(); j++) {
//                        Node textNode = textList.item(j);
//                        if (textNode.getNodeType() == Node.ELEMENT_NODE) {
//                            Element textElement = (Element) textNode;
//                            String textContent = textElement.getTextContent().trim();
//
//                            // Check if the text content matches any of the symbols
//                            if (symbolCount.containsKey(textContent)) {
//                                symbolCount.put(textContent, symbolCount.get(textContent) + 1);
//                            }
//                        }
//                    }
//
//                    // Store the symbol counts for this line
//                    lineSymbolCount.put(lineId, symbolCount);
//                }
//            }
//
//            // Output the results
//            for (Map.Entry<String, Map<String, Integer>> entry : lineSymbolCount.entrySet()) {
//                String lineId = entry.getKey();
//                Map<String, Integer> symbolCount = entry.getValue();
//                System.out.println("Line: " + lineId);
//                for (Map.Entry<String, Integer> symbolEntry : symbolCount.entrySet()) {
//                    String symbol = symbolEntry.getKey();
//                    int count = symbolEntry.getValue();
//                    System.out.println("  " + symbol + ": " + count);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
//

import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;

public class service {
    public static void main(String[] args) {
        try {
            // Path to the SVG file
            File svgFile = new File("/Users/enterprise/IdeaProjects/SVGParser/info.svg");

            // Create a factory and builder for parsing XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Parse the XML document
            Document document = builder.parse(svgFile);
            document.getDocumentElement().normalize();

            // Get all <g> elements in the document
            NodeList gList = document.getElementsByTagName("g");

            // Define the height ranges for each line (assuming each range covers 40 units)
            int[] heightRanges = new int[138]; // Total number of ranges
            int rangeStart = 60; // Changed from 51 to 60
            for (int i = 0; i < heightRanges.length; i++) {
                heightRanges[i] = rangeStart + (i * 40);
            }

            // Define the map to store counts of figures for each height range
            Map<Integer, HeightRange> heightRangesMap = new LinkedHashMap<>();

            // Initialize height ranges map
            for (int i = 0; i < heightRanges.length; i++) {
                heightRangesMap.put(heightRanges[i], new HeightRange(heightRanges[i]));
            }

            // Define symbols and their fill colors
            String[] symbols = {"осмотр состава/стоянка", "окончание формирования",
                    "опробование тормозов", "закрепление состава",
                    "стоянка пассажирского поезда", "расстановка по фронтам", "выгрузка"};
            String[] symbolColors = {"#FFFFFF", "#000000", "#FFFFFF", "#000000", "#800080", "#000000", "#A52A2A"};

            // First, extract the names and corresponding y-coordinates for each line
            for (int i = 0; i < gList.getLength(); i++) {
                Element gElement = (Element) gList.item(i);
                String gId = gElement.getAttribute("id");

                if ("DailyDiagramCaptionView".equals(gId)) {
                    NodeList textList = gElement.getElementsByTagName("text");
                    for (int j = 0; j < textList.getLength(); j++) {
                        Element textElement = (Element) textList.item(j);
                        String textContent = textElement.getTextContent().trim();
                        String textY = textElement.getAttribute("y");
                        double textYValue = Double.parseDouble(textY);

                        // Find the corresponding height range
                        for (int k = 0; k < heightRanges.length; k++) {
                            int range = heightRanges[k];
                            if (textYValue >= range && textYValue < range + 40) {
                                HeightRange heightRange = heightRangesMap.get(range);
                                heightRange.setName(textContent);
                                break;
                            }
                        }
                    }
                }
            }

            // Process each <g> element to count symbols
            for (int i = 0; i < gList.getLength(); i++) {
                Element gElement = (Element) gList.item(i);

                // Check for <rect> elements and their attributes
                NodeList rectList = gElement.getElementsByTagName("rect");
                for (int j = 0; j < rectList.getLength(); j++) {
                    Element rectElement = (Element) rectList.item(j);
                    String fill = rectElement.getAttribute("fill");
                    String y = rectElement.getAttribute("y");
                    String width = rectElement.getAttribute("width");

                    // Skip elements that do not have the desired width
                    if (!width.equals("138") && !width.equals("90")) {
                        continue;
                    }

                    double yValue = Double.parseDouble(y);

                    // Determine the type of operation based on the fill color
                    String operation = null;
                    for (int k = 0; k < symbolColors.length; k++) {
                        if (fill.equals(symbolColors[k])) {
                            operation = symbols[k];
                            break;
                        }
                    }

                    // Increment the count for the corresponding height range
                    if (operation != null) {
                        for (int k = 0; k < heightRanges.length; k++) {
                            int range = heightRanges[k];
                            if (yValue >= range && yValue < range + 40) {
                                HeightRange heightRange = heightRangesMap.get(range);
                                heightRange.addOperation(operation);
                                break;
                            }
                        }
                    }
                }
            }

            // Analyze operations preceding "осмотр состава/стоянка"
            Map<String, Integer> precedingOperationCounts = new HashMap<>();
            for (HeightRange range : heightRangesMap.values()) {
                List<String> operations = range.getOperations();
                for (int i = 1; i < operations.size(); i++) {
                    if (operations.get(i).equals("осмотр состава/стоянка")) {
                        String precedingOperation = operations.get(i - 1);
                        precedingOperationCounts.put(precedingOperation, precedingOperationCounts.getOrDefault(precedingOperation, 0) + 1);
                    }
                }
            }

            // Output the results with line names and preceding operation counts
            for (Map.Entry<Integer, HeightRange> entry : heightRangesMap.entrySet()) {
                int start = entry.getKey();
                HeightRange range = entry.getValue();
                System.out.println("Диапазон " + start + " - " + (start + 39) + " имеет название: " + range.getName());
                for (String symbol : symbols) {
                    System.out.println("  " + symbol + ": " + range.getSymbolCount(symbol));
                }
            }

            System.out.println("Частота операций, предшествующих 'осмотр состава/стоянка':");
            for (Map.Entry<String, Integer> entry : precedingOperationCounts.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class HeightRange {
        private int range;
        private String name;
        private Map<String, Integer> symbolCount;
        private List<String> operations;

        public HeightRange(int range) {
            this.range = range;
            this.symbolCount = new LinkedHashMap<>();
            this.operations = new ArrayList<>();
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void incrementSymbolCount(String symbol) {
            symbolCount.put(symbol, symbolCount.getOrDefault(symbol, 0) + 1);
        }

        public void addOperation(String operation) {
            operations.add(operation);
            incrementSymbolCount(operation);
        }

        public int getSymbolCount(String symbol) {
            return symbolCount.getOrDefault(symbol, 0);
        }

        public List<String> getOperations() {
            return operations;
        }
    }
}
