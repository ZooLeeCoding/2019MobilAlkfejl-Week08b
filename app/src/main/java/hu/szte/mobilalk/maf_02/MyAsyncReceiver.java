package hu.szte.mobilalk.maf_02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class MyAsyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final PendingResult pendingResult = goAsync();
        Task asyncTask = new Task(pendingResult, intent);
        asyncTask.execute();
    }

    private static class Task extends AsyncTask {

        private final PendingResult pendingResult;
        private final Intent intent;

        private Task(PendingResult pendingResult, Intent intent) {
            this.pendingResult = pendingResult;
            this.intent = intent;
        }

        protected String doInBackground(Object[] objects) {
            StringBuilder stb = new StringBuilder();
            stb.append("Action: " + intent.getAction() + "\n\n");
            stb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
            String log = stb.toString();
            Log.i("MYASYNCRECEIVER", log);
            return log;
        }

        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            pendingResult.finish();
        }
    }
}
