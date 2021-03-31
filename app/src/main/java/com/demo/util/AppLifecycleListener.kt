package com.demo.util

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.demo.R
import com.demo.providers.resources.ResourcesProvider
import com.demo.providers.socketio.SocketIO


class AppLifecycleListener(
        private val context: Context,
        private val socketIO: SocketIO,
        private val resource: ResourcesProvider
) : LifecycleObserver, ConnectionStateMonitor.OnNetworkAvailableCallbacks {
    private var connectionStateMonitor: ConnectionStateMonitor? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() { // app moved to foreground
        socketIO.connect()
        Handler(Looper.getMainLooper()).postDelayed({
            invokeSocketEvent(resource.getString(R.string.socket_user_login))
        }, 2000)
        startListeningNetwork()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() { // app moved to background
        invokeSocketEvent(resource.getString(R.string.socket_back_app_run))
        socketIO.disconnect()
        stopListeningNetwork()
    }

    private fun invokeSocketEvent(method: String) {
//        if (!socketIO.isConnected())
//            socketIO.connect()
        val userId = Prefs.init().currentUser?.id
        userId?.let {
            if (socketIO.isConnected()) {
                socketIO.emitMessage(
                        method,
                        toJsonObject("userId" to it,   "deviceToken" to context.getUniqueToken(), "success" to "true")
                )
            }
        }
    }

    private fun startListeningNetwork() {
        if (connectionStateMonitor == null)
            connectionStateMonitor = ConnectionStateMonitor(context, this)
        //Register
        connectionStateMonitor?.enable()

        // Recheck network status manually whenever activity resumes
        if (connectionStateMonitor?.hasNetworkConnection() == false) onNegative()
        else onPositive()
    }

    private fun stopListeningNetwork() {
        connectionStateMonitor?.disable()
        connectionStateMonitor = null
    }

    override fun onPositive() {
        socketIO.connect()
        Handler(Looper.getMainLooper()).postDelayed({
            invokeSocketEvent(resource.getString(R.string.socket_user_login))
        }, 2000)
    }

    override fun onNegative() {
    }
}