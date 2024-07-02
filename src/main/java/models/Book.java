package models;

public class Book extends Course {

    private String BookName;
    private String BookURL;

    public void setBookName(String bookName) {
        BookName = bookName;
    }
    public String getBookName(){return BookName;}

    public void setBookURL(String bookURL) {BookURL = bookURL;}
    public String getBookURL() {return BookURL;}

}
