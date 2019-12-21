package info.anodsplace.headunit.aap

import info.anodsplace.headunit.aap.protocol.messages.Messages
import info.anodsplace.headunit.ssl.NoCheckTrustManager
import info.anodsplace.headunit.ssl.SingleKeyKeyManager
import java.nio.ByteBuffer
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLEngine
import javax.net.ssl.SSLEngineResult

class AapSslContext(keyManger: SingleKeyKeyManager): AapSsl {
    private val sslContext: SSLContext = SSLContext.getInstance("TLSv1.2").apply {
        init(arrayOf(keyManger), arrayOf(NoCheckTrustManager()), null)
    }
    private lateinit var sslEngine: SSLEngine
    private lateinit var txBuffer: ByteBuffer
    private lateinit var rxBuffer: ByteBuffer

    override fun prepare(): Int {
        sslEngine = sslContext.createSSLEngine().apply {
            useClientMode = true
            session.also {
                val appBufferMax = it.applicationBufferSize
                val netBufferMax = it.packetBufferSize

                txBuffer = ByteBuffer.allocateDirect(netBufferMax)
                rxBuffer = ByteBuffer.allocateDirect(Messages.DEF_BUFFER_LENGTH.coerceAtLeast(appBufferMax + 50))
            }
        }
        return 0
    }

    override fun handshakeRead(): ByteArray {
        txBuffer.clear()
        val result = sslEngine.wrap(emptyArray(), txBuffer)
        runDelegatedTasks(result, sslEngine)
        val resultBuffer = ByteArray(result.bytesProduced())
        txBuffer.flip()
        txBuffer.get(resultBuffer)
        return resultBuffer
    }

    override fun handshakeWrite(start: Int, length: Int, buffer: ByteArray): Int {
        rxBuffer.clear()
        val receivedHandshakeData = ByteArray(length)
        System.arraycopy(buffer, start, receivedHandshakeData, 0, length)

        val data = ByteBuffer.wrap(receivedHandshakeData)
        val result = sslEngine.unwrap(data, rxBuffer)
        runDelegatedTasks(result, sslEngine)
        return receivedHandshakeData.size
    }

    override fun decrypt(start: Int, length: Int, buffer: ByteArray): ByteArrayWithLimit? {
        rxBuffer.clear()
        val encrypted = ByteBuffer.wrap(buffer, start, length)
        val result = sslEngine.unwrap(encrypted, rxBuffer)
        runDelegatedTasks(result, sslEngine)
        val resultBuffer = ByteArray(result.bytesProduced())
        rxBuffer.flip()
        rxBuffer.get(resultBuffer)
        return ByteArrayWithLimit(resultBuffer, resultBuffer.size)
    }

    override fun encrypt(offset: Int, length: Int, buffer: ByteArray): ByteArrayWithLimit? {
        txBuffer.clear()
        val byteBuffer = ByteBuffer.wrap(buffer, offset, length)
        val result = sslEngine.wrap(byteBuffer, txBuffer)
        runDelegatedTasks(result, sslEngine)
        val resultBuffer = ByteArray(result.bytesProduced() + offset)
        txBuffer.flip()
        txBuffer.get(resultBuffer, offset, result.bytesProduced())
        return ByteArrayWithLimit(resultBuffer, resultBuffer.size)
    }

    private fun runDelegatedTasks(result: SSLEngineResult, engine: SSLEngine) {
        if (result.handshakeStatus === SSLEngineResult.HandshakeStatus.NEED_TASK) {
            var runnable: Runnable? = engine.delegatedTask
            while (runnable != null) {
                runnable.run()
                runnable = engine.delegatedTask
            }
            val hsStatus = engine.handshakeStatus
            if (hsStatus === SSLEngineResult.HandshakeStatus.NEED_TASK) {
                throw Exception("handshake shouldn't need additional tasks")
            }
        }
    }
}
