package com.demo.base

import android.os.Bundle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.demo.util.Util
import java.lang.reflect.Constructor

class MyViewModelProvider (val callback : AsyncViewController) : ViewModelProvider.NewInstanceFactory(){

    var extras : Bundle? = null

    constructor(callback : AsyncViewController, extras : Bundle?) : this(callback){
        this.extras = extras
    }

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return try {
            if (extras==null) {
                val constructor: Constructor<T> = modelClass.getDeclaredConstructor(AsyncViewController::class.java)
                constructor.newInstance(callback)
            }else {
                val constructor: Constructor<T> = modelClass.getDeclaredConstructor(AsyncViewController::class.java, Bundle::class.java)
                constructor.newInstance(callback, extras)
            }
        } catch (e: Exception) {
            Util.log("Could not create new instance of class %s" + modelClass.canonicalName)
            throw e
        }
    }
}