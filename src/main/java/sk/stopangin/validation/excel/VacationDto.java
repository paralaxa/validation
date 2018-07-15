package sk.stopangin.validation.excel;

import java.io.Serializable;
import java.util.Date;

public class VacationDto implements Serializable{
    private String name;
    private Date from;
    private Date to;

    public VacationDto() {
    }

    public VacationDto(String name, Date from, Date to) {
        this.name = name;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "VacationDto{" +
                "name='" + name + '\'' +
                ", from=" + from +
                ", to=" + to +
                '}';
    }
}
