package sk.stopangin.validation.excel.evaluator;

import org.apache.logging.log4j.util.Strings;

public class MonthStartDataEvaluator implements StartDataEvaluator<Integer> {
    @Override
    public boolean isStartData(String currentData, EvaluatorContext<Integer> rowContext) {
        return currentData != null && currentData.length() > 2;//!Strings.isBlank(currentData);
    }
}
