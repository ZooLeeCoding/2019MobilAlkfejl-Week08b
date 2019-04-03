package hu.szte.mobilalk.maf_02;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<String>{

    private EditText mBookInput;
    private TextView mAuthorView;
    private TextView mTitleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        this.mBookInput = findViewById(R.id.bookInput);
        this.mAuthorView = findViewById(R.id.authorView);
        this.mTitleView = findViewById(R.id.titleView);

        if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
            this.mAuthorView.setText(savedInstanceState.getCharSequence("authorView"));
            this.mTitleView.setText(String.valueOf("titleView"));
        }

        if(getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null,
                    this);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("authorView", this.mAuthorView.getText());
        outState.putCharSequence("titleView", this.mTitleView.getText());
    }

    public void searchBook(View view) {
        String queryString = mBookInput.getText().toString();

        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if(connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        if(networkInfo != null && networkInfo.isConnected()
            && queryString.length() != 0) {
            Bundle queryBundle = new Bundle();
            queryBundle.putString("queryString", queryString);
            getSupportLoaderManager().restartLoader(0, queryBundle, this);
            mAuthorView.setText("");
            mTitleView.setText("");
        } else {
            Toast.makeText(this, "No network or query string",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void backToMain(View view) {
        String reply = this.mTitleView.getText().toString();
        Intent replyIntent = new Intent();
        replyIntent.putExtra(MessageActivity.EXTRA_REPLY, reply);
        setResult(RESULT_OK, replyIntent);
        finish();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        String queryString = "";
        if(bundle != null) {
            queryString = bundle.getString("queryString");
        }
        return new BookLoader(this, queryString);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            int i = 0;
            String title = null;
            String author = null;

            while(i < itemsArray.length() &&
                    (author == null && title == null)) {
                JSONObject book = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");

                try {
                    title = volumeInfo.getString("title");
                    author = volumeInfo.getString("authors");
                } catch(Exception e) {
                    e.printStackTrace();
                }

                i++;
            }

            if(title != null && author != null) {
                mTitleView.setText(title);
                mAuthorView.setText(author);
            } else {
                mAuthorView.setText("no result");
                mTitleView.setText("no result");
            }

        } catch(JSONException e) {
            e.printStackTrace();
            mTitleView.setText("no result");
            mAuthorView.setText("no result");
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
