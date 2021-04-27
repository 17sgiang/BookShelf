package edu.temple.bookshelf;

import android.os.Parcel;
import android.os.Parcelable;

public class AudioBookList implements Parcelable {
    protected AudioBookList(Parcel in) {
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AudioBookList> CREATOR = new Creator<AudioBookList>() {
        @Override
        public AudioBookList createFromParcel(Parcel in) {
            return new AudioBookList(in);
        }

        @Override
        public AudioBookList[] newArray(int size) {
            return new AudioBookList[size];
        }
    };
}
