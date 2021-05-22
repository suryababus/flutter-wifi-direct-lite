package com.example.flutter_wifi_direct_lite


import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.BufferedOutputStream
import java.io.File
import java.io.IOException
import java.net.Socket


class SendReceive(  var socket: Socket,private var channelToFlutter: ChannelToFlutter,private val context:Context) : Thread() {

    private var inputStream= socket.getInputStream()
    private var outputStream = (socket.getOutputStream())


    override fun run() {

        super.run()
        Log.d("SendReceive","Successfully connected")
        val buffer = ByteArray(1024)
        while(!currentThread().isInterrupted){
            try {
                val length = inputStream.read(buffer)
                if(length==0) continue
                val message = String(buffer,0,length)
                Log.d("Received message:",message)
                when(message){
                    "file"->{
                        readFile()
                    }
                    else->{
                        channelToFlutter.sendData(
                            ChannelToFlutter.MessageCode.MESSAGE_RECEIVED,
                            message
                        )
                    }
                }


            }catch (e:Exception){
                e.printStackTrace()
                channelToFlutter.sendData(
                    ChannelToFlutter.MessageCode.CONNECTION_CHANGED,
                    "DISCONNECTED"
                )
                currentThread().interrupt()
            }
        }

    }

    private fun readFile(){
        Log.d("Read File","Start")
        try {
            val buffer = ByteArray(2745752)
            var length = inputStream.read(buffer)
            val raw = String(buffer, 0, length).split(":,:")
            val fileBytesSize = raw[0].toLong()
            val fileName = raw[1]
            val extension = raw[2]
            val mimeType = raw[3]

            Log.d("Read File size ", "$fileBytesSize $fileName")

            val outputDir =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES); // context being the Activity pointer
            val outputFile = File(outputDir, "$fileName.$extension")
            outputFile.createNewFile()
            val fileBufferedWriter = BufferedOutputStream(outputFile.outputStream())
            var readBytes = 0
            while (readBytes < fileBytesSize) {
                length = inputStream.read(buffer)
                readBytes += length
                fileBufferedWriter.write(buffer, 0, length)
                Log.d("Read File", readBytes.toString())
            }
            Log.d("Read File", "END")
            fileBufferedWriter.close()
        }
        catch (e:Exception){
            e.printStackTrace()
        }
//        try {
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.setDataAndType(Uri.fromFile(outputFile), mimeType)
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK;
//            context.startActivity(intent)
//        } catch (e: ActivityNotFoundException) {
//            // no Activity to handle this kind of files
//        }

    }
    fun getMimeType(url: String?): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }

    fun write(msg: String){
        try {
            Thread{
                outputStream.write(msg.toByteArray())
                Log.d("sent message:", msg)
            }.start()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }

    fun write(file: File){
        try {
            Thread{
                val metaData = file.length().toString() +":,:"+ file.nameWithoutExtension + ":,:" + file.extension+":,:"+getMimeType(file.absolutePath)
                outputStream.write("file".toByteArray())
                sleep(500)
                outputStream.write(metaData.toByteArray())
                sleep(500)

                outputStream.write(file.readBytes())
                Log.d("sent File:", file.name)
            }.start()
        }catch (e:IOException){
            e.printStackTrace()
        }
    }



    override fun destroy() {
        inputStream.close()
        outputStream.close()
        socket.close()
    }
}

