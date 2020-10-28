package com.worka.eroyal.extensions

import android.database.DataSetObserver
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.google.android.material.textfield.TextInputLayout
import com.worka.eroyal.R
import com.worka.eroyal.data.report.Stock
import com.worka.eroyal.feature.common.GenericAppAdapter
import com.worka.eroyal.feature.common.MarketShareSuggestionAdapter
import pl.pawelkleczkowski.customgauge.CustomGauge
import java.text.DecimalFormat


@BindingAdapter("width")
fun ConstraintLayout.setWidth(width: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = width
    this.layoutParams = layoutParams
}

@BindingAdapter("width")
fun LinearLayout.setWidth(width: Int) {
    val layoutParams = this.layoutParams
    layoutParams.width = width
    this.layoutParams = layoutParams
}

@BindingAdapter(value = ["completedTask", "totalTask"])
fun CustomGauge.setTotalTask(completedTask: Int, totalTask: Int) {
    this.endValue = totalTask
    this.value = completedTask
}

@BindingAdapter("errorText")
fun TextInputLayout.setError(errorText: String?) {
    error = errorText
}

@BindingAdapter("vertAdapter")
fun RecyclerView.setVerticalAdapter(adapter: GenericAppAdapter<*>){
    layoutManager = LinearLayoutManager(getAppContext(), LinearLayoutManager.VERTICAL, false)
    this.adapter = adapter
    adapter.notifyDataSetChanged()
}

@BindingAdapter("horizAdapter")
fun RecyclerView.setHorizontalAdapter(adapter: GenericAppAdapter<*>){
    layoutManager = LinearLayoutManager(getAppContext(), LinearLayoutManager.HORIZONTAL, false)
    this.adapter = adapter
    adapter.notifyDataSetChanged()
}

@BindingAdapter("gridAdapter", "column")
fun RecyclerView.setHorizontalAdapter(adapter: GenericAppAdapter<*>, column: Int){
    if (column != 0) {
        layoutManager = GridLayoutManager(getAppContext(), column)
        this.adapter = adapter
        adapter.notifyDataSetChanged()
    }
}

@BindingAdapter(value = ["textWatcher", "adapterClick"])
fun AutoCompleteTextView.setTextWatcher(textWatcher: TextWatcher, adapterClick: AdapterView.OnItemClickListener) {
    textWatcher?.let {
        addTextChangedListener(textWatcher)
        onItemClickListener = adapterClick
    }
}

@BindingAdapter("autoCompleteAdapter")
fun AutoCompleteTextView.setAutoCompleteAdapter(autoCompleteAdapter: MarketShareSuggestionAdapter?){
    setAdapter(autoCompleteAdapter)
    autoCompleteAdapter?.registerDataSetObserver(object: DataSetObserver(){
        override fun onChanged() {
            super.onChanged()
            if (autoCompleteAdapter.brands.isNotEmpty()) {
                showDropDown()
            }
        }
    })
}

@BindingAdapter(value = ["datas", "colors"])
fun LineChart.setDatas(datas: Array<Array<Int>?>?, colors: Array<Int?>?) {
    val entries = arrayListOf<ArrayList<Entry>>()
    val labels = arrayListOf<String>()
    datas?.forEachIndexed { i, dataArray ->
        val entryArray = arrayListOf<Entry>()
        dataArray?.forEachIndexed { index, data ->
            entryArray.add(Entry(index.toFloat(), data.toFloat()))
            if (i == 0) {
                labels.add((index + 1).toString())
            }
        }
        entries.add(entryArray)
    }
    val lineDataSets = arrayListOf<ILineDataSet>()

    entries.forEachIndexed{ index, entryArray ->
        val lineDataSet = LineDataSet(entryArray, "")
        lineDataSet.lineWidth = 4f
        lineDataSet.setDrawValues(false)
        lineDataSet.setDrawCircles(false)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.1f
        lineDataSet.circleHoleColor
        lineDataSet.color = ContextCompat.getColor(getAppContext(), colors?.get(index)?:0)

        lineDataSets.add(lineDataSet)
    }

    val lineData = LineData(lineDataSets)

    xAxis.valueFormatter = IndexAxisValueFormatter(labels)

    description.isEnabled = false
    legend.isEnabled = false
    axisRight.isEnabled = false
    axisLeft.isEnabled = false
    isAutoScaleMinMaxEnabled = true
    xAxis.setDrawGridLines(false)
    xAxis.textSize = 6f
    xAxis.setLabelCount(datas?.get(0)?.size ?: 0, false)
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    setPinchZoom(false)
    data = lineData
    invalidate()
}

