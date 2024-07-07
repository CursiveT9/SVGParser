import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            // Указание полного пути к файлу SVG
            File svgFile = new File("A://NIIAS.svg");

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

            for (int i = 0; i < gList.getLength(); i++) {
                Element gElement = (Element) gList.item(i);

                // Перебор всех элементов <rect> в текущем <g>
                NodeList rectList = gElement.getElementsByTagName("rect");
                for (int j = 0; j < rectList.getLength(); j++) {
                    Element rectElement = (Element) rectList.item(j);
                    String rectFill = rectElement.getAttribute("fill");
                    if (!"#FFFFFF".equals(rectFill)) {
                        continue;
                    }

                    // Проверка наличия элемента <line> внутри <g>
                    NodeList lineList = gElement.getElementsByTagName("line");
                    if (lineList.getLength() == 1) {
                        for (int k = 0; k < lineList.getLength(); k++) {
                            Element lineElement = (Element) lineList.item(k);
                            double y1 = Double.parseDouble(lineElement.getAttribute("y1"));
                            double y2 = Double.parseDouble(lineElement.getAttribute("y2"));
                            if (y1 <= y2) {
                                continue;
                            }

                            try {
                                double x1 = Double.parseDouble(lineElement.getAttribute("x1"));
                                double x2 = Double.parseDouble(lineElement.getAttribute("x2"));
                                double lineLength = Math.abs(x2 - x1);

                                String rectY = rectElement.getAttribute("y");
                                double rectYValue = Double.parseDouble(rectY);

                                // Нахождение соответствующего диапазона и увеличение счетчика
                                for (int l = 0; l < heightRanges.length; l++) {
                                    int range = heightRanges[l];
                                    if (rectYValue >= range && rectYValue <= range + 39) {
                                        countByRange[l]++;
                                        heightRangesMap.get(range).incrementCouplingShuntingLocomotive();
                                        heightRangesMap.get(range).addTotalLengthCouplingShuntingLocomotive(lineLength);
                                        break;
                                    }
                                }
                            } catch (NumberFormatException e) {
                                System.err.println("Error parsing coordinates from line element: " + lineElement);
                                e.printStackTrace();
                            }
                        }
                    }
                }
                boolean hasLine = false;
                NodeList lineList = gElement.getElementsByTagName("line");
                if (lineList.getLength() > 0) {
                    hasLine = true;
                }

                NodeList pathList = gElement.getElementsByTagName("path");
                for (int j = 0; j < pathList.getLength(); j++) {
                    Element pathElement = (Element) pathList.item(j);
                    String d = pathElement.getAttribute("d");
                    String fill = pathElement.getAttribute("fill");
                    if ("#8B4513".equals(fill) && !hasLine) {
                        try {
                            // Извлечение координат треугольника из атрибута d
                            String[] coords = d.split("[ ,ML]");
                            // Фильтрация пустых строк
                            List<String> filteredCoords = Arrays.stream(coords)
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.toList());
                            double x1 = Double.parseDouble(filteredCoords.get(0));
                            double x2 = Double.parseDouble(filteredCoords.get(2));
                            double x3 = Double.parseDouble(filteredCoords.get(4));

                            // Вычисление длины треугольника по x
                            double triangleLength = Math.abs(x2 - x1);

                            // Поиск элемента <rect> в текущем <g>
                            for (int k = 0; k < rectList.getLength(); k++) {
                                Element rectElement = (Element) rectList.item(k);
                                String rectY = rectElement.getAttribute("y");
                                double rectYValue = Double.parseDouble(rectY);

                                // Нахождение соответствующего диапазона и увеличение счетчика
                                for (int l = 0; l < heightRanges.length; l++) {
                                    int range = heightRanges[l];
                                    if (rectYValue >= range && rectYValue <= range + 39) {
                                        countByRange[l]++;
                                        heightRangesMap.get(range).incrementCargoOperations();
                                        heightRangesMap.get(range).addTotalLengthCargoOperations(triangleLength);
                                        break;
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    if (d.contains("L") && "#FF0000".equals(fill) && !hasLine) {
                        try {
                            // Извлечение координат треугольника из атрибута d
                            String[] coords = d.split("[ ,LM]");
                            // Фильтрация пустых строк
                            List<String> filteredCoords = Arrays.stream(coords)
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.toList());
                            double x1 = Double.parseDouble(filteredCoords.get(0));
                            double x2 = Double.parseDouble(filteredCoords.get(2));
                            double x3 = Double.parseDouble(filteredCoords.get(4));
                            double triangleLength=0;
                            if(x2>x1) {
                                triangleLength= Math.abs(x2 - x1);
                            }
                            else
                                triangleLength=Math.abs(x1-x2);

                            NodeList textList = gElement.getElementsByTagName("text");
                            for (int k = 0; k < textList.getLength(); k++) {
                                Element textElement = (Element) textList.item(k);
                                String y = textElement.getAttribute("y");
                                int textY = Integer.parseInt(y);
                                for (int l = 0; l < heightRanges.length; l++) {
                                    int range = heightRanges[l];
                                    if (textY >= range && textY <= range + 39) {
                                        countByRange[l]++;
                                        heightRangesMap.get(range).incrementNumPassengerTrainArrivalsAndDepartures();
                                        heightRangesMap.get(range).addTotalLengthPassengerTrainArrivalsAndDepartures(triangleLength);
                                        break;
                                    }
                                }
                            }
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.err.println("Error parsing coordinates from path d attribute: " + d);
                            e.printStackTrace();
                        }
                    }

                    //#9ACD32 подставить, так правильно
                    if ("#9ACD32".equals(fill)) {
                        try {
                            // Извлечение координат треугольника из атрибута d
                            String[] coords = d.split("[ ,ML]");
                            // Фильтрация пустых строк
                            List<String> filteredCoords = Arrays.stream(coords)
                                    .filter(s -> !s.isEmpty())
                                    .collect(Collectors.toList());
                            double x1 = Double.parseDouble(filteredCoords.get(0));
                            double x2 = Double.parseDouble(filteredCoords.get(2));
                            double x3 = Double.parseDouble(filteredCoords.get(4));
                            if(x2>x3) {
                                // Вычисление длины треугольника по x
                                double triangleLength = Math.abs(x2 - x1);

                                // Поиск элемента <rect> в текущем <g>
                                for (int k = 0; k < rectList.getLength(); k++) {
                                    Element rectElement = (Element) rectList.item(k);
                                    String rectY = rectElement.getAttribute("y");
                                    double rectYValue = Double.parseDouble(rectY);

                                    // Нахождение соответствующего диапазона и увеличение счетчика
                                    for (int l = 0; l < heightRanges.length; l++) {
                                        int range = heightRanges[l];
                                        if (rectYValue >= range && rectYValue <= range + 39) {
                                            countByRange[l]++;
                                            heightRangesMap.get(range).incrementTrainLocomotiveTrailer();
                                            heightRangesMap.get(range).addTotalLengthTrainLocomotiveTrailer(triangleLength);
                                            break;
                                        }
                                    }
                                }
                            }
                        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                            System.err.println("Error parsing coordinates from path d attribute: " + d);
                            e.printStackTrace();
                        }
                    }
                }
            }





            // Поиск всех элементов <rect> в документе
            NodeList rectList = document.getElementsByTagName("rect");
            for (int i = 0; i < rectList.getLength(); i++) {
                Element rectElement = (Element) rectList.item(i);
                String fill = rectElement.getAttribute("fill");
                String rectY = rectElement.getAttribute("y");
                String rectWidth = rectElement.getAttribute("width");

                double rectYValue = Double.parseDouble(rectY);
                double rectWidthValue = Double.parseDouble(rectWidth);

                switch (fill) {
                    case "#90EE90" -> {
                        // Обработка для fill="#90EE90"
                        for (int j = 0; j < heightRanges.length; j++) {
                            int range = heightRanges[j];
                            if (rectYValue >= range && rectYValue <= range + 39) {
                                countByRange[j]++;
                                heightRangesMap.get(range).incrementLocomotiveArrival();
                                heightRangesMap.get(range).addTotalLengthLocomotiveArrival(rectWidthValue); // Увеличение суммарной длины
                                break;
                            }
                        }
                    }
                    case "#F08080" -> {
                        // Обработка для fill="#F08080"
                        for (int k = 0; k < heightRanges.length; k++) {
                            int range = heightRanges[k];
                            if (rectYValue >= range && rectYValue <= range + 39) {
                                countByRange[k]++;
                                heightRangesMap.get(range).incrementWaitingOfMovement();
                                heightRangesMap.get(range).addTotalLengthWaitingOfMovement(rectWidthValue); // Увеличение суммарной длины
                                break;
                            }
                        }
                    }
                    case "#DEB887" -> {
                        for (int k = 0; k < heightRanges.length; k++) {
                            int range = heightRanges[k];
                            if (rectYValue >= range && rectYValue <= range + 39) {
                                countByRange[k]++;
                                heightRangesMap.get(range).incrementWaitingForThread();
                                heightRangesMap.get(range).addTotalLengthWaitingForThread(rectWidthValue); // Увеличение суммарной длины
                                break;
                            }
                        }
                    }
                    case "#FFD700" -> {
                        for (int k = 0; k < heightRanges.length; k++) {
                            int range = heightRanges[k];
                            if (rectYValue >= range && rectYValue <= range + 39) {
                                countByRange[k]++;
                                heightRangesMap.get(range).incrementWaitingForBrigade();
                                heightRangesMap.get(range).addTotalLengthWaitingForBrigade(rectWidthValue); // Увеличение суммарной длины
                                break;
                            }
                        }
                    }
                }

                if (!"#FFFFFF".equals(fill)) {
                    continue; // Пропускаем текущий <rect>, если fill не равен #FFFFFF
                }

                // Получаем координаты и размеры <rect>
                String outRectX = rectElement.getAttribute("x");
                String outRectY = rectElement.getAttribute("y");
                String rectHeight = rectElement.getAttribute("height");

                double outRectYValue = Double.parseDouble(outRectY);
                double rectHeightValue = Double.parseDouble(rectHeight);

                // Поиск <clipPath> связанного с текущим <rect>
                String rectId = rectElement.getAttribute("id");
                NodeList clipPathList = document.getElementsByTagName("clipPath");
                for (int j = 0; j < clipPathList.getLength(); j++) {
                    Element clipPathElement = (Element) clipPathList.item(j);

                    // Проверяем, что текущий <clipPath> содержит <rect>, связанный с нашим <rect>
                    NodeList innerRectList = clipPathElement.getElementsByTagName("rect");
                    if (innerRectList.getLength() == 0) {
                        continue; // Пропускаем <clipPath>, если в нем нет <rect>
                    }

                    Element innerRectElement = (Element) innerRectList.item(0);
                    String innerRectX = innerRectElement.getAttribute("x");
                    String innerRectY = innerRectElement.getAttribute("y");
                    String innerRectWidth = innerRectElement.getAttribute("width");
                    String innerRectHeight = innerRectElement.getAttribute("height");

                    // Проверяем, что координаты и размеры совпадают
                    if (!outRectX.equals(innerRectX) || !outRectY.equals(innerRectY) ||
                            !rectWidth.equals(innerRectWidth) || !rectHeight.equals(innerRectHeight)) {
                        continue; // Пропускаем текущий <clipPath>, если параметры не совпадают
                    }

                    // Получаем id <clipPath>
                    String clipPathId = clipPathElement.getAttribute("id");

                    // Поиск <path> связанного с текущим <clipPath>
                    NodeList pathList = document.getElementsByTagName("path");
                    for (int k = 0; k < pathList.getLength(); k++) {
                        Element pathElement = (Element) pathList.item(k);
                        String pathFill = pathElement.getAttribute("fill");
                        String pathClipPath = pathElement.getAttribute("clip-path");

                        // Проверяем, что fill у <path> равен "none" и clip-path соответствует текущему <clipPath>
                        if ("none".equals(pathFill) && pathClipPath.equals("url(#" + clipPathId + ")")) {
                            String pathD = pathElement.getAttribute("d");
                            //System.out.println("path d: " + pathD);
                            for (int l = 0; l < heightRanges.length; l++) {
                                int range = heightRanges[l];
                                if (outRectYValue >= range && outRectYValue <= range + 39) {
                                    countByRange[l]++;
                                    heightRangesMap.get(range).incrementDowntime();
                                    heightRangesMap.get(range).addTotalLengthDowntime(Double.parseDouble(innerRectWidth));
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
                System.out.println("Диапазон " + start + " - " + (start + 39) +
                        " имеет название: " + range.getName() +
                        " Количество Приемы и отправления пассажирских поездов: " +
                        range.getNumPassengerTrainArrivalsAndDepartures() +
                        " (Суммарная длина: " + range.getTotalLengthPassengerTrainArrivalsAndDepartures() + ")" +
                        " Заезд поезд.локомотива: " + range.getNumLocomotiveArrival() +
                        " (Суммарная длина: " + range.getTotalLengthLocomotiveArrival() + ")" +
                        " Ожидание движения: " + range.getNumWaitingOfMovement() +
                        " (Суммарная длина: " + range.getTotalLengthWaitingOfMovement() + ")" +
                        " Простой: " + range.getNumDowntime() +
                        " (Суммарная длина: " + range.getTotalLengthDowntime() + ")" +
                        " Ожидание нитки: " + range.getNumWaitingForThread() +
                        " (Суммарная длина: " + range.getTotalLengthWaitingForThread() + ")" +
                        " Ожидание бригады: " + range.getNumWaitingForBrigade() +
                        " (Суммарная длина: " + range.getTotalLengthWaitingForBrigade() + ")" +
                        " Выгрузка: " + range.getNumDischarge() +
                        " (Суммарная длина: " + range.getTotalLengthDischarge() + ")" +
                        " Прицепка маневрового локомотива: " + range.getNumCouplingShuntingLocomotive() +
                        " (Суммарная длина: " + range.getTotalLengthCouplingShuntingLocomotive() + ")" +
                        " Прицепка поезного локомотива: " + range.getNumTrainLocomotiveTrailer() +
                        " (Суммарная длина: " + range.getTotalLengthTrainLocomotiveTrailer() + ")"
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

