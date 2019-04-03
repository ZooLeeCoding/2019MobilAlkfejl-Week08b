package hu.szte.mobilalk.maf_02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telecom.ConnectionService;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<String> {

    private static String broadcastIntent = "hu.szte.mobilalk.maf_02.CUSTOM_INTENT";

    private int counter;
    private TextView counterView;
    private TextView helloView;

    private BroadcastReceiver br;

    public static final String EXTRA_MESSAGE = "hu.szte.mobilalk.maf_02.MESSAGE";
    public static final int TEXT_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.counterView = findViewById(R.id.countView);
        this.helloView = findViewById(R.id.helloView);


        if(savedInstanceState != null && !savedInstanceState.isEmpty()) {
            this.counter = savedInstanceState.getInt("counter");
            this.helloView.setText(savedInstanceState.getCharSequence("helloView"));
            this.counterView.setText(String.valueOf(this.counter));
        } else {
            this.counter = 0;
        }

        if(getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null,
                    this);
        }

        /*this.br = new MyReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);*/
        this.br = new MyAsyncReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(this.br);
        super.onDestroy();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Toast toast = Toast.makeText(getApplicationContext(), item.getTitle(),
                Toast.LENGTH_SHORT);
        toast.show();

        if(id == R.id.item_async) {
            //new SleeperTask(this.helloView).execute();
            getSupportLoaderManager().restartLoader(0, null,
                    this);
        } else if(id == R.id.item_book){
            Intent intent = new Intent(this, BookActivity.class);
            startActivityForResult(intent, TEXT_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("counter", this.counter);
        outState.putCharSequence("helloView", this.helloView.getText());
    }

    public void toastMe(View view) {
        Context context = getApplicationContext();
        CharSequence text = getResources().getString(R.string.toast_message) +
                this.counter;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void countMe(View view) {
        this.counter++;
        counterView.setText(String.valueOf(this.counter));
    }

    public void launchOther(View view) {
        /*Intent intent = new Intent(this, MessageActivity.class);
        String message = "Counter was: " + this.counter;
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivityForResult(intent, TEXT_REQUEST);*/

        String textMessage = "The counter is " + this.counter;

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, textMessage);
        sendIntent.setType("text/plain");

        if (sendIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(sendIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TEXT_REQUEST) {
            if (resultCode == RESULT_OK) {
                String reply = data.getStringExtra(MessageActivity.EXTRA_REPLY);
                helloView.setText(reply);
            }
        }
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return  new SleeperLoader(this);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        helloView.setText(s);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
