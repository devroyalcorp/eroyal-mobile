package com.worka.eroyal.feature.clockinout

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants
import com.worka.eroyal.data.base.LocationData
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.getCurrentDate
import com.worka.eroyal.repository.ClockInOutRepository
import com.worka.eroyal.utils.ImagePickerHelper
import com.worka.eroyal.utils.MultipartHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.core.inject
import java.io.IOException
import java.lang.Exception
import java.util.*


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 2019-12-26.
 */
class ClockInOutViewModel(application: Application) : BaseViewModel(application) {

    val repository: ClockInOutRepository by inject()

    var imageClockInUri = ObservableField(Uri.EMPTY)
    var isImageCaptured = ObservableField(false)
    var isOpenCamera = MutableLiveData<Boolean>()
    var currentLocation = MutableLiveData<LocationData>()
    var labelClockInOutButton = ObservableField(getAppContext().getString(R.string.clock_in))
    var buttonEnable = ObservableField(false)
    var buttonVisibility = ObservableField(View.VISIBLE)
    var buttonRetakeVisibility = ObservableField(View.INVISIBLE)

    var currentDate = ObservableField(getCurrentDate())

    fun onRetakeImage() {
        isImageCaptured.set(false)
        isOpenCamera.value = true
        buttonEnable.set(false)
        buttonRetakeVisibility.set(View.INVISIBLE)
    }

    fun getAbsence(cbOnFinished: () -> Unit){
        repository.getAbsence({

            if (!it?.absence?.clockInTime.isNullOrBlank() && it?.absence?.clockOutTime.isNullOrBlank()) {
                buttonEnable.set(true)
                labelClockInOutButton.set(getAppContext().getString(R.string.clock_out))
                imageClockInUri.set(Uri.parse(it?.absence?.selfieUrl))
                isImageCaptured.set(true)
                buttonRetakeVisibility.set(View.INVISIBLE)
                cbOnFinished.invoke()
            } else if(!it?.absence?.clockInTime.isNullOrBlank() && !it?.absence?.clockOutTime.isNullOrBlank()) {
                imageClockInUri.set(Uri.parse(it?.absence?.selfieUrl))
                isImageCaptured.set(true)
                buttonRetakeVisibility.set(View.INVISIBLE)
                buttonVisibility.set(View.INVISIBLE)
                cbOnFinished.invoke()
            }
        },{
            onRetakeImage()
            cbOnFinished.invoke()
        })
    }

    fun saveBitmap(signature: Bitmap) {
        try {
            val photo = ImagePickerHelper.createImageFile()
            imageClockInUri.set(photo.toUri())
            ImagePickerHelper.saveBitmapToJPG(signature, photo)
            buttonRetakeVisibility.set(View.VISIBLE)
            isImageCaptured.set(true)
            buttonEnable.set(true)
        } catch (e: IOException) {
            chuckerCollector.onError(ClockInOutViewModel::class.java.simpleName, e)
            e.printStackTrace()
        }
    }

    fun clockInOut(context: Context, cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
       viewModelScope.launch {
           var addresses: List<Address> = arrayListOf()
           val geocoder = Geocoder(context, Locale.getDefault())

               if (currentLocation.value != null && currentLocation.value?.latitude != null && currentLocation.value?.longitude != null) {
                   withContext(Dispatchers.IO) {
                       try {
                           addresses = geocoder.getFromLocation(
                               currentLocation.value?.latitude ?: 0.0,
                               currentLocation.value?.longitude ?: 0.0,
                               1
                           )
                       } catch (e: Exception) {
                           chuckerCollector.onError("GEO_CODER_CRASH", e)
                           addresses = arrayListOf()
                       }
                   }

                   withContext(Dispatchers.Main) {
                       val address: String = if (!addresses.isNullOrEmpty()) {
                           addresses[0].getAddressLine(0).orEmpty()
                       } else {
                           ""
                       }

                       when (labelClockInOutButton.get()) {
                           getAppContext().getString(R.string.clock_in) -> {
                               repository.clockIn(address,
                                   currentLocation.value?.latitude,
                                   currentLocation.value?.longitude,
                                   imageClockInUri.get()?.let {
                                       MultipartHelper.generatePart(
                                           context,
                                           Constants.IMAGE_CLOCK_IN,
                                           it
                                       )
                                   }, {
                                       labelClockInOutButton.set(getAppContext().getString(R.string.clock_out))
                                       isOpenCamera.value = false
                                       cbOnSuccess.invoke()
                                   }, {
                                       cbOnError.invoke(it)
                                   })
                           }
                           getAppContext().getString(R.string.clock_out) -> {
                               repository.clockOut(address,
                                   currentLocation.value?.latitude,
                                   currentLocation.value?.longitude, {
                                       labelClockInOutButton.set(getAppContext().getString(R.string.clock_in))
                                       cbOnSuccess.invoke()
                                   }, {
                                       cbOnError.invoke(it)
                                   })
                           }
                       }
                   }
               } else {
                   cbOnError.invoke(getAppContext().resources.getString(R.string.failed_fetch_your_location))
               }
       }

    }

}
