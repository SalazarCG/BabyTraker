package com.salazar.babytraker.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageStorageManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Toma un URI (temporal de cámara o galería), lo procesa, 
     * lo optimiza y lo guarda en el almacenamiento interno de la app.
     * Retorna el URI del archivo permanente.
     */
    fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            // Crear nombre de archivo único
            val fileName = "baby_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)

            FileOutputStream(file).use { out ->
                // Comprimimos al 80% para ahorrar espacio sin perder calidad visual notable
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
