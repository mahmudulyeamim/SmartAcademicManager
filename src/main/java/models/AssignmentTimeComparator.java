package models;

import java.util.Comparator;

public class AssignmentTimeComparator implements Comparator<Assignment> {
    @Override
    public int compare(Assignment This, Assignment That) {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(This.getDueDate().substring(7));
        sb1.append(monthNumber(This.getDueDate().substring(3, 6)));
        sb1.append(This.getDueDate().substring(0, 2));

        StringBuilder sb2 = new StringBuilder();
        sb2.append(That.getDueDate().substring(7));
        sb2.append(monthNumber(That.getDueDate().substring(3, 6)));
        sb2.append(That.getDueDate().substring(0, 2));

        int date1 = Integer.parseInt(String.valueOf(sb1));
        int date2 = Integer.parseInt(String.valueOf(sb2));

        if(date1 > date2) return 1;
        else if(date1 < date2) return -1;

        StringBuilder sb3 = new StringBuilder();
        if(This.getDueTime().substring(6).equals("AM")) {
            sb3.append("1");
        }
        else {
            sb3.append("2");
        }
        sb3.append(This.getDueTime().substring(0, 2));
        sb3.append(This.getDueTime().substring(3, 5));

        StringBuilder sb4 = new StringBuilder();
        if(That.getDueTime().substring(6).equals("AM")) {
            sb4.append("1");
        }
        else {
            sb4.append("2");
        }
        sb4.append(That.getDueTime().substring(0, 2));
        sb4.append(That.getDueTime().substring(3, 5));

        int dueTime1 = Integer.parseInt(String.valueOf(sb3));
        int dueTime2 = Integer.parseInt(String.valueOf(sb4));

        if(dueTime1 > dueTime2) return 1;
        else if(dueTime1 < dueTime2) return -1;

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
