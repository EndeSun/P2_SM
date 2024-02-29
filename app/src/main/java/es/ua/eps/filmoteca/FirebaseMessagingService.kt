package es.ua.eps.filmoteca

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

//Interface for the messages
interface MessageListener {
    fun onMessageReceived(message: RemoteMessage)
}


class FirebaseMessagingService : FirebaseMessagingService() {
    companion object{
        lateinit var token: String
        private val listeners = mutableListOf<MessageListener>()
        fun registerListener(listener: MessageListener) {
            listeners.add(listener)
        }
        fun unregisterListener(listener: MessageListener) {
            listeners.remove(listener)
        }
    }
    override fun onNewToken(newToken: String) {
        token = newToken
    }
    //When the message arrives when the user are on the device, we have to handle it ourselves at this class.
    override fun onMessageReceived(message: RemoteMessage) {
        listeners.forEach { it.onMessageReceived(message) }
    }
}