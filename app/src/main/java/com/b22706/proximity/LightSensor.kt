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

class LightSensor(context:Context, private val listener: SensorEventListener?): SensorEventListener {
    companion object{
        const val LOG_NAME = "LightSensor"
    }
    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager
    val light: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

    var queue: LinkedList<LightSensorData> = LinkedList()
    private set
    data class LightSensorData(
        val time: Long,
        val lux: Float
    )

    fun start(){
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
        if(listener != null) sensorManager.registerListener(listener, light, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun stop(){
        sensorManager.unregisterListener(this, light)
        if(listener != null) sensorManager.unregisterListener(listener, light)
    }

    fun csvWriter(path: String, fileName: String): Boolean {
        //CSVファイルの書き出し
        try{
            //書込み先指定
            val writer = FileWriter("${path}/${fileName}-light.csv")

            //書き込み準備
            val csvPrinter = CSVPrinter(
                writer, CSVFormat.DEFAULT
                    .withHeader(
                        "time",
                        "lux"
                    )
            )
            //書き込み開始
            for(data in queue){
                //データ保存
                csvPrinter.printRecord(
                    data.time.toString(),
                    data.lux.toString()
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
        if(event.sensor.type == Sensor.TYPE_LIGHT){
            val data = event.values.clone()[0]
            queue.add(LightSensorData(System.currentTimeMillis(), data))
            Log.d(ProximitySensor.LOG_NAME, "${event.sensor.type}:${data}")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {
    }
}