@BindingAdapter(value = ["currentMonth", "prevMonth", "target"])
fun LineChart.setDatas(currentMonth: ArrayList<Long>, prevMonth: ArrayList<Long>, target: ArrayList<Long>?){
    val entriesCurrent = arrayListOf<Entry>()
    val entriesPrev = arrayListOf<Entry>()
    val entriesTarget = arrayListOf<Entry>()
    val lineDataSets = arrayListOf<ILineDataSet>()

    target?.let {

        target.forEachIndexed { index, data ->
            entriesTarget.add(Entry(index.toFloat(), data.toFloat()))
        }

        val lineDataSetTarget = LineDataSet(entriesTarget, "").apply {
            lineWidth = 4f
            setDrawValues(false)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.1f
            circleHoleColor
            color = ContextCompat.getColor(getAppContext(), R.color.colorOrange)
        }

        lineDataSets.add(lineDataSetTarget)
    }

    currentMonth.forEachIndexed{index, data ->
        entriesCurrent.add(Entry(index.toFloat(), data.toFloat()))
    }

    val lineDataSetCurrent = LineDataSet(entriesCurrent, "").apply {
        lineWidth = 4f
        setDrawValues(false)
        setDrawCircles(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        cubicIntensity = 0.1f
        color = ContextCompat.getColor(getAppContext(), R.color.colorGreen)
    }

    prevMonth.forEachIndexed{index, data ->
        entriesPrev.add(Entry(index.toFloat(), data.toFloat()))
    }

    val lineDataSetPrev = LineDataSet(entriesPrev, "").apply {
        lineWidth = 4f
        setDrawValues(false)
        setDrawCircles(false)
        mode = LineDataSet.Mode.CUBIC_BEZIER
        cubicIntensity = 0.1f
        circleHoleColor
        color = ContextCompat.getColor(getAppContext(), R.color.colorGrey)
    }

    lineDataSets.add(lineDataSetPrev)
    lineDataSets.add(lineDataSetCurrent)

    val lineData = LineData(lineDataSets)

    description.isEnabled = false
    legend.isEnabled = false
    axisRight.isEnabled = false
    axisRight.granularity = 1f
    axisLeft.isEnabled = false
    axisLeft.granularity = 1f
    xAxis.isEnabled = false
    isAutoScaleMinMaxEnabled = true
    setPinchZoom(false)
    data = lineData
    invalidate()
}

@BindingAdapter("expanded")
fun ConstraintLayout.setExpand(expanded: Boolean){
    val btnArrow = findViewById<ImageView>(R.id.btn_expand)
    val layoutExpand = findViewById<ConstraintLayout>(R.id.layout_area_expand)
    when (layoutExpand.visibility){
        View.GONE -> {
            btnArrow.animate().rotation(180f).setDuration(200).start()
        }
        else -> {
            btnArrow.animate().rotation(0f).setDuration(200).start()
        }
    }
}

@BindingAdapter("stock")
fun BarChart.setBarData(stock: Stock){
    val bars = arrayListOf<BarEntry>()
    bars.add(BarEntry(0f, (stock.mattress?: 0).toFloat()))
    bars.add(BarEntry(1f, (stock.divan?: 0).toFloat()))
    bars.add(BarEntry(2f, (stock.headboard?: 0).toFloat()))
    bars.add(BarEntry(3f, (stock.sorong?: 0).toFloat()))

    val barDataSets = arrayListOf<IBarDataSet>()
    barDataSets.add(BarDataSet(bars, "").apply { color = ContextCompat.getColor(context, R.color.colorAccent) })
    val barData = BarData(barDataSets).apply {
        barWidth = 0.7f
        setDrawValues(true)
        setValueTextSize(10f)
        setValueFormatter(DecimalValueFormatter())
        setValueTypeface(ResourcesCompat.getFont(context, R.font.neutrif_pro_semibold))
    }

    val labels = with(context) {
        arrayOf(
            getString(R.string.mattress), getString(R.string.divan), getString(R.string.headboard), getString(R.string.sorong)
        )
    }
    xAxis.valueFormatter = IndexAxisValueFormatter(labels)
    xAxis.isGranularityEnabled = true
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.setDrawGridLines(false)
    xAxis.textSize = 10f
    xAxis.setAvoidFirstLastClipping(false)
    xAxis.setCenterAxisLabels(false)
    xAxis.typeface = ResourcesCompat.getFont(context, R.font.neutrif_pro_semibold)

    description.isEnabled = false
    legend.isEnabled = false
    axisRight.isEnabled = false
    axisLeft.isEnabled = false
    setPinchZoom(false)
    setScaleEnabled(false)

    data = barData

    isClickable = false
    invalidate()
    resetZoom()
}

@BindingAdapter("prices", "colors")
fun PieChart.setBarData(prices: ArrayList<Int>, colors: ArrayList<Int>){
    val entries = arrayListOf<PieEntry>()
    prices.forEachIndexed { index, i ->
        entries.add(PieEntry(i.toFloat()))
    }

    val pieDataSet = PieDataSet(entries, "").apply { setColors(colors) }
    pieDataSet.setDrawValues(false)
    pieDataSet.isHighlightEnabled = true

    val pieData = PieData(pieDataSet)

    description.isEnabled = false
    legend.isEnabled = false
    isRotationEnabled = false
    setDrawEntryLabels(false)

    pieData.setValueFormatter(PercentFormatter(this))

    data = pieData

    isClickable = false
    if (prices.isNotEmpty()) {
        highlightValue(0f, 0)
    }
    invalidate()
}

internal class DecimalValueFormatter : ValueFormatter() {
    private val mFormat: DecimalFormat
    override fun getFormattedValue(value: Float): String {
        return mFormat.format(value)
    }

    init {
        mFormat = DecimalFormat("#")
    }
}
