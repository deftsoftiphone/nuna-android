package com.demo.providers.socketio

import android.util.Log
import com.demo.R
import com.demo.providers.resources.ResourcesProvider
import com.demo.webservice.APIService
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.engineio.client.transports.WebSocket
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import java.net.URI

class SocketIOImpl(private val resources: ResourcesProvider) : SocketIO {
    private var socket: Socket? = null

    init {
        try {
            socket = IO.socket(
                URI(APIService.BASE_URL),
                IO.Options().apply { transports = arrayOf(WebSocket.NAME) })
        } catch (e: Exception) {
            Log.e("Socket ERROR", "")
            e.printStackTrace()
        }
        setupSocketListeners()
        connect()
    }


    override fun connect() {
        socket?.connect()
    }

    override fun isConnected(): Boolean {
        if (socket?.connected() != null) {
            return socket?.connected()!!
        } else {
            return false
        }
    }

    override fun disconnect() {
        socket?.disconnect()
    }

    override fun listenOn(key: String, value: Emitter.Listener) {
        socket?.on(key, value)

    }

    override fun listenOff(key: String, value: Emitter.Listener) {
        socket?.off(key, value)
    }

    override fun <T> emitMessage(key: String, value: T) {
        socket?.emit(key, value)
    }

    private fun setupSocketListeners() {
        listenOn(
            Socket.EVENT_CONNECT
        ) { args ->
            Log.d("EVENT CONNECTED", isConnected().toString())
        }

        listenOn(
            Socket.EVENT_CONNECT_ERROR
        ) { args -> Log.d("EVENT CONNECT ERROR", args[0].toString()) }


        listenOn(resources.getString(R.string.socket_listen_user_login)) { args ->
            if (args.isNotEmpty()) {
                Log.d("LOGIN SUCCESS", args[0].toString())
            }
        }

        listenOn(resources.getString(R.string.socket_listen_back_app_run)) { args ->
            if (args.isNotEmpty()) {
                Log.d("APP RUN BACk SUCCESS", args[0].toString())
            }
        }
    }
}