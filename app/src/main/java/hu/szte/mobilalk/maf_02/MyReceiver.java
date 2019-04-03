package hu.szte.mobilalk.maf_02;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        StringBuilder stb = new StringBuilder();
        stb.append("Action: " + intent.getAction() + "\n\n");
        stb.append("URI: " + intent.toUri(Intent.URI_INTENT_SCHEME).toString() + "\n");
        String log = stb.toString();
        Log.i("MRECEIVER", log);
        Toast.makeText(context, log, Toast.LENGTH_LONG).show();
    }
}
