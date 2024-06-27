import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // Указание полного пути к файлу SVG
            File svgFile = new File("C:/Users/PCAdmin/Desktop/План_график_Туапсе_Сортировочная_Общая_Туапсе_Сорт_2_сутки_с_0_до.svg");

            // Создание фабрики и билдера для парсинга XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            // Парсинг XML-документа
            Document document = builder.parse(svgFile);
            document.getDocumentElement().normalize();

            // Получение списка всех элементов <g> в документе
            NodeList gList = document.getElementsByTagName("g");

            // Диапазоны высот, которые нас интересуют (высота учитывается согласно y)
            int[] heightRanges = new int[138]; // Общее количество диапазонов
            int rangeStart = 51;
            for (int i = 0; i < heightRanges.length; i++) {
                heightRanges[i] = rangeStart + (i * 40);
            }

            // Массив для подсчёта количества фигур в каждом диапазоне
            int[] countByRange = new int[heightRanges.length];

            Map<Integer, HeightRange> heightRangesMap = new LinkedHashMap<>();

            for (int i = 0; i < gList.getLength(); i++) {
                Element gElement = (Element) gList.item(i);
                String gId = gElement.getAttribute("id");
                if ("DailyDiagramCaptionView".equals(gId)) {
                    NodeList childGList = gElement.getElementsByTagName("g");
                    // Проходим по списку дочерних <g> элементов
                    for (int j = 0; j < childGList.getLength(); j++) {
                        Element childGElement = (Element) childGList.item(j);
                        NodeList textList = childGElement.getElementsByTagName("text");
                        if (textList.getLength() > 0) {
                            Element textElement = (Element) textList.item(0);
                            String textContent = textElement.getTextContent().trim();

                            // Извлекаем координаты текста
                            String textY = textElement.getAttribute("y");
                            double textYValue = Double.parseDouble(textY);

                            // Находим соответствующий диапазон высоты
                            for (int k = 0; k < heightRanges.length; k++) {
                                int range = heightRanges[k];
                                if (textYValue >= range && textYValue <= range + 39) {
                                    // Создаем объект HeightRange и добавляем в Map
                                    HeightRange heightRange = new HeightRange(range, textContent);
                                    heightRangesMap.put(range, heightRange);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Перебор каждого элемента <g>
            for (int i = 0; i < gList.getLength(); i++) {
                Element gElement = (Element) gList.item(i);

                // Проверка наличия элемента <line> внутри <g>
                boolean hasLine = false;
                NodeList lineList = gElement.getElementsByTagName("line");
                if (lineList.getLength() > 0) {
                    hasLine = true;
                }

                // Извлечение элементов <path>
                NodeList pathList = gElement.getElementsByTagName("path");
                for (int j = 0; j < pathList.getLength(); j++) {
                    Element pathElement = (Element) pathList.item(j);
                    String d = pathElement.getAttribute("d");
                    String fill = pathElement.getAttribute("fill");

                    // Проверка, что это нужный нам треугольник (исключая стрелочки)
                    if (d.contains("L") && fill.equals("#FF0000") && !hasLine) {
                        NodeList textList = gElement.getElementsByTagName("text");
                        for (int k = 0; k < textList.getLength(); k++) {
                            Element textElement = (Element) textList.item(k);
                            String y = textElement.getAttribute("y");
                            String x = textElement.getAttribute("x");
                            String textContent = textElement.getTextContent();
                            int textY = Integer.parseInt(y);
                            for (int l = 0; l < heightRanges.length; l++) {
                                int range = heightRanges[l];
                                if (textY >= range && textY <= range + 39) {
                                    countByRange[l]++;
                                    heightRangesMap.get(range).incrementNumPassengerTrainArrivalsAndDepartures();
                                    //System.out.println("Треугольник: x=" + x + ", y=" + y + ", текст=" + textContent + ", диапазон=" + range + "-" + (range + 39));
                                    break;
                                }
                            }
                        }
                    }
                }





            }

            // Выводим результаты в порядке ключей
            for (Map.Entry<Integer, HeightRange> entry : heightRangesMap.entrySet()) {
                int start = entry.getKey();
                HeightRange range = entry.getValue();
                System.out.println("Диапазон " + start + " - " + (start + 39) +" имеет название: " + range.getName() + " Количесто Приемы и отправления пассажирскоих поездов: " + range.getNumPassengerTrainArrivalsAndDepartures());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
