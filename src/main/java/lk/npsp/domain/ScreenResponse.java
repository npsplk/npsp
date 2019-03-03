package lk.npsp.domain;

import lk.npsp.domain.enumeration.ScreenLanguage;
import lk.npsp.service.SimpleTranslator;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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
    private SimpleTranslator simpleTranslator= new SimpleTranslator(); //TODO: autowire

    public ScreenResponse(List<ScheduleInstance> scheduleInstanceList,String bayName) throws IOException {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        Date today = new Date();
        this.currentDate = dateFormat.format(today);

        String screenTitle = (bayName.equals(""))? "Departures": bayName + " - Departures";
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

    private void generateTableHeaders() throws IOException {

        List<String> tableHeaders= new ArrayList<>();

        //read headers from config file
        File translatorFile = new ClassPathResource("schedule-screen/table-headers.csv").getFile();
        FileReader translatorFileReader = new FileReader(translatorFile);
        BufferedReader br = new BufferedReader(translatorFileReader);
        String line;
        while ((line = br.readLine()) != null) {
            String[] values = line.split(",");
            tableHeaders= new ArrayList<>(Arrays.asList(values));
        }

        this.tableHeaders.add(tableHeaders);
        this.tableHeaders.add(simpleTranslator.translate(tableHeaders,ScreenLanguage.SINHALA));
        this.tableHeaders.add(simpleTranslator.translate(tableHeaders,ScreenLanguage.TAMIL));
    }
}
