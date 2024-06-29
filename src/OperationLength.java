import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OperationLength {
    public static void main(String[] args) {
        try {
            // ������ ����� SVG
            String svgFilePath = "A://����_������_������_�������������_�����_������_����_2_�����_�_0_��.svg"; // �������� �� ���� � ������ SVG �����
            String svgData = new String(Files.readAllBytes(Paths.get(svgFilePath)));

            // �������� ������������� ��������
            svgData = svgData.trim().replaceFirst("^([\\W]+)<", "<");

            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            Document document = factory.createDocument(null, new StringReader(svgData));

            // ��������� ��������� �� ���������� y = 170
            processElementsByYCoordinate(document, "path", 170);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processElementsByYCoordinate(Document document, String tagName, Integer coorY) {
        NodeList elements = document.getElementsByTagName(tagName);
        int triangleCount = 0;
        double totalLength = 0.0;

        for (int i = 0; i < elements.getLength(); i++) {
            Element element = (Element) elements.item(i);
            if (tagName.equals("path")) {
                String d = element.getAttribute("d");
                if (pathContainsY(d, coorY)) {
                    System.out.println("path element with y=170: " + element);
                    triangleCount++;
                    double lengthX = getTriangleLengthX(d);
                    System.out.println("Length of triangle along x axis: " + lengthX);
                    totalLength += lengthX;
                }
            }
        }

        System.out.println("Total number of triangles at y=170: " + triangleCount);
        System.out.println("Total length of triangles along x axis: " + totalLength);
    }

    private static boolean pathContainsY(String d, double y) {
        // �������� ������ �������� � ������ ������� �� �������
        d = d.replaceAll("[,]", " ").replaceAll("\\s+", " ").trim();

        String[] commands = d.split(" ");
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].matches("[MLHVCSQTAZ]")) {
                // Skip command characters
                continue;
            }
            if (i % 2 != 0) {
                // This should be the y coordinate if i is odd
                double currentY = Double.parseDouble(commands[i]);
                if (currentY == y) {
                    return true;
                }
            }
        }
        return false;
    }

    private static double getTriangleLengthX(String d) {
        // �������� ������ �������� � ������ ������� �� �������
        d = d.replaceAll("[,]", " ").replaceAll("\\s+", " ").trim();

        String[] commands = d.split(" ");
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        boolean expectX = true;  // ���������, ��� ��������� ���������� ������ ���� X

        for (int i = 0; i < commands.length; i++) {
            if (commands[i].matches("[MLHVCSQTAZ]")) {
                // Skip command characters and reset expectation
                expectX = true;
                continue;
            }

            if (expectX) {
                // This should be the x coordinate
                try {
                    double currentX = Double.parseDouble(commands[i]);
                    if (currentX < minX) {
                        minX = currentX;
                    }
                    if (currentX > maxX) {
                        maxX = currentX;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format: " + commands[i]);
                }
                expectX = false;  // Next should be Y
            } else {
                // This should be the y coordinate
                expectX = true;  // Reset to expect X next
            }
        }
        return maxX - minX;
    }
}
