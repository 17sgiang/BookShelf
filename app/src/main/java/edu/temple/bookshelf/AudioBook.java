package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioBook implements Parcelable {
    protected AudioBook(Parcel in) {
    }

    public static final Creator<AudioBook> CREATOR = new Creator<AudioBook>() {
        @Override
        public AudioBook createFromParcel(Parcel in) {
            return new AudioBook(in);
        }

        @Override
        public AudioBook[] newArray(int size) {
            return new AudioBook[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}
