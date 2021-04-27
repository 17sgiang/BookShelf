package edu.temple.bookshelf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.prefs.Preferences;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity
        extends AppCompatActivity
        implements BookListFragment.BookListFragmentInterface ,
        ControlFragment.ControlFragmentInterface {

    FragmentManager fm;
    ControlFragment controlFragment;
    BookDetailsFragment bookDetailsFragment;
    BookListFragment bookListFragment;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Handler progressHandler;
    AudiobookService.MediaControlBinder mediaControlBinder;
    boolean isConnected;

    // IntentService for threads
    boolean twoPane;
    Book selectedBook, playingBook;
    BookList bookList;
    Button searchButton;

    SeekBar seekBar;
    TextView nowPlayingTextView;
    Intent serviceIntent;
    ServiceConnection serviceConnection;
    int seekProgress;

    private final String TAG_BOOKLIST = "booklist", TAG_BOOKDETAILS = "bookdetails", TAG_CONTROL = "control";
    private final String KEY_SELECTED_BOOK = "selectedBook";
    private final String KEY_BOOKLIST = "booksHere";
    private final String KEY_PROGRESS = "progress";
    private final String KEY_PLAYING_BOOK = "playingBook";
    public static final int SEARCH_REQUEST_CODE = 12434;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new Gson();
        preferences = getPreferences(MODE_PRIVATE);
        editor = preferences.edit();
        bookList = gson.fromJson(preferences.getString(KEY_BOOKLIST, null), BookList.class);
        selectedBook = gson.fromJson(preferences.getString(KEY_SELECTED_BOOK, null), Book.class);
        playingBook = gson.fromJson(preferences.getString(KEY_PLAYING_BOOK, null), Book.class);
        seekProgress = preferences.getInt(KEY_PROGRESS, 0);

        // Fetch selected book if there was one
        if(savedInstanceState != null){
            // Fetch selected book if there was one
            selectedBook = savedInstanceState.getParcelable(KEY_SELECTED_BOOK);
            // Fetch previously searched books if one was previously retrieved
            bookList = savedInstanceState.getParcelable(KEY_BOOKLIST);
            seekProgress = savedInstanceState.getInt(KEY_PROGRESS);
        } else {
            bookList = new BookList();
        }

        twoPane = findViewById(R.id.container_2) != null;
        fm = getSupportFragmentManager();

        // IBinder is an interface, describes the interface of the service.
        // Connect by calling bindService()
        progressHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                if(seekBar != null && msg.obj != null) {
                    seekBar.setProgress(((AudiobookService.BookProgress)msg.obj).getProgress());
                }
                return true;
                // If done with message after this, then return true
            }
        });

        serviceConnection = new ServiceConnection() {

            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {
                mediaControlBinder = (AudiobookService.MediaControlBinder) binder;
                mediaControlBinder.setProgressHandler(progressHandler);
                isConnected = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                isConnected = false;
            }
        };

        serviceIntent = new Intent(MainActivity.this, AudiobookService.class);
