package info.anodsplace.headunit.connection

/**
 * @author algavris
 * *
 * @date 05/11/2016.
 */

interface AccessoryConnection {
    val isSingleMessage: Boolean
    fun sendBlocking(buf: ByteArray, length: Int, timeout: Int): Int
    fun recvBlocking(buf: ByteArray, length: Int, timeout: Int, readFully: Boolean): Int
    val isConnected: Boolean
    suspend fun connect(): Boolean
    fun disconnect()
}
