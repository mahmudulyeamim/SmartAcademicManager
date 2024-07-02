package controllers;

import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import models.Event;

public class EventsCardController {
    @FXML
    private Text EventName;

    @FXML
    private Text EventTime;

    @FXML
    private Text EventType;

    @FXML
    private Text EventsDate;

    @FXML
    private AnchorPane sideCover;

    @FXML
    private ImageView verticalThreeDots;

    private final String[] startColors = {"#A70E7D", "#3F666D", "#235B00", "#4C17A4", "#900C3F"};
    private final String[] endColors = {"#C83EA2", "#568B95", "#46871E", "#6C3DBD", "#C70039"};


    public ImageView setData(Event event, int colorPicker) {
        EventName.setText(event.getEventName());
        EventType.setText(event.getEventType());

        String date = event.getEventDate();
        StringBuilder s = new StringBuilder();
        for(int i = 0; i < 2; i++) {
            s.append(date.charAt(i));
        }
        s.append(' ');
        for(int i = 3; i < 6; i++) {
            s.append(date.charAt(i));
        }
        EventsDate.setText(String.valueOf(s));

        EventTime.setText("•" + event.getStartTime() + " - " + event.getEndTime());

        // sideCover.setStyle("-fx-background-color: " + startColors[colorPicker]);
        sideCover.setStyle("-fx-background-color: linear-gradient(to top left, " + startColors[4 - colorPicker] + ", " + endColors[4 - colorPicker] + ")");
        return verticalThreeDots;
    }
}
