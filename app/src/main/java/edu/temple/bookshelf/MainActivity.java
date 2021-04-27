package edu.temple.bookshelf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.DownloadManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.util.Objects;

import edu.temple.audiobookplayer.AudiobookService;

public class MainActivity
        extends AppCompatActivity
        implements BookListFragment.BookListFragmentInterface ,
        ControlFragment.ControlFragmentInterface {

    FragmentManager fm;
    ControlFragment controlFragment;
    BookDetailsFragment bookDetailsFragment;
    BookListFragment bookListFragment;

    JSONObject audioBookList;
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
    private final String KEY_AUDIO_BOOKS = "audioBookList";
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
        audioBookList = gson.fromJson(preferences.getString(KEY_AUDIO_BOOKS, null), JSONObject.class);

        // Fetch selected book if there was one
        if(savedInstanceState != null){
            // Fetch selected book if there was one
            selectedBook = savedInstanceState.getParcelable(KEY_SELECTED_BOOK);
            // Fetch previously searched books if one was previously retrieved
            bookList = savedInstanceState.getParcelable(KEY_BOOKLIST);
            seekProgress = savedInstanceState.getInt(KEY_PROGRESS);
        }
        if(bookList == null){
            bookList = new BookList();
        }
        if(audioBookList == null){
            audioBookList = new JSONObject();
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
                .replace(R.id.control_container, controlFragment, TAG_CONTROL)
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
        editor.putString(KEY_AUDIO_BOOKS, gson.toJson(audioBookList));
        editor.apply();
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

    // One of the BookListFragmentInterface methods
    public void bookSelected(int index){
        // Store the selected book
        selectedBook = bookList.get(index);
        Log.d("MyTag", "Selected: " + selectedBook.getTitle());
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
        ((BookListFragment) Objects.requireNonNull(fm.findFragmentByTag(TAG_BOOKLIST))).showNewBooks();
    }

    // ControlFragmentInterface method implementation
    @Override
    public void bookPlay() {

        if(playingBook != selectedBook && selectedBook != null){
            // New book started, save progress
            int progress;
            try{
                progress = seekBar.getProgress();
                if(progress <= 10){
                    progress = 0;
                } else {
                    progress -= 10;
                }
                audioBookList.put(String.valueOf(playingBook.getId()), progress);
            } catch(Exception e){
                e.printStackTrace();
            }

        }
        playingBook = selectedBook;

        if(playingBook != null){
            updateNowPlaying();
            int progress = 0;

            String downloadString = "https://kamorris.com/lab/audlib/download.php?id=" + playingBook.getId();
            Log.d("MyTag", "downloadString: " + downloadString);
            // greatexpectations_01_dickens_64kb.mp3

            // Rather than generating it, try getting it from audioBookList
            // If fileName is null, then the book hasn't been downloaded.
            // If the book downloads, then set the book's fileName

            // File downloading currently doesn't function
            // Tried adding android:usesCleartextTraffic="true" in AndroidManifest.xml
            // but download continues to fail. This implementation only leads to audio stream,
            // but most of the behavior is implemented.
            String fileName = null;

            if(fileName != null){   // Already downloaded, get progress
                try{
                    progress = (int) audioBookList.get(String.valueOf(playingBook.getId()));
                } catch (Exception e){
                    e.printStackTrace();
                }

                File file = new File(getFilesDir(), fileName);
                // play the book from downloaded source instead of streaming it
                if(isConnected){
                    Log.d("MyTag", "Playing from file");
                    mediaControlBinder.play(file, progress);
                }
            } else {    // Not downloaded, put new entry in audioBookList

                try{
                    audioBookList.put(String.valueOf(playingBook.getId()), 0);
                } catch(Exception e){
                    e.printStackTrace();
                }

                downloadBook(downloadString);   // Download book in the background

                if(isConnected){                // Stream as usual
                    mediaControlBinder.play(playingBook.getId());
                }
            }
            startService(serviceIntent);
        } else {
            Toast.makeText(this, "No book selected", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    @Override
    public void bookPause() {
        // Save progress on pause
        int progress;
        try{
            progress = seekBar.getProgress();
            if(progress <= 10){
                progress = 0;
            } else {
                progress -= 10;
            }
            audioBookList.put(String.valueOf(playingBook.getId()), progress);
        } catch(Exception e){
            e.printStackTrace();
        }
        if(isConnected) {
            mediaControlBinder.pause();
        }
    }

    // TODO fix "downloadfile-1.bin Download unsuccessful" issue
    @Override
    public void downloadBook(String downloadString) {

        Log.d("MyTag", "Downloading");
        // Download the audiobook
        DownloadManager.Request req = new DownloadManager.Request(Uri.parse(downloadString));
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterByStatus(DownloadManager.STATUS_FAILED | DownloadManager.STATUS_SUCCESSFUL);
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        dm.enqueue(req); // returns a downloadId (long)

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
        // Set progress to 0 if stop is pressed.
        try{
            audioBookList.put(String.valueOf(playingBook.getId()), 0);
        } catch(Exception e){
            e.printStackTrace();
        }

        mediaControlBinder.stop();
        // Update now playing
        playingBook = null;
        seekBar.setProgress(0);
        updateNowPlaying();
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
//            controlFragment.updateNowPlaying(displayText);
            nowPlayingTextView.setText(displayText);
        } else {
            // No book
            try{
//                controlFragment.updateNowPlaying("");
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