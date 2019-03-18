package lk.npsp.domain;

import lk.npsp.domain.enumeration.ScreenLanguage;
import lk.npsp.service.ResourceLocator;
import lk.npsp.service.SimpleTranslator;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ScreenResponse {
    private static final int MAX_ROW_LIMIT_FOR_BAY = 5;
    private static final int MAX_ROW_LIMIT_FOR_SUMMARY = 10;

    private String currentDate;
    private List<String> screenTitle;
    private List<ScreenRow> screenRows = new ArrayList<>();
    private List<List<String>> tableHeaders = new ArrayList<>();
    private SimpleTranslator simpleTranslator;
    private ResourceLocator resourceLocator;

    public ScreenResponse(List<ScheduleInstance> scheduleInstanceList, String bayName,
                          SimpleTranslator simpleTranslator, ResourceLocator resourceLocator) throws IOException {
        this.simpleTranslator = simpleTranslator;
        this.resourceLocator = resourceLocator;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd h:mm a");
        Date today = new Date();
        this.currentDate = dateFormat.format(today);

        String screenTitle = (bayName.equals("")) ? "Departures" : bayName + " - Departures";

        this.screenTitle = new ArrayList<>(Arrays.asList(
            screenTitle,
            simpleTranslator.translate(screenTitle, ScreenLanguage.SINHALA),
            simpleTranslator.translate(screenTitle, ScreenLanguage.TAMIL)
        ));

        generateTableHeaders();
        int listLimit;
        int listSize = scheduleInstanceList.size();
        if (bayName.equals("")) {
            listLimit = listSize > MAX_ROW_LIMIT_FOR_SUMMARY ? MAX_ROW_LIMIT_FOR_SUMMARY : listSize;
        } else {
            listLimit = listSize > MAX_ROW_LIMIT_FOR_BAY ? MAX_ROW_LIMIT_FOR_BAY : listSize;
        }

        for (int i = 0; i < listLimit; i++) {
            ScheduleInstance scheduleInstance = scheduleInstanceList.get(i);
            ScreenRow screenRow = new ScreenRow(scheduleInstance, simpleTranslator);

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

        List<String> tableHeaders = resourceLocator.locateResource(
            "schedule-screen/table-headers.csv", ",").get(0);

        this.tableHeaders.add(tableHeaders);
        this.tableHeaders.add(simpleTranslator.translate(tableHeaders, ScreenLanguage.SINHALA));
        this.tableHeaders.add(simpleTranslator.translate(tableHeaders, ScreenLanguage.TAMIL));
    }
}
