package models;

import java.util.Comparator;

public class EventTimeComparator implements Comparator<Event> {
    @Override
    public int compare(Event This, Event That) {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(This.getEventDate().substring(7));
        sb1.append(monthNumber(This.getEventDate().substring(3, 6)));
        sb1.append(This.getEventDate().substring(0, 2));

        StringBuilder sb2 = new StringBuilder();
        sb2.append(That.getEventDate().substring(7));
        sb2.append(monthNumber(That.getEventDate().substring(3, 6)));
        sb2.append(That.getEventDate().substring(0, 2));

        int date1 = Integer.parseInt(String.valueOf(sb1));
        int date2 = Integer.parseInt(String.valueOf(sb2));

        if(date1 > date2) return 1;
        else if(date1 < date2) return -1;

        StringBuilder sb3 = new StringBuilder();
        if(This.getStartTime().substring(6).equals("AM")) {
            sb3.append("1");
        }
        else {
            sb3.append("2");
        }
        sb3.append(This.getStartTime().substring(0, 2));
        sb3.append(This.getStartTime().substring(3, 5));

        StringBuilder sb4 = new StringBuilder();
        if(That.getStartTime().substring(6).equals("AM")) {
            sb4.append("1");
        }
        else {
            sb4.append("2");
        }
        sb4.append(That.getStartTime().substring(0, 2));
        sb4.append(That.getStartTime().substring(3, 5));

        int startTime1 = Integer.parseInt(String.valueOf(sb3));
        int startTime2 = Integer.parseInt(String.valueOf(sb4));

        if(startTime1 > startTime2) return 1;
        else if(startTime1 < startTime2) return -1;

        StringBuilder sb5 = new StringBuilder();
        if(This.getEndTime().substring(6).equals("AM")) {
            sb5.append("1");
        }
        else {
            sb5.append("2");
        }
        sb5.append(This.getEndTime().substring(0, 2));
        sb5.append(This.getEndTime().substring(3, 5));

        StringBuilder sb6 = new StringBuilder();
        if(That.getEndTime().substring(6).equals("AM")) {
            sb6.append("1");
        }
        else {
            sb6.append("2");
        }
        sb6.append(That.getEndTime().substring(0, 2));
        sb6.append(That.getEndTime().substring(3, 5));

        int endTime1 = Integer.parseInt(String.valueOf(sb5));
        int endTime2 = Integer.parseInt(String.valueOf(sb6));

        if(endTime1 > endTime2) return 1;
        else if(endTime1 < endTime2) return -1;

        return 0;
    }

    private String monthNumber(String month) {
        String monthNumber = null;
        switch(month) {
            case "Jan":
                monthNumber = "01";
                break;
            case "Feb":
                monthNumber = "02";
                break;
            case "Mar":
                monthNumber = "03";
                break;
            case "Apr":
                monthNumber = "04";
                break;
            case "May":
                monthNumber = "05";
                break;
            case "Jun":
                monthNumber = "06";
                break;
            case "Jul":
                monthNumber = "07";
                break;
            case "Aug":
                monthNumber = "08";
                break;
            case "Sep":
                monthNumber = "09";
                break;
            case "Oct":
                monthNumber = "10";
                break;
            case "Nov":
                monthNumber = "11";
                break;
            case "Dec":
                monthNumber = "12";
                break;
        }
        return monthNumber;
    }
}
