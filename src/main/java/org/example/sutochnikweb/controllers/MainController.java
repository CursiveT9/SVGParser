package org.example.sutochnikweb.controllers;

import org.apache.poi.ss.usermodel.Workbook;
import org.example.sutochnikweb.models.AccumulationLastAndDescentFields;
import org.example.sutochnikweb.models.Action;
import org.example.sutochnikweb.models.HeightRange;
import org.example.sutochnikweb.models.TrainStatistics;
import org.example.sutochnikweb.services.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private String originalFileName;
    private static final String UPLOAD_DIR = "uploads/";
    private final SimpleExcelService excelService;
    private final SVGService svgService;
    private final TimeService timeService;
    private final TransitTrainsService transitTrainsService;
    private final TrainsWithOvertimeService trainsWithOvertimeService;

    public final DepartureService departureService;

    private final TrainStatisticsService trainStatisticsService;

    private final AccumulationDescentService accumulationDescentService;

    private final FullOvertimeStatisticService fullOvertimeStatisticService;

    private byte[] excelBytes;


    public MainController(SimpleExcelService excelService, SVGService svgService, TimeService timeService, TransitTrainsService transitTrainsService, TrainsWithOvertimeService trainsWithOvertimeService, TrainStatisticsService trainStatisticsService, AccumulationDescentService accumulationDescentService, DepartureService departureService, FullOvertimeStatisticService fullOvertimeStatisticService) {
        this.excelService = excelService;
        this.svgService = svgService;
        this.timeService = timeService;
        this.transitTrainsService = transitTrainsService;
        this.trainsWithOvertimeService = trainsWithOvertimeService;
        this.trainStatisticsService = trainStatisticsService;
        this.accumulationDescentService = accumulationDescentService;
        this.departureService = departureService;
        this.fullOvertimeStatisticService = fullOvertimeStatisticService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("message", "Пожалуйста, выберите файл для загрузки");
            return "upload";
        }

        try {
            Files.createDirectories(Paths.get(UPLOAD_DIR));

            Path tempFilePath = Files.createTempFile("temp", file.getOriginalFilename());
            Files.write(tempFilePath, file.getBytes());

            File tempFile = tempFilePath.toFile();
            originalFileName = file.getOriginalFilename();  // Сохраняем имя загружаемого файла

            Map<String, HeightRange> map = svgService.parseSvg(tempFile);
            Map<String, List<List<Action>>> transitTrainsMap = transitTrainsService.findTransitTrains(map);
            Map<String, List<List<Action>>> overtimeTrainsMap = trainsWithOvertimeService.findTrainsWithOvertime(map);
            AccumulationLastAndDescentFields accumulationDescentTrains = accumulationDescentService.findAverageAccumulationDuration(map);
            AccumulationLastAndDescentFields endAccumulationDescentTrains = accumulationDescentService.findEndAccumulationSequences(map);
            accumulationDescentTrains.setCount(accumulationDescentTrains.getCount()+endAccumulationDescentTrains.getCount());
            accumulationDescentTrains.setAvgDuration((accumulationDescentTrains.getAvgDuration()+endAccumulationDescentTrains.getAvgDuration())/2);
            Map<String, List<List<Action>>> departureTrainsMap = departureService.findFormationOrShuntingPairs(map);

            Workbook excelFile = excelService.convertToExcel(map);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            excelFile.write(bos);
            excelBytes = bos.toByteArray();
            TrainStatistics transitTrainsStatistic=trainStatisticsService.calculateTrainStatistics(transitTrainsMap, timeService);
            TrainStatistics overtimeTrainsStatistic=trainStatisticsService.calculateTrainStatistics(overtimeTrainsMap, timeService);
            TrainStatistics departureTrainsStatistic=trainStatisticsService.calculateTrainStatistics(departureTrainsMap, timeService);
            TrainStatistics fullOvertimeTrainsStatistic = fullOvertimeStatisticService.sumPartsOfOvertimeTrains
                    (overtimeTrainsStatistic,
                    departureTrainsStatistic,
                    accumulationDescentTrains);

            model.addAttribute("transitTrainsStatistic", transitTrainsStatistic);
            model.addAttribute("arrivalTrainsStatistic", overtimeTrainsStatistic);
            model.addAttribute("departureTrainsStatistic", departureTrainsStatistic);
            model.addAttribute("fullOvertimeTrainsStatistic", fullOvertimeTrainsStatistic);

            model.addAttribute("accumulationDescentTrains", accumulationDescentTrains);
            model.addAttribute("stringAccumulationDescentTrainsAvgTime", timeService.convertMillisToTime(accumulationDescentTrains.getAvgDuration()));

//Тесты
            //model.addAttribute("departureTrains", departureTrainsMap);
            //Map<String, List<String>> pairsTimeMap = departureService.getPairsDurations(departureTrainsMap);
            //model.addAttribute("departureTrains", pairsTimeMap);

            return "preview";

        } catch (Exception e) {
            model.addAttribute("message", "Произошла ошибка при загрузке файла. Убедитесь, что загружаете суточный план-график, который не был в стороннем редакторе");
            return "upload";
        }
    }
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadFile() {
        if (excelBytes == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(excelBytes);
        HttpHeaders headers = new HttpHeaders();
        //Нужна кодировка, чтобы не было ошибки, когда имя файла на кириллице
        String downloadFileName = originalFileName != null
                ? originalFileName.replaceFirst("[.][^.]+$", "") + ".xlsx"
                : "generated_excel.xlsx";

        // Кодируем имя файла в формате RFC 5987
        String encodedFileName = URLEncoder.encode(downloadFileName, StandardCharsets.UTF_8)
                .replace("+", "%20"); // Заменяем "+" на "%20" для пробелов

        headers.add("Content-Disposition", "attachment; filename*=UTF-8''" + encodedFileName);

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}