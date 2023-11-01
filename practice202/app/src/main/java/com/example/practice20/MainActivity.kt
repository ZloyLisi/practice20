package com.example.practice20

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlin.math.round

class MainActivity : AppCompatActivity(), SensorEventListener{
    private lateinit var sensorManager: SensorManager
    private lateinit var gyroscope: Sensor
    private var running = true

    private var x: Float = 0.0F
    private var y: Float = 0.0F
    private var z: Float = 0.0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView : TextView = findViewById(R.id.textView)
        val btn : Button = findViewById(R.id.button)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)!!

        var runnable: Runnable? = null
        var thread: Thread = Thread()
        btn.setOnClickListener(){
            if(!running){
                running = true
                btn.setText(R.string.btn_off)
                runnable = Runnable{
                    try {
                        while(running) {
                            textView.post(Runnable {
                                sensorManager?.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)

                                x = round(x * 100) / 100
                                y = round(y * 100) / 100
                                z = round(z * 100) / 100

                                textView.text = "X = $x\nY = $y\nZ = $z"
                            })
                            Thread.sleep(500)
                            sensorManager?.unregisterListener(this)
                        }
                    }
                    catch(e: InterruptedException){
                        e.printStackTrace()
                    }
                }
                thread = Thread(runnable)
                thread.start()
            }
            else{
                try {
                    running = false
                    btn.setText(R.string.btn_on)
                    thread.interrupt()
                }
                catch(e: InterruptedException){
                    e.printStackTrace()
                }
            }
        }

    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_GYROSCOPE) {
            x = event.values[0] // Ось X
            y = event.values[1] // Ось Y
            z = event.values[2] // Ось Z
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

}