import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
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
            int rangeStart = 51;
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
            String[] symbolColors = {"#FFFFFF", "#000000", "#FFFFFF", "#000000", "#8A2BE2", "#000000", "#A52A2A"};

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
                    if (!width.equals("138") && !width.equals("90") && !width.equals("132") && !width.equals("129") && !fill.equals("FFFFFF") && !fill.equals("#8A2BE2")) {
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
                        if (fill.equals("#8A2BE2")) {
                            operation = "стоянка пассажирского поезда";
                        } else if ((width.equals("138") || width.equals("90") || width.equals("132") || width.equals("129")) && fill.equals("#FFFFFF")) {
                            operation = "осмотр состава/стоянка";
                        }
                        for (int k = 0; k < heightRanges.length; k++) {
                            int range = heightRanges[k];
                            if (yValue >= range && yValue < range + 40) {
                                HeightRange heightRange = heightRangesMap.get(range);
                                heightRange.incrementSymbolCount(operation);
                                break;
                            }
                        }
                    }
                }

                // Check for <ellipse> elements and their attributes
                NodeList ellipseList = gElement.getElementsByTagName("ellipse");
                for (int j = 0; j < ellipseList.getLength(); j++) {
                    Element ellipseElement = (Element) ellipseList.item(j);
                    String fill = ellipseElement.getAttribute("fill");
                    String cy = ellipseElement.getAttribute("cy");

                    double yValue = Double.parseDouble(cy);

                    // Determine the type of operation based on the fill color
                    String operation = null;
                    if (fill.equals("#FF0000")) {
                        operation = "предъявление состава";
                    } else if (fill.equals("#000000")) {
                        operation = "отдача состава";
                    }

                    // Increment the count for the corresponding height range
                    if (operation != null) {
                        for (int k = 0; k < heightRanges.length; k++) {
                            int range = heightRanges[k];
                            if (yValue >= range && yValue < range + 40) {
                                HeightRange heightRange = heightRangesMap.get(range);
                                heightRange.incrementSymbolCount(operation);
                                break;
                            }
                        }
                    }
                }
            }

            for (Map.Entry<Integer, HeightRange> entry : heightRangesMap.entrySet()) {
                int start = entry.getKey();
                HeightRange range = entry.getValue();
                System.out.println("Диапазон " + start + " - " + (start + 39) + " имеет название: " + range.getName());
                for (String symbol : symbols) {
                    System.out.println("  " + symbol + ": " + range.getSymbolCount(symbol));
                }
                System.out.println("  предъявление состава (ellipses with #FF0000): " + range.getSymbolCount("предъявление состава"));
                System.out.println("  отдача состава (ellipses with #000000): " + range.getSymbolCount("отдача состава"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class HeightRange {
        private int range;
        private String name;
        private Map<String, Integer> symbolCount;

        public HeightRange(int range) {
            this.range = range;
            this.symbolCount = new LinkedHashMap<>();
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

        public int getSymbolCount(String symbol) {
            return symbolCount.getOrDefault(symbol, 0);
        }
    }
}
