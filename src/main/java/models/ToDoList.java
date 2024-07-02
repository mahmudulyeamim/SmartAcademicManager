package models;

public class ToDoList implements ListItem {
    private String itemTitle;
    private boolean status;
    public ToDoList() {
        status = false;
    }

    @Override
    public boolean getStatus() {
        return status;
    }

    @Override
    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String getItemTitle() {
        return this.itemTitle;
    }

    @Override
    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }
}
