package sk.stopangin.validation.excel.event;

import org.apache.commons.lang3.Range;
import sk.stopangin.validation.excel.MonthDto;
import sk.stopangin.validation.excel.Processor;

import java.util.ArrayList;
import java.util.List;

public class MonthEventListener implements EventListener {
    public static final int DECEMBER_OFFSET = 30;
    private int start;
    private int end;
    private String monthName;

    private List<String> data = new ArrayList<>();

    private List<MonthDto> monthDtos = new ArrayList<>();

    public List<MonthDto> getMonthDtos() {
        return monthDtos;
    }

    public void fireEvent(Event event) {
        List<Object> context = Processor.getContext();//todo update, as i don't wana connection between listener and processor
        if (event instanceof StartEvent) {
            data = new ArrayList<>();
            start = ((StartEvent) event).getStart();
            monthName = event.getPayload();
        } else if (event instanceof EndEvent) {
            end = ((EndEvent) event).getEnd();
            monthDtos.add(new MonthDto(monthName, Range.between(start, end), data));
        } else if (event instanceof FinishEvent) {
            end = ((FinishEvent) event).getEnd();
            monthDtos.add(new MonthDto(monthName, Range.between(start, end + DECEMBER_OFFSET), data));
            for (MonthDto monthDto : monthDtos) {
                System.out.println(monthDto);
            }
        } else data.add(event.getPayload());
    }
}
