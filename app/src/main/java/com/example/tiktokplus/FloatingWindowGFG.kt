package com.example.tiktokplus;

import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.*
import android.view.View.OnTouchListener
import android.widget.Button
import com.example.tiktokplus.R

class FloatingWindowGFG : Service() {
    //The reference variables for the
    //ViewGroup, WindowManager.LayoutParams, WindowManager, Button, EditText classes are created
    private var floatView: ViewGroup? = null
    private var floatView2: ViewGroup? = null
    private var LAYOUT_TYPE = 0
    private var floatWindowLayoutParam: WindowManager.LayoutParams? = null
    private var floatWindowLayoutParam2: WindowManager.LayoutParams? = null
    private var windowManager: WindowManager? = null
    private val maximizeBtn: Button? = null

    //As FloatingWindowGFG inherits Service class, it actually overrides the onBind method
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        //The screen height and width are calculated, cause
        //the height and width of the floating window is set depending on this
        val metrics = applicationContext.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels

        //To obtain a WindowManager of a different Display,
        //we need a Context for that display, so WINDOW_SERVICE is used
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        //A LayoutInflater instance is created to retrieve the LayoutInflater for the floating_layout xml
        val inflater = baseContext.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        //inflate a new view hierarchy from the floating_layout xml
        floatView = inflater.inflate(R.layout.floating_layout, null) as ViewGroup?
        floatView2 = inflater.inflate(R.layout.floating_layout, null) as ViewGroup?

        //The Buttons and the EditText are connected with
        //the corresponding component id used in floating_layout xml file
        //maximizeBtn = floatView.findViewById(R.id.buttonMaximize);

        //WindowManager.LayoutParams takes a lot of parameters to set the
        //the parameters of the layout. One of them is Layout_type.
        LAYOUT_TYPE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //If API Level is more than 26, we need TYPE_APPLICATION_OVERLAY
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            //If API Level is lesser than 26, then we can use TYPE_SYSTEM_ERROR,
            //TYPE_SYSTEM_OVERLAY, TYPE_PHONE, TYPE_PRIORITY_PHONE. But these are all
            //deprecated in API 26 and later. Here TYPE_TOAST works best.
            WindowManager.LayoutParams.TYPE_TOAST
        }

        //Now the Parameter of the floating-window layout is set.
        //1) The Width of the window will be 55% of the phone width.
        //2) The Height of the window will be 58% of the phone height.
        //3) Layout_Type is already set.
        //4) Next Parameter is Window_Flag. Here FLAG_NOT_FOCUSABLE is used. But
        //problem with this flag is key inputs can't be given to the EditText.
        //This problem is solved later.
        //5) Next parameter is Layout_Format. System chooses a format that supports translucency by PixelFormat.TRANSLUCENT
        floatWindowLayoutParam = WindowManager.LayoutParams(
            width, (height * 0.06f).toInt(),
            LAYOUT_TYPE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        floatWindowLayoutParam2 = WindowManager.LayoutParams(
            width, (height * 0.043f).toInt(),
            LAYOUT_TYPE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        //The Gravity of the Floating Window is set. The Window will appear in the center of the screen
        floatWindowLayoutParam!!.gravity = Gravity.BOTTOM
        floatWindowLayoutParam2!!.gravity = Gravity.CENTER
        //X and Y value of the window is set
        floatWindowLayoutParam2!!.x = 0;
        floatWindowLayoutParam2!!.y = -971;

        //The ViewGroup that inflates the floating_layout.xml is
        //added to the WindowManager with all the parameters
        windowManager!!.addView(floatView, floatWindowLayoutParam)
        windowManager!!.addView(floatView2, floatWindowLayoutParam2)


        //Another feature of the floating window is, the window is movable.
        //The window can be moved at any position on the screen.
        floatView!!.setOnTouchListener(object : OnTouchListener {
            val floatWindowLayoutUpdateParam: WindowManager.LayoutParams = floatWindowLayoutParam as WindowManager.LayoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = floatWindowLayoutUpdateParam.x.toDouble()
                        y = floatWindowLayoutUpdateParam.y.toDouble()
                        //returns the original raw X coordinate of this event
                        px = event.rawX.toDouble()
                        //returns the original raw Y coordinate of this event
                        py = event.rawY.toDouble()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        floatWindowLayoutUpdateParam.x = (x + event.rawX - px).toInt()
                        floatWindowLayoutUpdateParam.y = (y + event.rawY - py).toInt()

                        //updated parameter is applied to the WindowManager
                        windowManager!!.updateViewLayout(floatView, floatWindowLayoutUpdateParam)
                    }
                }
                return false
            }
        })
        floatView2!!.setOnTouchListener(object : OnTouchListener {
            val floatWindowLayoutUpdateParam2: WindowManager.LayoutParams = floatWindowLayoutParam2 as WindowManager.LayoutParams
            var x = 0.0
            var y = 0.0
            var px = 0.0
            var py = 0.0
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        x = floatWindowLayoutUpdateParam2.x.toDouble()
                        y = floatWindowLayoutUpdateParam2.y.toDouble()
                        //returns the original raw X coordinate of this event
                        px = event.rawX.toDouble()
                        //returns the original raw Y coordinate of this event
                        py = event.rawY.toDouble()
                    }
                    MotionEvent.ACTION_MOVE -> {
                        floatWindowLayoutUpdateParam2.x = (x + event.rawX - px).toInt()
                        floatWindowLayoutUpdateParam2.y = (y + event.rawY - py).toInt()

                        //updated parameter is applied to the WindowManager
                        windowManager!!.updateViewLayout(floatView2, floatWindowLayoutUpdateParam2)
                    }
                }
                return false
            }
        })
    }

    //It is called when stopService() method is called in MainActivity
    override fun onDestroy() {
        super.onDestroy()
        stopSelf()
        //Window is removed from the screen
        windowManager!!.removeView(floatView)
        windowManager!!.removeView(floatView2)
    }
}