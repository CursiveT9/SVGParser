package org.example.sutochnikweb.controllers;

import org.apache.poi.ss.usermodel.Workbook;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    private static final String UPLOAD_DIR = "uploads/";
    private final SimpleExcelService excelService;
    private final SVGService svgService;
    private final TimeService timeService;
    private final TransitTrainsService transitTrainsService;
    private final TrainsWithOvertimeService trainsWithOvertimeService;

    private final TrainStatisticsService trainStatisticsService;

    private byte[] excelBytes;


    public MainController(SimpleExcelService excelService, SVGService svgService, TimeService timeService, TransitTrainsService transitTrainsService, TrainsWithOvertimeService trainsWithOvertimeService, TrainStatisticsService trainStatisticsService) {
        this.excelService = excelService;
        this.svgService = svgService;
        this.timeService = timeService;
        this.transitTrainsService = transitTrainsService;
        this.trainsWithOvertimeService = trainsWithOvertimeService;
        this.trainStatisticsService = trainStatisticsService;
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

            Map<String, HeightRange> map = svgService.parseSvg(tempFile);
            Map<String, List<List<Action>>> transitTrainsMap = transitTrainsService.findTransitTrains(map);
            Map<String, List<List<Action>>> overtimeTrainsMap = trainsWithOvertimeService.findTrainsWithOvertime(map);

            Workbook excelFile = excelService.convertToExcel(map);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            excelFile.write(bos);
            excelBytes = bos.toByteArray();
            TrainStatistics transitTrainsStatistics=trainStatisticsService.calculateTrainStatistics(transitTrainsMap, timeService);
            TrainStatistics overtimeTrainsStatistic=trainStatisticsService.calculateTrainStatistics(overtimeTrainsMap, timeService);

            model.addAttribute("totalTransitTrains", transitTrainsStatistics.getTotalTrains());
            model.addAttribute("avgTransitDuration", transitTrainsStatistics.getAvgDuration());
            model.addAttribute("avgTransitWaitingDuration", transitTrainsStatistics.getAvgWaitingDuration());
            model.addAttribute("avgTransitEffectiveDuration", transitTrainsStatistics.getAvgEffectiveDuration());

            model.addAttribute("totalOvertimeTrains", overtimeTrainsStatistic.getTotalTrains());
            model.addAttribute("avgOvertimeDuration", overtimeTrainsStatistic.getAvgDuration());
            model.addAttribute("avgOvertimeWaitingDuration", overtimeTrainsStatistic.getAvgWaitingDuration());
            model.addAttribute("avgOvertimeEffectiveDuration", overtimeTrainsStatistic.getAvgEffectiveDuration());

            return "preview";

        } catch (Exception e) {
            model.addAttribute("message", "Произошла ошибка при загрузке файл. Убедитесь, что загружаете суточный план-график, который не был в стороннем редакторе");
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
        headers.add("Content-Disposition", "attachment; filename=generated_excel.xlsx");

        return ResponseEntity.ok().headers(headers).body(excelBytes);
    }
}