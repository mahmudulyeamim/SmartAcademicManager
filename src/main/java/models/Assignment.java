package models;

public class Assignment extends ToDoList {
    private String description;
    private String dueTime;
    private String dueDate;

    public String getDescription() {
        return description;
    }

    public String getDueTime() {
        return dueTime;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDueTime(String dueTime) {
        this.dueTime = dueTime;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }
}
