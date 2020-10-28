package com.worka.eroyal.feature.tasks

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Handler
import android.view.View
import androidx.core.net.toUri
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.google.android.gms.maps.model.LatLng
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseActivity
import com.worka.eroyal.base.BaseViewModel
import com.worka.eroyal.base.Constants.CLIENT_SIGNATURE_FILENAME
import com.worka.eroyal.base.Constants.EROYAL_FILENAME
import com.worka.eroyal.base.Constants.IMAGE_FOLLOW_UP_VISIT
import com.worka.eroyal.base.Constants.IMAGE_PROFILE_CUSTOMER
import com.worka.eroyal.base.Constants.IMAGE_TYPE
import com.worka.eroyal.base.Constants.LIMIT_RADIUS
import com.worka.eroyal.data.base.LocationData
import com.worka.eroyal.data.report.AreaReport
import com.worka.eroyal.data.tasks.Brand
import com.worka.eroyal.data.tasks.BrandImage
import com.worka.eroyal.data.tasks.CheckListData
import com.worka.eroyal.data.tasks.CheckOutRequest
import com.worka.eroyal.data.tasks.CheckoutData
import com.worka.eroyal.data.tasks.CheckoutImage
import com.worka.eroyal.data.tasks.Customer
import com.worka.eroyal.data.tasks.CustomerSubmitMarketShare
import com.worka.eroyal.data.tasks.ImageBrandData
import com.worka.eroyal.data.tasks.SubmitMarketShareRequest
import com.worka.eroyal.data.tasks.TaskResponseData
import com.worka.eroyal.data.visits.Branch
import com.worka.eroyal.extensions.convertToLocation
import com.worka.eroyal.extensions.formatThousandSeparator
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.extensions.getLocation
import com.worka.eroyal.extensions.prefixItsBeen
import com.worka.eroyal.extensions.taskByPrefix
import com.worka.eroyal.feature.common.SimpleViewModel
import com.worka.eroyal.feature.tasks.TasksViewModel.MediaType.BACKDROP
import com.worka.eroyal.feature.tasks.TasksViewModel.MediaType.BANNER
import com.worka.eroyal.feature.tasks.TasksViewModel.MediaType.BILLBOARD
import com.worka.eroyal.feature.tasks.TasksViewModel.MediaType.DOORPAINTING
import com.worka.eroyal.feature.tasks.TasksViewModel.MediaType.FABRIC_SIGN
import com.worka.eroyal.feature.tasks.checklist.MediaMarketShareViewModel
import com.worka.eroyal.repository.TasksRepository
import com.worka.eroyal.utils.DateUtils
import com.worka.eroyal.utils.ImagePickerHelper
import com.worka.eroyal.utils.MultipartHelper
import com.worka.eroyal.utils.compressImage
import com.worka.eroyal.utils.encodeToBase64
import com.worka.eroyal.utils.getDateFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.inject
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class TasksViewModel(var app: Application) : BaseViewModel(app) {
    private val repository: TasksRepository by inject()
    lateinit var navController: NavController

    var title = MutableLiveData<String>()
    val taskList = MutableLiveData<MutableList<TaskResponseData>>()
    var customerList = MutableLiveData<MutableList<Customer>>()
    var currentLocation = MutableLiveData<LocationData>()
    var mediaList = MutableLiveData<MutableList<MediaMarketShareViewModel>>()
    var marketShareSuggestionList = MutableLiveData<ArrayList<Brand>>()
    var visitImageUris = MutableLiveData<MutableList<Uri>>()
    var marketShareList = MutableLiveData<ArrayList<Brand>>()

    var date = MutableLiveData("")
    var dateDisplay = ObservableField<String>()
    val dropTaskReason = ObservableField("")
    var selectedTask = ObservableField<TaskResponseData>()
    var selectedCustomer = ObservableField<Customer>()
    var canCheckIn = ObservableField(false)
    var signatureUri = ObservableField<Uri>()
    var signatureBitmap = ObservableField<Bitmap>()
    var notes = ObservableField<String>("")
    var textTimer = ObservableField<String>("")
    var selectedMarketShareTemp = ObservableField<Brand?>()
    var canNextSignature = ObservableField(false)
    var btnAddMarketShareEnabled = ObservableField(false)

    var taskCompleted = 0
    var totalTask = 0
    var visitResultId = 0
    var selectedMarketShareForEdit: Brand? = null
    var imageUriTemp: Uri = Uri.EMPTY
    var isBackButtonPressedOnce = false

    var billBoards = ArrayList<BrandImage>()
    var backDrops = ArrayList<BrandImage>()
    var banners = ArrayList<BrandImage>()
    var doorPaintings = ArrayList<BrandImage>()
    var fabricSigns = ArrayList<BrandImage>()
    var marketShareListPayload = ArrayList<Brand>()

    var brandModifyTemp: BrandImage? = null
    var mediaModifyIdTemp: Int = 0
    var mediaTypeTemp: MediaType? = null
    var taskListEmptyLayout = ObservableField<Int>(View.GONE)
    var imgNoDataVisibility = ObservableField(View.VISIBLE)
    var stateEmptyText = ObservableField(R.string.there_is_nothing_here_yet)
    var emptySubText = ObservableField(R.string.enjoy_your_day_but_stay_on_your_toes)

    private var timer: Timer? = null

    // === Follow up visit variable ===
    var visitReason = ObservableField("")
    var urlImageFollowUpVisit = ObservableField(Uri.EMPTY)
    var followUpVisitFormVisibility = ObservableField(false)
    var formFollowUpVisitEnable = ObservableField(false)
    var customerName = ObservableField("")
    var customerAddress = ObservableField("")
    var customerCity = ObservableField("")
    var customerContactPerson = ObservableField("")
    var customerPhone = ObservableField("")
    var customerType = ObservableField("")
    var customerImageProfileUri = ObservableField(Uri.EMPTY)
    var canSubmitAddCustomer = ObservableField(false)
    var areaId: Int? = null
    var branchId: Int? = null
    var newCustomerLocation: LatLng? = null
    var areas: ArrayList<AreaReport>? = null
    var branches: ArrayList<Branch>? = null

    var visitReasonError = ObservableField("")
    var visitReasonErrorEnabled = ObservableField(false)

    fun setSelectedDate(selectedDate: Calendar) {
        date.value = selectedDate.getDateFormat(DateUtils.EEEE_D_MMM_YYYY)
        dateDisplay.set(selectedDate.getDateFormat(DateUtils.EEEE_D_MMM_YYYY))
    }


    fun getTasksData(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        val list = mutableListOf<TaskResponseData>()
        repository.getTasks( DateUtils.formateDate(date.value,
            DateUtils.EEEE_D_MMM_YYYY,
            DateUtils.DD_MM_YYYY), { response ->
            response?.data?.let { data -> list.addAll(data) }
            taskList.value = list
            response?.complete?.let { taskCompleted = it }
            response?.target?.let { totalTask = it }
            imgNoDataVisibility.set(View.VISIBLE)
            stateEmptyText.set(R.string.there_is_nothing_here_yet)
            emptySubText.set(R.string.enjoy_your_day_but_stay_on_your_toes)
            taskList.value?.let { if(it.isEmpty()) taskListEmptyLayout.set(View.VISIBLE) else taskListEmptyLayout.set(View.GONE) }
            cbOnSuccess.invoke()
        }, {
            imgNoDataVisibility.set(View.GONE)
            stateEmptyText.set(R.string.something_wrong)
            emptySubText.set(R.string.empty)
            taskListEmptyLayout.set(View.VISIBLE)
            cbOnError.invoke(it)
        })
    }

    fun searchMarketShare(keyword: String?, scope: String?) {
        repository.searchMarketShare(keyword, scope, { response ->
            val tempList = response?.brands
            marketShareList.value?.forEach { marketShare ->
                tempList?.remove(response.brands.find { brand -> brand.id == marketShare.brandId })
            }
            marketShareSuggestionList.value = tempList
        }, {})
    }

    fun validateButtonAddCustomer(){
        if (!customerImageProfileUri.get()?.equals(Uri.EMPTY)!! && !customerName.get().isNullOrBlank() && !customerAddress.get().isNullOrBlank() && !customerContactPerson.get().isNullOrBlank() &&
            !customerCity.get().isNullOrBlank() && !customerPhone.get().isNullOrBlank() && !customerType.get().isNullOrBlank() && newCustomerLocation != null && branchId != null  &&
            areaId != null && !visitReason.get().isNullOrBlank() && !urlImageFollowUpVisit.get()?.equals(Uri.EMPTY)!!){
            canSubmitAddCustomer.set(true)
        } else{
            canSubmitAddCustomer.set(false)
        }
    }

    fun validateButtonAddMarketShare(name: String?, price: String?){
        if (!name.isNullOrBlank() && !price.isNullOrBlank()){
            btnAddMarketShareEnabled.set(true)
        } else {
            btnAddMarketShareEnabled.set(false)
        }
    }

    fun setMediaTypeMarketShareData(context: Context?, onAddPhoto: (Int, BrandImage?) -> Unit, onUnselectedBrand: () -> Unit, onAddNewBrand: () -> Unit) {
        val list = arrayListOf<MediaMarketShareViewModel>()
        context?.let {
            list.add(MediaMarketShareViewModel(context, 0, context.getString(R.string.billboards), billBoards, { mediaId, brand ->
                        mediaTypeTemp = BILLBOARD
                        if (!brand?.id.isNullOrBlank()) {
                            onAddPhoto.invoke(mediaId, brand)
                        } else {
                            onUnselectedBrand.invoke()
                        }
                    }, { mediaId, brandImage, uri ->
                        removeImage(mediaId, brandImage, uri)
                    }, {
                        onAddNewBrand.invoke()
                    }
                ))
            list.add(MediaMarketShareViewModel(context, 1, context.getString(R.string.backdrops), backDrops, { mediaId, brand ->
                        mediaTypeTemp = BACKDROP
                        onAddPhoto.invoke(mediaId, brand)
                    }, { mediaId, brandImage, uri ->
                        removeImage(mediaId, brandImage, uri)
                    }, {
                        onAddNewBrand.invoke()
                    })
            )
            list.add(MediaMarketShareViewModel(context, 2, context.getString(R.string.banners), banners, { mediaId, brand ->
                        mediaTypeTemp = BANNER
                        onAddPhoto.invoke(mediaId, brand)
                    }, { mediaId, brandImage, uri ->
                        removeImage(mediaId, brandImage, uri)
                    }, {
                        onAddNewBrand.invoke()
                    })
            )
            list.add(MediaMarketShareViewModel(context, 3, context.getString(R.string.door_painting), doorPaintings, { mediaId, brand ->
                        mediaTypeTemp = DOORPAINTING
                        onAddPhoto.invoke(mediaId, brand)
                    }, { mediaId, brandImage, uri ->
                        removeImage(mediaId, brandImage, uri)
                    }, {
                        onAddNewBrand.invoke()
                    })
            )
            list.add(MediaMarketShareViewModel(context, 4, context.getString(R.string.fabric_sign), fabricSigns, { mediaId, brand ->
                        mediaTypeTemp = FABRIC_SIGN
                        onAddPhoto.invoke(mediaId, brand)
                    }, { mediaId, brandImage, uri ->
                        removeImage(mediaId, brandImage, uri)
                    }, {
                        onAddNewBrand.invoke()
                    })
            )
        }
        mediaList.value = list
    }

    fun dropTask(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit){
        repository.drop(dropTaskReason.get().toString(), selectedCustomer.get()?.id.toString(), selectedTask.get()?.id.toString(), {
            cbOnSuccess.invoke()
        },{
            cbOnError.invoke(it)
        })
    }

    private fun removeImage(mediaId: Int, brandImage: BrandImage?, uri: Uri?){
        mediaModifyIdTemp = mediaId
        brandModifyTemp = brandImage
        updateBrandImage(true, uri = uri)
    }

    fun updateBrandImage(isRemove: Boolean = false, uri: Uri?) {
        when(mediaTypeTemp){
            BILLBOARD -> {
                if (isRemove){
                    uri?.let { billBoards.find { it.index == brandModifyTemp?.index }?.imageList?.remove(it) }
                } else {
                    uri?.let { billBoards.find { it.index == brandModifyTemp?.index }?.imageList?.add(it) }
                }
                mediaList.value?.find { it.mediaId == mediaModifyIdTemp }?.brandListLiveData?.value = billBoards
            }
            BACKDROP -> {
                if (isRemove){
                    uri?.let { backDrops.find { it.index == brandModifyTemp?.index }?.imageList?.remove(it) }
                } else {
                    uri?.let { backDrops.find { it.index == brandModifyTemp?.index }?.imageList?.add(it) }
                }
                mediaList.value?.find { it.mediaId == mediaModifyIdTemp }?.brandListLiveData?.value = backDrops
            }
            BANNER -> {
                if (isRemove){
                    uri?.let { banners.find { it.index == brandModifyTemp?.index }?.imageList?.remove(it) }
                } else {
                    uri?.let { banners.find { it.index == brandModifyTemp?.index }?.imageList?.add(it) }
                }
                mediaList.value?.find { it.mediaId == mediaModifyIdTemp }?.brandListLiveData?.value = banners
            }
            DOORPAINTING -> {
                if (isRemove){
                    uri?.let { doorPaintings.find { it.index == brandModifyTemp?.index }?.imageList?.remove(it) }
                } else {
                    uri?.let { doorPaintings.find { it.index == brandModifyTemp?.index }?.imageList?.add(it) }
                }
                mediaList.value?.find { it.mediaId == mediaModifyIdTemp }?.brandListLiveData?.value = doorPaintings
            }
            FABRIC_SIGN -> {
                if (isRemove){
                    uri?.let { fabricSigns.find { it.index == brandModifyTemp?.index }?.imageList?.remove(it) }
                } else {
                    uri?.let { fabricSigns.find { it.index == brandModifyTemp?.index }?.imageList?.add(it) }
                }
                mediaList.value?.find { it.mediaId == mediaModifyIdTemp }?.brandListLiveData?.value = fabricSigns
            }
        }
    }

    fun searchCustomers(keyword: String?) {
        repository.searchCustomers(keyword, {
            customerList.value = it?.customers?.toMutableList()
        }, {})
    }

    fun getCustomerListData(onSelected: () -> Unit): MutableList<CustomerItemViewModel> {
        val list = mutableListOf<CustomerItemViewModel>()
        customerList.value?.forEach { customers ->
            list.add(CustomerItemViewModel(customers.id, customers.name, customers.address) {
                selectedCustomer.set(customerList.value?.find { customer -> customer.id == it })
                onSelected.invoke()
            })
        }
        return list
    }

    fun checkIn(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.checkIn(
            selectedTask.get()?.customer?.id.toString(),
            selectedTask.get()?.id.toString(), {
                visitResultId = it?.visitResult?.id ?: 0
                cbOnSuccess.invoke()
            }, {
                cbOnError.invoke(it)
            })
    }

    fun getAreaList(cbOnSuccess: () -> Unit){
        repository.getAreas({
            areas = it?.areas
            cbOnSuccess.invoke()
        },{})
    }

    fun getBranchList(cbOnSuccess: () -> Unit){
        repository.getBranches(areaId, {
            branches = it?.branches
            cbOnSuccess.invoke()
        },{})
    }

    fun addCustomer(context: Context, cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit){
        repository.addCustomer(customerName.get().orEmpty(), customerAddress.get().orEmpty(), customerCity.get().orEmpty(), customerContactPerson.get().orEmpty(), customerPhone.get().orEmpty(),
            customerType.get().orEmpty(), newCustomerLocation?.latitude.toString(), newCustomerLocation?.longitude.toString(), areaId.toString(), branchId.toString(), selectedMarketShareTemp.get()?.id.orEmpty(),
            customerImageProfileUri.get()?.let { context?.let { ctx ->
                MultipartHelper.generatePart(ctx, IMAGE_PROFILE_CUSTOMER, it) } }?: run { null },{
                selectedCustomer.set(it?.customer)
                cbOnSuccess.invoke()
            },{
                cbOnError.invoke(it)
            } )
    }

    fun savePhotoFollowUpVisit(file: File){
        val fileTemp = file.compressImage()
        urlImageFollowUpVisit.set(Uri.fromFile(fileTemp))

    }

    fun checkInFollowUpVisit(context: Context?, cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        if (urlImageFollowUpVisit.get() != null && urlImageFollowUpVisit.get() != Uri.EMPTY) {
            repository.checkInFollowUp(
                selectedCustomer.get()?.id.toString(),
                visitReason.get().orEmpty(), context?.let { ctx -> urlImageFollowUpVisit.get()?.let {
                    MultipartHelper.generatePart(ctx, IMAGE_FOLLOW_UP_VISIT, it)
                } }, {
                    visitResultId = it?.visitResult?.id ?: 0
                    cbOnSuccess.invoke()
                }, {
                    cbOnError.invoke(it)
                })
        } else {
            cbOnError.invoke(getAppContext().resources.getString(R.string.please_take_a_picture_for_visit))
        }
    }

    fun checkout(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val signature = signatureUri.get()?.let { File(it.path).encodeToBase64() }
            val visitImages = arrayListOf<CheckoutImage>()
            val checkListDatas = arrayListOf<CheckListData>()
            visitImageUris.value?.forEach { uri ->
                val image = CheckoutImage(
                    File(uri.path.orEmpty()).encodeToBase64(7),
                    EROYAL_FILENAME + System.currentTimeMillis() + ".jpg",
                    IMAGE_TYPE
                )
                visitImages.add(image)
            }

            billBoards.forEach {
                if (!it.imageList.isNullOrEmpty()) {
                    checkListDatas.add(createCheckListData(it, BILLBOARD))
                }
            }
            backDrops.forEach {
                if (!it.imageList.isNullOrEmpty()) {
                    checkListDatas.add(createCheckListData(it, BACKDROP))
                }
            }
            banners.forEach {
                checkListDatas.add(createCheckListData(it, BANNER))
            }
            doorPaintings.forEach {
                if (!it.imageList.isNullOrEmpty()) {
                    checkListDatas.add(createCheckListData(it, DOORPAINTING))
                }
            }
            fabricSigns.forEach {
                if (!it.imageList.isNullOrEmpty()) {
                    checkListDatas.add(createCheckListData(it, FABRIC_SIGN))
                }
            }

            val checkoutData = CheckoutData(
                signature,
                CLIENT_SIGNATURE_FILENAME + System.currentTimeMillis() + ".jpg",
                IMAGE_TYPE,
                notes.get(),
                currentLocation.value?.latitude,
                currentLocation.value?.longitude,
                visitImages,
                checkListDatas
            )

            val checkOutRequest = CheckOutRequest(checkoutData, visitResultId.toString())

            submitMarketShareList({
                marketShareListPayload.clear()
                repository.checkOut(checkOutRequest, visitResultId.toString(), {
                    cbOnSuccess.invoke()
                }, { errorCheckoutMessage ->
                    getExistingMarketShareCustomerList({
                        cbOnError.invoke(errorCheckoutMessage)
                    }, {
                        cbOnError.invoke(errorCheckoutMessage)
                    })
                })
            }, {
                cbOnError.invoke(it)
            })
        }

    }

    fun getTaskList(onClick: (Int?) -> Unit): List<TaskItemViewModel> {
        val list = mutableListOf<TaskItemViewModel>()
        taskList.value?.forEach {
            list.add(
                TaskItemViewModel(it.customer?.id, it.state, it.customer?.name, it.createdBy?.name.taskByPrefix(), checkoutTime =  it.checkOut) { id ->
                    selectedTask.set(taskList.value?.find { it.customer?.id == id })
                    selectedCustomer.set(taskList.value?.find { it.customer?.id == id }?.customer)
                    onClick.invoke(id)
                })
        }
        return list
    }

    fun getTaskProgressList(): List<TargetProgressItemViewModel> {
        val list = mutableListOf<TargetProgressItemViewModel>()
        list.add(TargetProgressItemViewModel(taskCompleted, totalTask))
       return list
    }

    fun checkCustomerDistance() : Boolean {
        return if (!selectedCustomer.get()?.latitude.isNullOrBlank() && !selectedCustomer.get()?.longitude.isNullOrBlank()) {
            val location = currentLocation.value?.convertToLocation()
            val taskLocation = selectedCustomer.get()?.getLocation()
            location?.let {
                canCheckIn.set(it.distanceTo(taskLocation) <= LIMIT_RADIUS)
                return it.distanceTo(taskLocation) <= LIMIT_RADIUS
            }
            false
        } else {
            canCheckIn.set(true)
            true
        }
    }

    fun startTimer(activity: BaseActivity) {
        if (textTimer.get().isNullOrBlank()) {
            timer = Timer()
            timer?.scheduleAtFixedRate(MyTimer(activity), 0, 1000)
        }
    }

    fun cancelTimer() {
        timer?.let {
            it.cancel()
            it.purge()
            Handler().postDelayed({ textTimer.set("") }, 1000)
        }
    }

    fun getExistingMarketShareCustomerList(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        repository.getExistingMarketShareCustomerList(selectedCustomer.get()?.id.toString(), { response ->
            marketShareList.value = response.marketShares
            cbOnSuccess.invoke()
        }, {
           cbOnError.invoke(it)
        })
    }

    fun updateMarketShareToList(brand: Brand) {
        viewModelScope.launch {
            selectedMarketShareForEdit?.let {
                val list = marketShareList.value?.let { it } ?: arrayListOf()
                val marketShareTempIndex = list.find { it.id == brand.id }?.index
                marketShareTempIndex?.let {
                    list.set(marketShareTempIndex.toInt(), brand)
                }
                addMarketShareToPayload(brand)
                marketShareList.value = list
                selectedMarketShareForEdit = null
            } ?: run {
                val list = marketShareList.value?.let { it } ?: arrayListOf()
                list.add(brand)
                addMarketShareToPayload(brand)
                marketShareList.value = list
            }
        }
    }

    fun addMarketShareToPayload(marketShare: Brand?) {
        marketShare?.let {
            val marketShareTemp = marketShareListPayload.find { it.name == marketShare.name }
            marketShareTemp?.apply {
                price = marketShare.price
                if (isDelete == true) {
                    isDelete = null
                }
            } ?: run {
                marketShareListPayload.add(marketShare)
            }
        }
    }

    fun getMarkerShareList(): List<MarketShareItemViewModel> {
        val list = mutableListOf<MarketShareItemViewModel>()
        marketShareList.value?.forEachIndexed { index, marketShare ->
            list.add(MarketShareItemViewModel(
                marketShare.id,
                (index + 1).toString().plus(". "),
                marketShare.name,
                marketShare.price?.toLong()?.formatThousandSeparator(), {
                    val tempList = marketShareList.value
                    selectedMarketShareForEdit = tempList?.get(it.toInt() - 1)
                    navController.navigate(R.id.action_marketShareListFragment_to_addEditMarketShareFragment)
                }, {
                    val tempList = marketShareList.value
                    val marketShareTemp = tempList?.get(it.toInt() - 1)
                    tempList?.removeAt(it.toInt() - 1)
                    marketShareList.value = tempList
                    removeMarketShare(marketShareTemp)
                })
            )
        }
        return list
    }

    private fun removeMarketShare(marketShare: Brand?) {
        marketShare?.let {
            marketShare.id?.let {
                marketShare.isDelete = true
                addMarketShareToPayload(marketShare)
            } ?: run {
                marketShareListPayload.remove(marketShare)
            }
        }
    }

    fun submitMarketShareList(cbOnSuccess: () -> Unit, cbOnError: (String?) -> Unit) {
        val customerData = CustomerSubmitMarketShare(marketShareListPayload)
        val request = SubmitMarketShareRequest(customerData)

        if (!request.customer?.marketshares.isNullOrEmpty()) {
            repository.submitMarketShareList(selectedCustomer.get()?.id.toString(), request, {
                cbOnSuccess.invoke()
            }, {
                cbOnError.invoke(it)
            })
        } else {
            cbOnSuccess.invoke()
        }
    }

    fun validateFollowUpVisit() {
        visitReasonErrorEnabled.set(false)
        visitReasonError.set("")
        canCheckIn.set(true)
        when {
            visitReason.get().isNullOrBlank() -> {
                visitReasonErrorEnabled.set(true)
                visitReasonError.set(getAppContext().getString(R.string.visit_reason_cant_be_empty))
                canCheckIn.set(false)
            }
        }
    }

    fun getVisitImageList(onAddPhoto: () -> Unit): MutableList<SimpleViewModel> {
        val list = mutableListOf<SimpleViewModel>()

        visitImageUris.value?.let {
            if (it.isNotEmpty()) {
                visitImageUris.value?.forEach { uri ->
                    list.add(VisitImageItemViewModel(uri) {
                        val currentUris = visitImageUris.value
                        currentUris?.remove(uri)
                        visitImageUris.value = currentUris
                    })
                }
            }
        }

        if (list.size < 6) {
            list.add(AddPhotoItemViewModel {
                onAddPhoto.invoke()
            })
        }
        return list
    }

    fun addVisitImage(uri: Uri) {
        if (visitImageUris.value.isNullOrEmpty()) {
            visitImageUris.value = mutableListOf()
        }
        val uris = visitImageUris.value
        uris?.add(uri)
        visitImageUris.value = uris
        imageUriTemp = Uri.EMPTY
    }

    private fun createCheckListData(marketShareImage: BrandImage, mediaType: MediaType): CheckListData {
        val images = arrayListOf<ImageBrandData>()
        marketShareImage.imageList?.forEach {
            val imageFile = File(it.path)
            images.add(ImageBrandData(EROYAL_FILENAME + System.currentTimeMillis() + ".jpg", imageFile.encodeToBase64(7), IMAGE_TYPE))
        }
        return CheckListData(marketShareImage.name, mediaType.value, marketShareImage.id, images)
    }

    fun saveBitmap(signature: Bitmap) {
        signatureBitmap.set(signature)
        try {
            val photo = ImagePickerHelper.createImageFile()
            signatureUri.set(photo.toUri())
            ImagePickerHelper.saveBitmapToJPG(signature, photo)
        } catch (e: IOException) {
            chuckerCollector.onError(TasksViewModel::class.java.simpleName, e)
            e.printStackTrace()
        }
    }

    private inner class MyTimer(val activity: BaseActivity) : TimerTask() {
        var hour: Int = 0
        var minutes: Int = 0
        var seconds: Int = 0
        var hoursText: String = ""
        var minutesText: String = ""
        var secondsText: String = ""
        override fun run() {
            activity.runOnUiThread {
                when {
                    minutes == 59 -> {
                        hour++
                        minutes = 0
                        seconds = 0
                        setTimer()
                    }
                    seconds == 59 -> {
                        minutes++
                        seconds = 0
                        setTimer()
                    }
                    else -> {
                        seconds++
                        setTimer()
                    }
                }
            }
        }

        fun setTimer() {
            if (hour > 0) {
                hoursText = if (hour.toString().length < 2) "0$hour" else "$hour"
                minutesText = if (minutes.toString().length < 2) "0$minutes" else "$minutes"
                secondsText = if (seconds.toString().length < 2) "0$seconds" else "$seconds"
                textTimer.set("$hoursText:$minutesText:$secondsText".prefixItsBeen())
            } else {
                minutesText = if (minutes.toString().length < 2) "0$minutes" else "$minutes"
                secondsText = if (seconds.toString().length < 2) "0$seconds" else "$seconds"
                textTimer.set("$minutesText:$secondsText".prefixItsBeen())
            }
        }
    }

    enum class MediaType(var value: String){
        BILLBOARD("billboards"), BACKDROP("backdrops"), BANNER("banners"), DOORPAINTING("doorpaintings"), FABRIC_SIGN("fabric signs")
    }
}
