package sk.stopangin.validation.excel.evaluator;

public interface StartDataEvaluator<T> {
    boolean isStartData(String currentData, EvaluatorContext<T> rowContext);
}
