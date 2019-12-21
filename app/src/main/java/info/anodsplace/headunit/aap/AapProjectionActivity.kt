package info.anodsplace.headunit.aap

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.SurfaceHolder

import info.anodsplace.headunit.App
import info.anodsplace.headunit.aap.protocol.Screen
import info.anodsplace.headunit.aap.protocol.messages.TouchEvent
import info.anodsplace.headunit.aap.protocol.messages.VideoFocusEvent
import info.anodsplace.headunit.app.SurfaceActivity
import info.anodsplace.headunit.utils.AppLog
import info.anodsplace.headunit.utils.IntentFilters
import info.anodsplace.headunit.contract.KeyIntent
import kotlinx.android.synthetic.main.activity_headunit.*

class AapProjectionActivity : SurfaceActivity(), SurfaceHolder.Callback {

    private lateinit var screen: Screen
    private val disconnectReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            finish()
        }
    }

    private val keyCodeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            intent.getParcelableExtra<KeyEvent?>(KeyIntent.extraEvent)?.let { event ->
                onKeyEvent(event.keyCode, event.action == KeyEvent.ACTION_DOWN)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        AppLog.i("HeadUnit for Android Auto (tm) - Copyright 2011-2015 Michael A. Reid. All Rights Reserved...")

        screen = Screen.forResolution(App.provide(this).settings.resolution)

        surface.setSurfaceCallback(this)
        surface.setOnTouchListener { _, event ->
            sendTouchEvent(event)
            true
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(disconnectReceiver)
        unregisterReceiver(keyCodeReceiver)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(disconnectReceiver, IntentFilters.disconnect)
        registerReceiver(keyCodeReceiver, IntentFilters.keyEvent)
    }

    val transport: AapTransport
        get() = App.provide(this).transport

    override fun surfaceCreated(holder: SurfaceHolder) {

    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        transport.send(VideoFocusEvent(gain = true, unsolicited = false))
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        transport.send(VideoFocusEvent(gain = false, unsolicited = false))
    }

    private fun sendTouchEvent(event: MotionEvent) {
        val action = TouchEvent.motionEventToAction(event) ?: return
        val ts = SystemClock.elapsedRealtime()

        val scaleX = (surface.width / screen.width.toFloat())
        val scaleY = (surface.height / screen.height.toFloat())
        val pointerData = mutableListOf<Triple<Int, Int, Int>>()
        repeat(event.pointerCount) { pointerIndex ->
            val pointerId = event.getPointerId(pointerIndex)
            val x = event.getX(pointerIndex) / scaleX
            val y = event.getY(pointerIndex) / scaleY
            if (x < 0 || x >= 65535 || y < 0 || y >= 65535) return
            pointerData.add(Triple(pointerId, x.toInt(), y.toInt()))
        }

        transport.send(TouchEvent(ts, action, event.actionIndex, pointerData))
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        AppLog.i("KeyCode: %d", keyCode)
        // PRes navigation on the screen
        onKeyEvent(keyCode, true)
        return super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        AppLog.i("KeyCode: %d", keyCode)
        onKeyEvent(keyCode, false)
        return super.onKeyUp(keyCode, event)
    }

    private fun onKeyEvent(keyCode: Int, isPress: Boolean) {
        transport.send(keyCode, isPress)
    }

    companion object {
        const val EXTRA_FOCUS = "focus"

        fun intent(context: Context): Intent {
            val aapIntent = Intent(context, AapProjectionActivity::class.java)
            aapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            return aapIntent
        }
    }
}