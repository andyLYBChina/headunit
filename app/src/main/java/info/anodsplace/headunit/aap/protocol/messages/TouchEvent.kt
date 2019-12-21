package info.anodsplace.headunit.aap.protocol.messages

import android.view.MotionEvent
import com.google.protobuf.Message
import info.anodsplace.headunit.aap.AapMessage
import info.anodsplace.headunit.aap.protocol.Channel
import info.anodsplace.headunit.aap.protocol.proto.Input

/**
 * @author algavris
 * @date 13/02/2017.
 */
class TouchEvent(timeStamp: Long, action: Input.TouchEvent.PointerAction, actionIndex: Int, pointerData: Iterable<Triple<Int, Int, Int>>)
    : AapMessage(Channel.ID_INP, Input.MsgType.EVENT_VALUE, makeProto(timeStamp, action, actionIndex, pointerData)) {

    constructor(timeStamp: Long, action: Int, actionIndex: Int, pointerData: Iterable<Triple<Int, Int, Int>>)
        : this(timeStamp, Input.TouchEvent.PointerAction.forNumber(action), actionIndex, pointerData)

    constructor(timeStamp: Long, action: Input.TouchEvent.PointerAction, x: Int, y: Int)
        : this(timeStamp, action, 0, listOf(Triple(0, x, y)))

    companion object {
        fun motionEventToAction(event: MotionEvent): Int? {
            return when (event.actionMasked) {
                MotionEvent.ACTION_POINTER_DOWN -> Input.TouchEvent.PointerAction.TOUCH_ACTION_DOWN_VALUE
                MotionEvent.ACTION_DOWN -> MotionEvent.ACTION_DOWN
                MotionEvent.ACTION_MOVE -> MotionEvent.ACTION_MOVE
                MotionEvent.ACTION_CANCEL -> MotionEvent.ACTION_UP
                MotionEvent.ACTION_POINTER_UP -> MotionEvent.ACTION_POINTER_UP
                MotionEvent.ACTION_UP -> MotionEvent.ACTION_UP
                else -> null
            }
        }

        private fun makeProto(timeStamp: Long, action: Input.TouchEvent.PointerAction, actionIndex: Int, pointerData: Iterable<Triple<Int, Int, Int>>): Message {
            val touchEvent = Input.TouchEvent.newBuilder()
                    .also {
                        pointerData.forEach { data ->
                            it.addPointerData(
                                    Input.TouchEvent.Pointer.newBuilder().also { pointer ->
                                        pointer.pointerId = data.first
                                        pointer.x = data.second
                                        pointer.y = data.third
                                    })
                        }
                        it.actionIndex = actionIndex
                        it.action = action
                    }

            return Input.InputReport.newBuilder()
                    .setTimestamp(timeStamp * 1000000L)
                    .setTouchEvent(touchEvent).build()
        }
    }
}