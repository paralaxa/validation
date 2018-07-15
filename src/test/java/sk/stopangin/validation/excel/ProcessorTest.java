package sk.stopangin.validation.excel;

import org.junit.Test;
import sk.stopangin.validation.excel.evaluator.MonthEndDataEvaluator;
import sk.stopangin.validation.excel.evaluator.MonthStartDataEvaluator;
import sk.stopangin.validation.excel.evaluator.VacationEndDataEvaluator;
import sk.stopangin.validation.excel.evaluator.VacationStartDataEvaluator;
import sk.stopangin.validation.excel.event.MonthEventListener;
import sk.stopangin.validation.excel.event.VacationEventListener;

import java.util.Arrays;
import java.util.List;

public class ProcessorTest {

    public static final String[] monthData = {"", "", "",
            "January", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
            "February", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28",
            "March", "2", "3", "4", "5", "6", "7", "8", "9", "10",
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
            "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31",
    };

    //first 3 are offset
    public static final String[] vacationData = {
            "","","","", "1", "1", "1", "", "", "", "", "", "",
            "", "", "1", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "",
            "", "1", "", "", "", "", "", "", "", "",
            "", "", "", "", "", "", "", "", "", "",
            "1", "1", "", "", "", "", "", "", "", "", "",
    };

    @Test
    public void testProcessor() {
        Processor<Integer> monthProcessor = new Processor<>(new MonthStartDataEvaluator(), new MonthEndDataEvaluator());
        MonthEventListener monthEventListener = new MonthEventListener();
        monthProcessor.withEventListener(monthEventListener);
        monthProcessor.process(Arrays.asList(monthData));
        List<MonthDto> monthDtos = monthEventListener.getMonthDtos();

        Processor<Boolean> vacationProcessor = new Processor<>(new VacationStartDataEvaluator(), new VacationEndDataEvaluator());
        vacationProcessor.withEventListener(new VacationEventListener(new MonthUtil(monthDtos)));
        vacationProcessor.process(Arrays.asList(vacationData));
    }


}