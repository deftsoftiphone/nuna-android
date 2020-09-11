package com.demo.providers.socketio

import com.github.nkzawa.emitter.Emitter

interface SocketIO {
    fun connect()
    fun disconnect()
    fun isConnected() : Boolean
    fun listenOn(key: String, value: Emitter.Listener)
    fun listenOff(key: String, value: Emitter.Listener)
    fun  <T>emitMessage(key: String, value: T)
}