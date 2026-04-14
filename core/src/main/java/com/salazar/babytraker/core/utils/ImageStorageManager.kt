package com.salazar.babytraker.core.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
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
     * Incluye corrección de rotación basada en metadatos EXIF.
     */
    fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            // 1. Obtener la orientación EXIF antes de procesar
            val orientation = getOrientation(uri)

            // 2. Decodificar el Bitmap desde el stream
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (originalBitmap == null) return null

            // 3. Corregir la rotación si es necesario
            val correctedBitmap = rotateBitmap(originalBitmap, orientation)

            // 4. Crear nombre de archivo único
            val fileName = "baby_${UUID.randomUUID()}.jpg"
            val file = File(context.filesDir, fileName)

            // 5. Guardar el bitmap procesado
            FileOutputStream(file).use { out ->
                // Comprimimos al 80% para ahorrar espacio sin perder calidad visual notable
                correctedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)
            }

            // Liberar memoria del bitmap original si se creó uno nuevo rotado
            if (correctedBitmap != originalBitmap) {
                originalBitmap.recycle()
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Extrae la orientación de los metadatos EXIF de la imagen.
     */
    private fun getOrientation(uri: Uri): Int {
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                val exifInterface = ExifInterface(input)
                exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        } catch (e: Exception) {
            ExifInterface.ORIENTATION_NORMAL
        }
    }

    /**
     * Rota el bitmap según la orientación especificada.
     */
    private fun rotateBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
            else -> return bitmap
        }
        
        return try {
            val rotatedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
            )
            rotatedBitmap
        } catch (e: OutOfMemoryError) {
            e.printStackTrace()
            bitmap
        }
    }
}
