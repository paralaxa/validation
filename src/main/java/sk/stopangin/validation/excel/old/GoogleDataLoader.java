package sk.stopangin.validation.excel.old;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.stopangin.validation.excel.MonthDto;
import sk.stopangin.validation.excel.MonthUtil;
import sk.stopangin.validation.excel.Processor;
import sk.stopangin.validation.excel.evaluator.MonthEndDataEvaluator;
import sk.stopangin.validation.excel.evaluator.MonthStartDataEvaluator;
import sk.stopangin.validation.excel.evaluator.VacationEndDataEvaluator;
import sk.stopangin.validation.excel.evaluator.VacationStartDataEvaluator;
import sk.stopangin.validation.excel.event.MonthEventListener;
import sk.stopangin.validation.excel.event.VacationEventListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class GoogleDataLoader {
    private static final String APPLICATION_NAME = "Google Sheets API Java Quickstart";
    private static final JacksonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String CREDENTIALS_FOLDER = "src/main/resources/credentials";

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String CLIENT_SECRET_DIR = "/client_secret.json";

    private static final Logger log = LoggerFactory.getLogger(GoogleDataLoader.class);
    public static final String CONFIG_PROPERTIES = "config.properties";

    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws IOException {

        InputStream in = GoogleDataLoader.class.getResourceAsStream(CLIENT_SECRET_DIR);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(CREDENTIALS_FOLDER)))
                .setAccessType("offline")
                .build();
        return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
    }


    public List<MemberCreateDto> loadData() {
        Properties prop = null;
        try {
            prop = PropertyUtil.loadProperties(CONFIG_PROPERTIES);
        } catch (IOException e) {
            throw new RuntimeException("Error while loading properties", e);
        }

        //TODO properties row from datasheet

        String position = prop.getProperty("position");
        String name = prop.getProperty("name");
        String billingValue = prop.getProperty("billingValue");
        String focus = prop.getProperty("focus");
        String sheetId = prop.getProperty("spreadsheetId");

        final String monthRange = "D1:ND1";
        final String daysRange = "D2:ND2";
        final String vacationRange = "D3:ND56"; //todo next rows as well


        List<MemberCreateDto> memberCreateDtos = new ArrayList<>();
        try {
            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
            final String spreadsheetId = "1kD1UO68ceFnMnVU0u_TBkLf32k9BdSAqLu-3_PawDxk";

            Sheets service = new Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            ValueRange monthRow = service.spreadsheets().values()
                    .get(spreadsheetId, monthRange)
                    .execute();

            List<Object> days = service.spreadsheets().values()
                    .get(spreadsheetId, daysRange)
                    .execute().getValues().get(0);


            List<List<Object>> listVacations = service.spreadsheets().values()
                    .get(spreadsheetId, vacationRange)
                    .execute().getValues();


            Processor<Integer> monthProcessor = new Processor<>(new MonthStartDataEvaluator(), new MonthEndDataEvaluator());
            MonthEventListener monthEventListener = new MonthEventListener();
            monthProcessor.withEventListener(monthEventListener);
            monthProcessor.process(monthRow.getValues().get(0));
            List<MonthDto> monthDtos = monthEventListener.getMonthDtos();

            MonthUtil mu = new MonthUtil(monthDtos);
            mu.setDays(days);

            Processor<Boolean> vacationProcessor = new Processor<>(new VacationStartDataEvaluator(), new VacationEndDataEvaluator());
            VacationEventListener vacationEventListener = new VacationEventListener(mu);
            vacationProcessor.withEventListener(vacationEventListener);

            int iter=0;
            for (List<Object> vacation : listVacations) {
                for (Object o : vacation) {
                    System.out.println(o);
                }
                vacationProcessor.process(vacation);
            }


        } catch (Exception e) {
            throw new RuntimeException("Error while processing data.", e);
        }

        return memberCreateDtos;
    }

    public static void main(String[] args) {
        GoogleDataLoader googleDataLoader = new GoogleDataLoader();
        googleDataLoader.loadData();
    }
}
