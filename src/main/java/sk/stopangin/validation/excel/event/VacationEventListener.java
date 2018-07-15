package sk.stopangin.validation.excel.event;

import sk.stopangin.validation.excel.MonthUtil;
import sk.stopangin.validation.excel.VacationDto;

import java.util.ArrayList;
import java.util.List;

public class VacationEventListener implements EventListener {
    private int start;
    private int end;
    private String monthName;

    private List<String> data = new ArrayList<>();

    private List<VacationDto> vacationDtos = new ArrayList<>();

    private MonthUtil monthUtil;



    public VacationEventListener(MonthUtil monthUtil) {
        this.monthUtil = monthUtil;
    }

    public void fireEvent(Event event) {
        if (event instanceof StartEvent) {
            data = new ArrayList<>();
            start = ((StartEvent) event).getStart();
            monthName = event.getPayload();
        } else if (event instanceof EndEvent) {
            end = ((EndEvent) event).getEnd();
            vacationDtos.add(new VacationDto("some name from ThLocalcontext", monthUtil.monthById(start), monthUtil.monthById(end)));
        } else if (event instanceof FinishEvent) {
            end = ((FinishEvent) event).getEnd();
            vacationDtos.add(new VacationDto("some name from ThLocalcontext", monthUtil.monthById(start), monthUtil.monthById(end)));
            for (VacationDto vacationDto : vacationDtos) {
                System.out.println(vacationDto);
            }
        } else data.add(event.getPayload());
    }
}
