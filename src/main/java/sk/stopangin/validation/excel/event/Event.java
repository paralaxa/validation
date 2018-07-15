package sk.stopangin.validation.excel.event;

public class Event {
    private String payload;

    public Event(String payload) {
        this.payload = payload;
    }

    public Event() {
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Event{" +
                "payload='" + payload + '\'' +
                '}';
    }
}
