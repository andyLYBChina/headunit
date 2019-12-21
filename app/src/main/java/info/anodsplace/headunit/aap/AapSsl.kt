package info.anodsplace.headunit.aap

/**
 * @author algavris
 * *
 * @date 14/02/2017.
 */
interface AapSsl {
    fun prepare(): Int
    fun handshakeRead(): ByteArray?
    fun handshakeWrite(start: Int, length: Int, buffer: ByteArray): Int
    fun decrypt(start: Int, length: Int, buffer: ByteArray): ByteArrayWithLimit?
    fun encrypt(offset: Int, length: Int, buffer: ByteArray): ByteArrayWithLimit?
}
