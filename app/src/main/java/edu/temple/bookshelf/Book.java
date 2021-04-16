package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

public class Book implements Parcelable {

    private int id;
    private String title;
    private String author;
    private String coverUrl;    // Book cover image URL
    private int duration;

    public Book(int id, String title, String author, String coverUrl, int duration){
        this.id = id;
        this.title = title;
        this.author = author;
        this.coverUrl = coverUrl;
        this.duration = duration;
    }

    protected Book(Parcel in) {
        id = in.readInt();
        title = in.readString();
        author = in.readString();
        coverUrl = in.readString();
        duration = in.readInt();
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
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(coverUrl);
        dest.writeInt(duration);
    }

    // Method to return a formatted String containing title and author?

    // Getters

    public int getId() { return id; }

    public String getAuthor(){
        return author;
    }

    public String getTitle(){
        return title;
    }

    public String getCoverURL() {
        return coverUrl;
    }

    public int getDuration() { return duration;}

}
