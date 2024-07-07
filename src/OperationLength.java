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
            // Чтение файла SVG
            String svgFilePath = "A://План_график_Туапсе_Сортировочная_Общая_Туапсе_Сорт_2_сутки_с_0_до.svg"; // замените на путь к вашему SVG файлу
            String svgData = new String(Files.readAllBytes(Paths.get(svgFilePath)));

            // Удаление нежелательных символов
            svgData = svgData.trim().replaceFirst("^([\\W]+)<", "<");

            String parser = XMLResourceDescriptor.getXMLParserClassName();
            SAXSVGDocumentFactory factory = new SAXSVGDocumentFactory(parser);
            Document document = factory.createDocument(null, new StringReader(svgData));

            // Найти все элементы <path> и обработать их
            processElementsByYCoordinate(document, "path", 170);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processElementsByYCoordinate(Document document, String tagName, int coorY) {
        NodeList paths = document.getElementsByTagName(tagName);
        int triangleCount = 0;
        double totalLength = 0.0;

        // Поиск элементов <rect> перед каждым <path>
        for (int i = 0; i < paths.getLength(); i++) {
            Element pathElement = (Element) paths.item(i);
            if (tagName.equals("path")) {
                // Поиск всех элементов <rect> перед текущим <path>
                NodeList rects = pathElement.getOwnerDocument().getElementsByTagName("rect");
                int rectCount = 0;

                // Проверка координаты y элементов <rect> и подсчет их
                for (int j = 0; j < rects.getLength(); j++) {
                    Element rectElement = (Element) rects.item(j);
                    double rectY = Double.parseDouble(rectElement.getAttribute("y"));
                    if (rectY < coorY) {
                        rectCount++;
                    }
                }

                // Обработка элемента <path> (в вашем случае, вывод информации о треугольниках)
                String d = pathElement.getAttribute("d");
                if (pathContainsY(d, coorY)) {
                    System.out.println("path element with y=" + coorY + ": " + pathElement);
                    triangleCount++;
                    double lengthX = getTriangleLengthX(d);
                    System.out.println("Length of triangle along x axis: " + lengthX);
                    totalLength += lengthX;
                }

                // Вывод количества элементов <rect> перед текущим <path>
                System.out.println("Number of <rect> elements before this <path>: " + rectCount);
            }
        }

        System.out.println("Total number of triangles at y=" + coorY + ": " + triangleCount);
        System.out.println("Total length of triangles along x axis: " + totalLength);
    }

    private static boolean pathContainsY(String d, double y) {
        // Реализация метода pathContainsY как в вашем предыдущем коде
        return false;
    }

    private static double getTriangleLengthX(String d) {
        // Реализация метода getTriangleLengthX как в вашем предыдущем коде
        return 0.0;
    }
}
