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
            // Указание полного пути к файлу SVG, если не фуричит значит что-то с кодировкой в проекте
            File svgFile = new File("C:\\Users\\PCAdmin\\Desktop\\План_график_Туапсе_Сортировочная_Общая_Туапсе_Сорт_2_сутки_с_0_до.svg");

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

            Map<Integer, HeightRange> heightRangesMap = new LinkedHashMap<>();
            int numOfOperationInLine = 0;
            double elementStartX = 0.0;
            double elementEndX = 0.0;
            int elementY = 0; // высота элемента для дальнейшего определения его в диапазон

            for (int i = 0; i < gList.getLength(); i++) { // первый прогон для именования диапазонов
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

            // в код что выше просто верьте

            // Перебор каждого элемента <g>
            for (int i = 0; i < gList.getLength(); i++) { // основной прогон с определением элементов и добавлением их в диапазон
                Element gElement = (Element) gList.item(i);

                // Проверка наличия элемента <line> внутри <g> чтобы позже отсечь стрелки
                boolean hasLine = false;
                NodeList lineList = gElement.getElementsByTagName("line");
                if (lineList.getLength() > 0) {
                    hasLine = true;
                }

                // прием и отправление любого поезда (красные и черные треугольники)
                NodeList pathList = gElement.getElementsByTagName("path");
                for (int j = 0; j < pathList.getLength(); j++) {
                    Element pathElement = (Element) pathList.item(j);
                    String fill = pathElement.getAttribute("fill");
                    String stroke = pathElement.getAttribute("stroke");
                    String fillOpacity = pathElement.getAttribute("fill-opacity");

                    // отсюда начинается поиск треугольников
                    if (!hasLine && (stroke.equals("#FFFFFF") || stroke.equals("#000000")) && fillOpacity.equals("1.0")) { // Проверка на треугольник
                        String dAttribute = pathElement.getAttribute("d");
                        // Разбиение значения атрибута 'd' на отдельные команды
                        String[] commands = dAttribute.split("\\s+");
                        elementStartX = Double.parseDouble(commands[7]);
                        elementEndX = Double.parseDouble(commands[4]);
                        NodeList textList = gElement.getElementsByTagName("text");
                        String textContent = "0";
                        elementY = (int) Math.round(Double.parseDouble(commands[2]));
                        if (textList.getLength() > 0) { // если есть номер поезда, запишем
                            Element textElement = (Element) textList.item(0);
                            textContent = textElement.getTextContent();
                        }
                        for (int range : heightRanges) {
                            if (elementY >= range && elementY <= range + 39) { // определение в диапазон по высоте
                                if(commands[1].equals(commands[4]) && commands[4].equals(commands[10])) {
                                    if(fill.equals("#FF0000")){
                                        numOfOperationInLine = heightRangesMap.get(range).addAction(ActionType.PASSENGER_TRAIN_ARRIVAL, calculateHour(elementStartX), calculateHour(elementEndX), calculateHourDuration(elementStartX, elementEndX));
                                        heightRangesMap.get(range).getActions().get(numOfOperationInLine - 1).setOtherNumInfo(Integer.parseInt(textContent)); // номер поезда
                                        break;
                                    } else if(fill.equals("#000000")){
                                        numOfOperationInLine = heightRangesMap.get(range).addAction(ActionType.TRAIN_ARRIVAL, calculateHour(elementStartX), calculateHour(elementEndX), calculateHourDuration(elementStartX, elementEndX));
                                        heightRangesMap.get(range).getActions().get(numOfOperationInLine - 1).setOtherNumInfo(Integer.parseInt(textContent)); // номер поезда
                                        break;
                                    }
                                } else if(commands[1].equals(commands[7]) && commands[7].equals(commands[10])){
                                    if(fill.equals("#FF0000")){
                                        numOfOperationInLine = heightRangesMap.get(range).addAction(ActionType.PASSENGER_TRAIN_DEPARTURE, calculateHour(elementStartX), calculateHour(elementEndX), calculateHourDuration(elementStartX, elementEndX));
                                        heightRangesMap.get(range).getActions().get(numOfOperationInLine - 1).setOtherNumInfo(Integer.parseInt(textContent)); // номер поезда
                                        break;
                                    } else if(fill.equals("#000000")){
                                        numOfOperationInLine = heightRangesMap.get(range).addAction(ActionType.TRAIN_DEPARTURE, calculateHour(elementStartX), calculateHour(elementEndX), calculateHourDuration(elementStartX, elementEndX));
                                        heightRangesMap.get(range).getActions().get(numOfOperationInLine - 1).setOtherNumInfo(Integer.parseInt(textContent)); // номер поезда
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }



                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры
                // TODO: Остальные фигуры


            }
            // Вывод информации о каждом диапазоне и действиях в нём
            for (Map.Entry<Integer, HeightRange> entry : heightRangesMap.entrySet()) {
                int start = entry.getKey();
                HeightRange range = entry.getValue();
                System.out.println("Диапазон " + start + " - " + (start + 39) + " имеет название: " + range.getName());
                for (Action action : range.getActions()) {
                    System.out.println(action);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для подсчёта времени по x в минутах
    // тут округление идёт можно потом переделать
    public static int calculateHour(double x) {
        x = x - 250; // ноль находится на 250
        return (int) (Math.abs(x) / 180 * 60); // в часу 180
    }

    // Метод для подсчёта продолжительности в минутах
    // тут округление идёт можно потом переделать
    public static int calculateHourDuration(double elementStartX, double elementEndX) {
        return (int) (Math.abs(elementEndX - elementStartX) / 180 * 60);
    }
}
