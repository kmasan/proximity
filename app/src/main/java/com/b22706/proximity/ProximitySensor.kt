package com.b22706.proximity

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.FileWriter
import java.io.IOException
import java.util.*

class ProximitySensor(context:Context, private val listener: SensorEventListener?): SensorEventListener {
    companion object{
        const val LOG_NAME = "ProximitySensor"
    }
    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val proximity: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    var queue: LinkedList<ProximitySensorData> = LinkedList()
    private set
    data class ProximitySensorData(
        val time: Long,
        val distance: Float
    )

    fun start(){
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
        if(listener != null) sensorManager.registerListener(listener, proximity, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop(){
        sensorManager.unregisterListener(this, proximity)
        if(listener != null) sensorManager.unregisterListener(listener, proximity)
    }

    fun csvWriter(path: String, fileName: String): Boolean {
        //CSVファイルの書き出し
        try{
            //書込み先指定
            val writer = FileWriter("${path}/${fileName}-proximity.csv")

            //書き込み準備
            val csvPrinter = CSVPrinter(
                writer, CSVFormat.DEFAULT
                    .withHeader(
                        "time",
                        "distance"
                    )
            )
            //書き込み開始
            for(data in queue){
                //データ保存
                csvPrinter.printRecord(
                    data.time.toString(),
                    data.distance.toString()
                )
            }
            //データ保存の終了処理
            csvPrinter.flush()
            csvPrinter.close()
            return true
        }catch (e: IOException){
            //エラー処理
            Log.d(LOG_NAME, "${e}:${e.message!!}")
            return false
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        if(event.sensor.type == Sensor.TYPE_PROXIMITY){
            val data = event.values.clone()[0]
            queue.add(ProximitySensorData(System.currentTimeMillis(), data))
            Log.d(LOG_NAME, "${event.sensor.type}:${data}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {
    }
}