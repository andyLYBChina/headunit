package info.anodsplace.headunit.aap

import android.content.Context
import info.anodsplace.headunit.connection.AccessoryConnection
import info.anodsplace.headunit.decoder.MicRecorder
import info.anodsplace.headunit.main.BackgroundNotification
import info.anodsplace.headunit.utils.AppLog
import info.anodsplace.headunit.utils.Settings

/**
 * @author algavris
 * *
 * @date 13/02/2017.
 */
internal interface AapRead {
    fun read(): Int

    abstract class Base internal constructor(
            private val connection: AccessoryConnection?,
            internal val ssl: AapSsl,
            internal val handler: AapMessageHandler) : AapRead {

        override fun read(): Int {
            if (connection == null) {
                AppLog.e("No connection.")
                return -1
            }

            return doRead(connection)
        }

        protected abstract fun doRead(connection: AccessoryConnection): Int
    }

    object Factory {
        fun create(connection: AccessoryConnection, transport: AapTransport, recorder: MicRecorder, aapAudio: AapAudio, aapVideo: AapVideo, settings: Settings, notification: BackgroundNotification, context: Context): AapRead {
            val handler = AapMessageHandlerType(transport, recorder, aapAudio, aapVideo, settings, notification, context)

            return if (connection.isSingleMessage)
                AapReadSingleMessage(connection, transport.ssl, handler)
            else
                AapReadMultipleMessages(connection, transport.ssl, handler)
        }
    }
}
