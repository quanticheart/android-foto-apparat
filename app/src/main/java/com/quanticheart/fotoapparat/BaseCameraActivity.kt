/*
 *
 *  *                                     /@
 *  *                      __        __   /\/
 *  *                     /==\      /  \_/\/
 *  *                   /======\    \/\__ \__
 *  *                 /==/\  /\==\    /\_|__ \
 *  *              /==/    ||    \=\ / / / /_/
 *  *            /=/    /\ || /\   \=\/ /
 *  *         /===/   /   \||/   \   \===\
 *  *       /===/   /_________________ \===\
 *  *    /====/   / |                /  \====\
 *  *  /====/   /   |  _________    /      \===\
 *  *  /==/   /     | /   /  \ / / /         /===/
 *  * |===| /       |/   /____/ / /         /===/
 *  *  \==\             /\   / / /          /===/
 *  *  \===\__    \    /  \ / / /   /      /===/   ____                    __  _         __  __                __
 *  *    \==\ \    \\ /____/   /_\ //     /===/   / __ \__  ______  ____ _/ /_(_)____   / / / /__  ____ ______/ /_
 *  *    \===\ \   \\\\\\\/   ///////     /===/  / / / / / / / __ \/ __ `/ __/ / ___/  / /_/ / _ \/ __ `/ ___/ __/
 *  *      \==\/     \\\\/ / //////       /==/  / /_/ / /_/ / / / / /_/ / /_/ / /__   / __  /  __/ /_/ / /  / /_
 *  *      \==\     _ \\/ / /////        |==/   \___\_\__,_/_/ /_/\__,_/\__/_/\___/  /_/ /_/\___/\__,_/_/   \__/
 *  *        \==\  / \ / / ///          /===/
 *  *        \==\ /   / / /________/    /==/
 *  *          \==\  /               | /==/
 *  *          \=\  /________________|/=/
 *  *            \==\     _____     /==/
 *  *           / \===\   \   /   /===/
 *  *          / / /\===\  \_/  /===/
 *  *         / / /   \====\ /====/
 *  *        / / /      \===|===/
 *  *        |/_/         \===/
 *  *                       =
 *  *
 *  * Copyright(c) Developed by John Alves at 2020/2/16 at 0:24:55 for quantic heart studios
 *
 */

package com.quanticheart.fotoapparat

import androidx.appcompat.app.AppCompatActivity
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.fileLogger
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.parameter.ScaleType
import io.fotoapparat.result.BitmapPhoto
import io.fotoapparat.selector.*
import io.fotoapparat.view.CameraView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

abstract class BaseCameraActivity : AppCompatActivity() {
    protected lateinit var fotoapparat: Fotoapparat

    /**
     * Default configurations
     */
    private var cameraState = CameraState.BACK
    private var flashState = FlashState.OFF

    fun setCamera(
        cameraView: CameraView,
        startCameraState: CameraState = cameraState,
        startFlashState: FlashState = flashState
    ) {
        cameraState = startCameraState
        flashState = startFlashState
        fotoapparat = Fotoapparat(
            context = this,
            view = cameraView,                   // view which will draw the camera preview
            scaleType = ScaleType.CenterCrop,    // (optional) we want the preview to fill the view
            lensPosition = if (cameraState == CameraState.BACK) back() else front(),               // (optional) we want back camera
            logger = loggers(                    // (optional) we want to log camera events in 2 places at once
                logcat(),                   // ... in logcat
                fileLogger(this)            // ... and to file
            ),
            cameraErrorCallback = { error ->
                println("Recorder errors: $error")
            }   // (optional) log fatal errors
        )
    }

    private fun getConfig(): CameraConfiguration = CameraConfiguration(
        pictureResolution = highestResolution(), // (optional) we want to have the highest possible photo resolution
        previewResolution = highestResolution(), // (optional) we want to have the highest possible preview resolution
        previewFpsRange = highestFps(),          // (optional) we want to have the best frame rate
        focusMode = firstAvailable(              // (optional) use the first focus mode which is supported by device
            continuousFocusPicture(),
            autoFocus(),                       // if continuous focus is not available on device, auto focus will be used
            fixed()                            // if even auto focus is not available - fixed focus mode will be used
        ),
        flashMode = if (flashState == FlashState.TORCH) torch() else off(),
        antiBandingMode = firstAvailable(       // (optional) similar to how it is done for focus mode & flash, now for anti banding
            auto(),
            hz50(),
            hz60(),
            none()
        ),
        jpegQuality = manualJpegQuality(90),     // (optional) select a jpeg quality of 90 (out of 0-100) values
        sensorSensitivity = lowestSensorSensitivity(), // (optional) we want to have the lowest sensor sensitivity (ISO)
        frameProcessor = { frame ->

        }            // (optional) receives each frame from preview stream

        /**
         * Flash modes
         */
//                flashMode = firstAvailable (              // (optional) similar to how it is done for focus mode, this time for flash
//                autoRedEye(),
//                autoFlash(),
//                torch(),
//                off()),
    )

    /**
     * Take picture
     */
    var formatter: SimpleDateFormat = SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US)
    var now: Date = Date()
    protected fun takePicture(file: File? = null, takeBitmap: ((BitmapPhoto?) -> Unit)? = null) {
        val photoResult = fotoapparat.takePicture()
        // save to file
        photoResult.saveToFile(
            file ?: File(
                getExternalFilesDir("photos"),
                formatter.format(now) + "_photo.jpg"
            )
        )

        // obtain Bitmap
        photoResult.toBitmap()
            .whenAvailable { bitmapPhoto ->
                takeBitmap?.invoke(bitmapPhoto)
            }
    }

    /**
     * func
     */

    protected fun changeFlashStatus() {
        flashState = if (flashState == FlashState.OFF) FlashState.TORCH else FlashState.OFF
        fotoapparat.updateConfiguration(
            getConfig().copy(
                flashMode = if (flashState == FlashState.TORCH) torch() else off()
            )
        )
    }

    protected fun changeCameraStatus() {
        cameraState = if (cameraState == CameraState.BACK) CameraState.FRONT else CameraState.BACK
        fotoapparat.switchTo(
            if (cameraState == CameraState.BACK) back() else front(),
            getConfig()
        )
    }

    protected fun setZoon(zoom: Int) {
        fotoapparat.setZoom(zoom.toFloat().div(100))
    }

    protected fun callFocus() {
        fotoapparat.focus()
    }

    protected fun callAutoFocus() {
        fotoapparat.autoFocus()
    }

    /**
     * Enus for states
     */

    enum class CameraState {
        FRONT, BACK
    }

    enum class FlashState {
        TORCH, OFF
    }

    /**
     * init camera
     */

    private fun initCamera() {
        fotoapparat.start()
        fotoapparat.updateConfiguration(getConfig())
    }

    /**
     * init camera
     */
    override fun onStart() {
        super.onStart()
        initCamera()
    }

    override fun onStop() {
        super.onStop()
        fotoapparat.stop()
    }
}