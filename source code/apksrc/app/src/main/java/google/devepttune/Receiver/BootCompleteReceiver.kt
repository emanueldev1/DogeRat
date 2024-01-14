package google.devepttune.Receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import google.devepttune.MainService

class BootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent) {
        ContextCompat.startForegroundService(p0, Intent(p0, MainService::class.java))
    }
}