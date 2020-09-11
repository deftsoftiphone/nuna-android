package com.demo.providers.socketio

import com.demo.webservice.APIService
import com.github.nkzawa.emitter.Emitter
import com.github.nkzawa.engineio.client.transports.WebSocket
import com.github.nkzawa.socketio.client.IO
import com.github.nkzawa.socketio.client.Socket
import java.net.URI

class SocketIOImpl : SocketIO {
    private var socket: Socket? = null

    init {
        try {
            val options =
                IO.Options()
            options.transports = arrayOf(WebSocket.NAME)
            socket = IO.socket(URI(APIService.BASE_URL), options)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun connect() {
        socket?.connect()

    }

    override fun isConnected(): Boolean {
        if(socket?.connected()!=null){
            return socket?.connected()!!
        }else{
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
}