//        serviceIntent.putExtra();
        bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);

        controlFragment = ControlFragment.newInstance();
        fm.beginTransaction()
                .add(R.id.control_container, controlFragment, TAG_CONTROL)
                .commit();

        // controlFragment's onCreateView function hasn't been called yet here
        // Because it's asynchronous, but we need to update various information.


        Fragment fragment1 = fm.findFragmentById(R.id.container_1);
        // Fragment fragment1 = fm.findFragmentByTag();     // Circumvents timing issues

        if(fragment1 instanceof BookDetailsFragment){
            fm.popBackStack();
        } else if (!(fragment1 instanceof BookListFragment)){
            // If bookList hasn't been initiated then can't call this
            bookListFragment = BookListFragment.newInstance(bookList);
            fm.beginTransaction()
                    .replace(R.id.container_1, bookListFragment, TAG_BOOKLIST)
                    .commit();
        }

        // If two containers available, load single instance of BookDetailsFragment to display all selected books
        bookDetailsFragment = (selectedBook == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(selectedBook);

        if(twoPane){
            fm.beginTransaction()
                    .replace(R.id.container_2, bookDetailsFragment, TAG_BOOKDETAILS)
                    .commit();
        } else if (selectedBook != null){
            // If a book was selected and we have a single container
            // Replace BookListFragment with BookDetailsFragment, making the transaction reversible
            fm.beginTransaction()
                    .replace(R.id.container_1, bookDetailsFragment, TAG_BOOKDETAILS)
                    .addToBackStack(null)
                    .commit();
        }

        // using .add instead of .replace makes the fragments persist upon rotation, and aren't cleaned up.
        // inefficient, creates a fragment that was already there.
        // Find a solution that doesn't require recreation of the fragment
        searchButton = findViewById(R.id.search_open_button);
        searchButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // Create Intent
                Intent intent = new Intent(MainActivity.this, BookSearchActivity.class);
                // Putting data into the intent
                // Starting Activity
                startActivityForResult(intent, SEARCH_REQUEST_CODE);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SEARCH_REQUEST_CODE && resultCode == RESULT_OK){

            bookList.clear();
            bookList.addAll((BookList) data.getParcelableExtra(BookSearchActivity.BOOKLIST_KEY));

            if(bookList.size() == 0){
                Toast.makeText(this, "No books matched your search", Toast.LENGTH_SHORT).show();
            }
            // showNewBooks() notifies the BookListFragment of the data set changing
            showNewBooks();

        }
    }

    // MainActivity should check the layout through presence of containers
    // Depending on check, different actions should be taken when items are clicked
    // If mobile layout, should replace with BookDetails Fragment to the BackStack

    @Override
    public void onResume() {
        super.onResume();
        if(selectedBook != null) {
            seekBar.setMax(selectedBook.getDuration());

        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Gson gson = new Gson();
        editor.putString(KEY_BOOKLIST, gson.toJson(bookList));
        editor.putString(KEY_PLAYING_BOOK, gson.toJson(playingBook));
        editor.putString(KEY_SELECTED_BOOK, gson.toJson(selectedBook));
        editor.putInt(KEY_PROGRESS, seekBar.getProgress());
        editor.apply();
    }

    // One of the BookListFragmentInterface methods
    public void bookSelected(int index){
        // Store the selected book to use later if activity restarts
        selectedBook = bookList.get(index);

        if(twoPane){
            // Display selected book using previously attached fragment
            bookDetailsFragment.displayBook(selectedBook);
        } else {
            // Display book using new fragment
            fm.beginTransaction()
                    .replace(R.id.container_1, BookDetailsFragment.newInstance(selectedBook), TAG_BOOKDETAILS)
                    .addToBackStack(null)
                    .commit();
        }
    }

    private void showNewBooks(){
        if((fm.findFragmentByTag(TAG_BOOKDETAILS) instanceof BookDetailsFragment)){
            fm.popBackStack();
        }
        ((BookListFragment) fm.findFragmentByTag(TAG_BOOKLIST)).showNewBooks();
    }

    // ControlFragmentInterface method implementation
    @Override
    public void bookPlay() {

        if(selectedBook != null){

            playingBook = selectedBook;
            updateNowPlaying();

            String downloadString = "https://kamorris.com/lab/audlib/download.php?id=" + playingBook.getId();
            Log.d("MyTag", "downloadString: " + downloadString);
            // greatexpectations_01_dickens_64kb.mp3
            // TODO figure out how to generate this filename
            // Rather than generating it, try getting it from the Book object
            // If fileName is null, then the book hasn't been downloaded.
            // If the book downloads, then set the book's fileName
            // Be careful about which version of the book you edit, might not persist

            String fileName = null;

            if(fileName != null){

                // file's declaration might have to be moved to increase scope
                File file = new File(getFilesDir(), fileName);
                // If downloaded, play the book from downloaded source instead of streaming it
                if(isConnected){
                    Log.d("MyTag", "Playing from file");
                    mediaControlBinder.play(file);
                }
            } else {
                // Stream as usual
                // Also begin downloading the book in the background
                downloadBook(downloadString);

                if(isConnected){
                    mediaControlBinder.play(playingBook.getId());
                }
            }
            // TODO Consider moving startService()
            startService(serviceIntent);
        }
    }

    @Override
    public void bookPause() {
        if(isConnected) {
            mediaControlBinder.pause();
        }
    }

    @Override
    public void downloadBook(String downloadString) {

        Log.d("MyTag", "Downloading");
        // Download the audiobook
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(downloadString));
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_FAILED | DownloadManager.STATUS_SUCCESSFUL);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        long downloadId = dm.enqueue(req);


        // Set the fileName to downloadedBooks (Map<Integer, Book>)
//        dm.
        Cursor c = dm.query(query);
        int status;
        // Checks and returns status
        if(c.moveToFirst()){
            status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));

            switch(status){
                case DownloadManager.STATUS_FAILED: {
                    Log.d("MyTag", "Download failed");
                } case DownloadManager.STATUS_SUCCESSFUL: {
                    Log.d("MyTag", "Download successful");
                }
            }
            c.close();

        }

    }

    @Override
    public void bookStop() {
        mediaControlBinder.stop();
        // Update now playing
        seekBar.setProgress(0);
        nowPlayingTextView.setText("");
    }

    @Override
    public void updateSeekProgress(int progress){
        if(playingBook != null) {
            mediaControlBinder.seekTo(progress);
        }
    }


    @Override
    public void updateNowPlaying(){
        if(playingBook != null){
            // Update now playing
            seekBar.setMax(playingBook.getDuration());
            String displayText = "Now playing: " + playingBook.getTitle();
            // Sometimes crashes because nowPlayingTextView isn't initialized yet?
            try{
                nowPlayingTextView.setText(displayText);
            } catch (Exception e){
                e.printStackTrace();
            }

        } else {
            // No book
            try{
                nowPlayingTextView.setText("");
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setControlReferences(SeekBar seekBar, TextView nowPlayingTextView){
        this.seekBar = seekBar;
        this.nowPlayingTextView = nowPlayingTextView;
        // TODO might fix visual bugs if use of these references are eliminated.
    }

    public ArrayList<String> bookListToStringSet(BookList bookList){
        ArrayList<String> books = new ArrayList<>();
        Gson gson = new Gson();
        Book tempBook;

        for(int i = 0; i < bookList.size(); i++){
            try{
                tempBook = bookList.get(i);
                books.add(gson.toJson(tempBook));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return books;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState){
        super.onSaveInstanceState(outState);
        // Saves selected book
        outState.putParcelable(KEY_SELECTED_BOOK, selectedBook);
        outState.putParcelable(KEY_BOOKLIST, bookList);
        outState.putInt(KEY_PROGRESS, seekBar.getProgress());
        outState.putParcelable(KEY_PLAYING_BOOK, playingBook);
    }

    @Override
    public void onBackPressed() {
        // If the user hits the back button, clear the selected book
        super.onBackPressed();
        selectedBook = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

}