package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    private int id;
    private String title;
    private String author;
    private String coverURL;    // Book cover image URL

    public Book(int id, String title, String author, String coverURL){
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverURL = coverURL;
    }

    protected Book(Parcel in) {
        title = in.readString();
        author = in.readString();
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(author);
    }

    // Method to return a formatted String containing title and author?

    // Getters

    public String getAuthor(){
        return author;
    }

    public String getTitle(){
        return title;
    }


}
