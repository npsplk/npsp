package lk.npsp.domain;

import lk.npsp.domain.enumeration.ScreenLanguage;
import lk.npsp.service.SimpleTranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ScreenResponse {
    private static final int MAX_ROW_LIMIT = 5;

    private String currentDate;
    private List<String> screenTitle;
    private List<ScreenRow> screenRows = new ArrayList<>();
    private List<List<String>> tableHeaders = new ArrayList<>();

    public ScreenResponse(List<ScheduleInstance> scheduleInstanceList) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        Date today = new Date();
        this.currentDate = dateFormat.format(today);

        SimpleTranslator simpleTranslator = new SimpleTranslator(); //TODO: autowire

        String screenTitle = "Bay 01 - Departures";
        this.screenTitle = new ArrayList<>(Arrays.asList(
            screenTitle,
            simpleTranslator.translate(screenTitle, ScreenLanguage.SINHALA),
            simpleTranslator.translate(screenTitle, ScreenLanguage.TAMIL)
        ));

        generateTableHeaders();

        int list_limit = scheduleInstanceList.size() < MAX_ROW_LIMIT ? scheduleInstanceList.size() : MAX_ROW_LIMIT;

        for (int i = 0; i < list_limit; i++) {
            ScheduleInstance scheduleInstance = scheduleInstanceList.get(i);
            ScreenRow screenRow = new ScreenRow(scheduleInstance);

            this.screenRows.add(screenRow);
        }

    }

    public String getCurrentDate() {
        return this.currentDate;
    }

    public List<String> getScreenTitle() {
        return this.screenTitle;
    }

    public List<ScreenRow> getScreenRows() {
        return this.screenRows;
    }

    public List<List<String>> getTableHeaders() {
        return this.tableHeaders;
    }

    private void generateTableHeaders() {
        ArrayList<String> tableHeadersEnglish = new ArrayList<>(Arrays.asList(
            "TIME", "DESTINATION", "ROUTE", "STATUS", "REMARKS"
        ));
        ArrayList<String> tableHeadersSinhala = new ArrayList<>(Arrays.asList(
            "වේලාව", "ගමනාන්තය", "මාර්ග අංකය", "තත්ත්වය", "විශේෂ"
        ));
        ArrayList<String> tableHeadersTamil = new ArrayList<>(Arrays.asList(
            "நேரம்", "இலக்கு", "தடத்தை", "நிலை", "சிறப்பு"
        ));
        this.tableHeaders.add(tableHeadersEnglish);
        this.tableHeaders.add(tableHeadersSinhala);
        this.tableHeaders.add(tableHeadersTamil);
    }
}
