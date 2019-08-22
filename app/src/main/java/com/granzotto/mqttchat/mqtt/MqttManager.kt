package com.granzotto.mqttchat.mqtt

import android.util.Log
import io.reactivex.Completable
import io.reactivex.Observable
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.util.*

object MqttManager {

    private const val TAG = "MqttManager"

    private var userName: String? = ""
    private var userPassword: String? = ""

    private var clientID = randomClientID()

    var client: IMqttAsyncClient? = null

    fun setup(
        serverUrl: String,
        serverPort: Int,
        userName: String?,
        userPassword: String?
    ) {
        this.userName = userName
        this.userPassword = userPassword

        var url = if (serverUrl.contains("//")) serverUrl else "tcp://$serverUrl"
        url = "$url:$serverPort"
        client = MqttAsyncClient(url, clientID, MemoryPersistence())
    }

    @ExperimentalStdlibApi
    fun connect(connectionListener: ConnectionListener) {
        val options = MqttConnectOptions()
        userName?.let { options.userName = it }
        userPassword?.toCharArray()?.let { options.password = it }

        client?.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d(TAG, "$topic: ${message?.payload?.decodeToString()}")
            }

            override fun connectionLost(cause: Throwable?) {
                Log.e(TAG, "connectionLost", cause ?: Throwable())
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                Log.d(TAG, "deliveryComplete: ${token?.message?.id}")
            }
        })

        client?.connect(options, this, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d(TAG, "connected onSuccess: $asyncActionToken")
                connectionListener.onConnected()
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e(TAG, "connect onFailure $asyncActionToken", exception ?: Throwable())
                connectionListener.onConnectionError(exception)
            }
        })
    }

    fun disconnect() {
        client?.disconnect()
    }

    fun subscribe(topic: String): Observable<MqttMessage> {
        return Observable.create { emitter ->
            client?.subscribe(topic, 0, this, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    Log.d(TAG, "Subscribed to $topic")
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    if (!emitter.isDisposed) emitter.onError(exception ?: Throwable())
                }
            }) { _, message ->
                if (!emitter.isDisposed) emitter.onNext(message)
            }
        }
    }

    fun publish(topic: String, payload: ByteArray): Completable {
        return Completable.create { emitter ->
            client?.publish(topic, payload, 0, false, this, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken?) {
                    if (!emitter.isDisposed) emitter.onComplete()
                }

                override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                    if (!emitter.isDisposed) emitter.onError(exception ?: Throwable())
                }
            })
        }
    }

    private fun generateRandomKey(hexSize: Int): String {
        val byteArray = ByteArray(hexSize)
        Random().nextBytes(byteArray)
        return byteArray.map { String.format("%02x", it.toInt().and(0xFF)) }
            .reduce { acc, s -> acc + s }
    }

    private fun randomClientID() = "Android-${generateRandomKey(10)}"

}

interface ConnectionListener {
    fun onConnected()
    fun onConnectionError(exception: Throwable?)
}