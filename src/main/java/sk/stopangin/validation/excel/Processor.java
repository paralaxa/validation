package sk.stopangin.validation.excel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.stopangin.validation.excel.evaluator.EndDataEvaluator;
import sk.stopangin.validation.excel.evaluator.EvaluatorContext;
import sk.stopangin.validation.excel.evaluator.StartDataEvaluator;
import sk.stopangin.validation.excel.event.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Processor<T> {
    private StartDataEvaluator<T> startDataEvaluator;
    private EndDataEvaluator<T> endDataEvaluator;
    private Set<EventListener> eventListeners = new HashSet<>();
    private static final Logger log = LoggerFactory.getLogger(Processor.class);
    private static ThreadLocal<List<Object>> context = new InheritableThreadLocal<>();

    public static List<Object> getContext() {
        return context.get();
    }

    public Processor(StartDataEvaluator<T> startDataEvaluator, EndDataEvaluator<T> endDataEvaluator) {
        this.startDataEvaluator = startDataEvaluator;
        this.endDataEvaluator = endDataEvaluator;
    }

    public Processor withEventListener(EventListener eventListener) {
        eventListeners.add(eventListener);
        return this;
    }

    public void process(List<Object> rowData) {
        EvaluatorContext<T> evaluatorContext = new EvaluatorContext<>(null);
        context.set(rowData);//todo rowContextCreator with currentData passed in (ie for name from first 2 cells)
        boolean started = false;
        for (int i = 0; i < rowData.size(); i++) { //not using foreach, as i need iter
            String currentData = (String) rowData.get(i);
            if (started && endDataEvaluator.isEndData(currentData, evaluatorContext)) {
                started = false;
                fireEndEvent(i - 1);
            }
            if (!started && startDataEvaluator.isStartData(currentData, evaluatorContext)) {
                started = true;
                fireStartEvent(i, currentData);
            }
            fireEvent(new Event(currentData));
        }
        int end = Math.max(rowData.size() - 1, 0);

        fireEvent(new FinishEvent(end));
    }

    private void fireStartEvent(int iter, String value) {
        log.info("Start event fired for iter: {} with value: {}.", iter, value);
        StartEvent startEvent = new StartEvent(iter);
        startEvent.setPayload(value);
        fireEvent(startEvent);
    }

    private void fireEndEvent(int iter) {
        log.info("End event fired for iter: {}.", iter);
        EndEvent endEvent = new EndEvent(iter);
        fireEvent(endEvent);
    }

    private void fireEvent(Event event) {
        for (EventListener eventListener : eventListeners) {
            eventListener.fireEvent(event);
        }
    }
}



