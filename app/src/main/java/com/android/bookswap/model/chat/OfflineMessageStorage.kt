package com.android.bookswap.model.chat

import com.android.bookswap.data.DataMessage
import com.android.bookswap.data.MessageType
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.gson.Gson
import java.io.File

class OfflineMessageStorage (text: String, private val db: FirebaseFirestore) {
    private val messages = mutableListOf<DataMessage>()
    private val maxMessages = 10
    private val tempText = "C:\\Users\\jaime\\Desktop\\Bookswap\\app\\src\\main\\java\\com\\android\\bookswap\\model\\chat\\Messages.txt"
    private val pathToText = text.ifEmpty { tempText }

    fun getMessagesFromText(): MutableList<DataMessage> {
        val gson = Gson()
        val jsonString = File(pathToText).readText()
        val messages = gson.fromJson(jsonString, Array<DataMessage>::class.java).toMutableList()
        return messages
    }

    fun setMessages() {
        val gson = Gson()
        val jsonString = gson.toJson(messages)
        File(pathToText).writeText(jsonString)
    }

    fun addMessage(message: DataMessage) {
        messages.add(message)
    }

    fun getMessages(): List<DataMessage> {
        return messages
    }

    fun clearMessages() {
        messages.clear()
    }


    fun retrieveMessagesFromFirestore(callback: (Result<List<DataMessage>>) -> Unit) {
        db.collection("messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .whereEqualTo("messageType", MessageType.TEXT)
            .limit(maxMessages.toLong())
            .get()
            .addOnSuccessListener { textResult ->
                val textMessages = textResult.documents.mapNotNull { it.toObject(DataMessage::class.java) }

                db.collection("messages")
                    .whereEqualTo("messageType", MessageType.IMAGE)
                    .get()
                    .addOnSuccessListener { imageResult ->
                        val imageMessages = imageResult.documents.mapNotNull { it.toObject(DataMessage::class.java) }
                        val allMessages = (textMessages + imageMessages).sortedBy { it.timestamp }
                        clearMessages()
                        messages.addAll(allMessages)
                        callback(Result.success(allMessages))
                    }
                    .addOnFailureListener { e ->
                        callback(Result.failure(e))
                    }
            }
            .addOnFailureListener { e ->
                callback(Result.failure(e))
            }
    }
}