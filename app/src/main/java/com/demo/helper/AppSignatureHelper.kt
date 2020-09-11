package com.example.androidphonepermission.helper

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class AppSignatureHelper (context: Context) : ContextWrapper(context) {

    companion object {
        val TAG = "key_hash"
        private val HASH_TYPE = "SHA-256"
        val NUM_HASHED_BYTES = 9
        val NUM_BASE64_CHAR = 11
    }

    fun getAppSignatures(): ArrayList<String> {
        val appCodes = ArrayList<String>()

        return try {
            val packageName = packageName
            val packageManager = packageManager
            val signatures = packageManager.getPackageInfo(packageName,
                PackageManager.GET_SIGNATURES).signatures

            signatures
                .mapNotNull { hash(packageName, it.toCharsString()) }
                .mapTo(appCodes) { it }
            return appCodes
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "Unable to find package to obtain hash.", e)
            ArrayList<String>()
        }
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        return try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            }
            var hashSignature = messageDigest.digest()

            // truncated into NUM_HASHED_BYTES
            hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
            // encode into Base64
            var base64Hash = Base64.encodeToString(hashSignature, Base64.NO_PADDING or Base64.NO_WRAP)
            base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)

            Log.d(TAG, "-- key hash: $base64Hash")
            base64Hash
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "hash:NoSuchAlgorithm", e)
            null
        }
    }
}