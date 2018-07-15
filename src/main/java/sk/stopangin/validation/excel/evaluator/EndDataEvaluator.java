package sk.stopangin.validation.excel.evaluator;

public interface EndDataEvaluator<T> {
    boolean isEndData(String currentData, EvaluatorContext<T> rowContext);
}
