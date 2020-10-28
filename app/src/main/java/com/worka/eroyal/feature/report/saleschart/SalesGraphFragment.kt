package com.worka.eroyal.feature.report.saleschart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.LargeValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.worka.eroyal.R
import com.worka.eroyal.base.BaseFragment
import com.worka.eroyal.data.report.ChartReport
import com.worka.eroyal.databinding.FragmentSalesGraphBinding
import com.worka.eroyal.extensions.getAppContext
import com.worka.eroyal.feature.report.ReportViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


/**
 * Created by Ahmad Yunus <scootyunus@gmail.com>
 * on 22/06/20.
 */
class SalesGraphFragment : BaseFragment() {

    private lateinit var binding: FragmentSalesGraphBinding

    private val viewModel: ReportViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_graph, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.salesChartReportList.observe(this, Observer {
            setupGraph(it[0])
        })
    }

    private fun setupGraph(data: ChartReport) {
        val entriesCurrent = arrayListOf<Entry>()
        val entriesPrev = arrayListOf<Entry>()
        val entriesTarget = arrayListOf<Entry>()
        val lineDataSets = arrayListOf<ILineDataSet>()

        data.target.forEachIndexed { index, data ->
            entriesTarget.add(Entry(index.toFloat(), data.toFloat()))
        }

        val lineDataSetTarget = LineDataSet(entriesTarget, "").apply {
            lineWidth = 4f
            setDrawValues(true)
            setDrawCircles(false)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 9f
            valueTypeface = context?.let { ResourcesCompat.getFont(it, R.font.neutrif_pro_semibold) }
            cubicIntensity = 0.1f
            circleHoleColor
            color = ContextCompat.getColor(getAppContext(), R.color.colorOrange)
        }
        lineDataSetTarget.valueFormatter = LargeValueFormatter()
        lineDataSets.add(lineDataSetTarget)

        data.thisMonth.forEachIndexed { index, data ->
            entriesCurrent.add(Entry(index.toFloat(), data.toFloat()))
        }

        val lineDataSetCurrent = LineDataSet(entriesCurrent, "").apply {
            lineWidth = 4f
            setDrawValues(true)
            setDrawCircles(false)
            valueTextSize = 9f
            valueTypeface = context?.let { ResourcesCompat.getFont(it, R.font.neutrif_pro_semibold) }
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.1f
            color = ContextCompat.getColor(getAppContext(), R.color.colorGreen)
        }

        data.lastMonth.forEachIndexed { index, data ->
            entriesPrev.add(Entry(index.toFloat(), data.toFloat()))
        }

        val lineDataSetPrev = LineDataSet(entriesPrev, "").apply {
            lineWidth = 4f
            setDrawValues(true)
            setDrawCircles(false)
            valueTextSize = 9f
            valueTypeface = context?.let { ResourcesCompat.getFont(it, R.font.neutrif_pro_semibold) }
            mode = LineDataSet.Mode.CUBIC_BEZIER
            cubicIntensity = 0.1f
            circleHoleColor
            color = ContextCompat.getColor(getAppContext(), R.color.colorGrey)
        }
        lineDataSetPrev.valueFormatter = LargeValueFormatter()
        lineDataSetCurrent.valueFormatter = LargeValueFormatter()
        lineDataSets.add(lineDataSetPrev)
        lineDataSets.add(lineDataSetCurrent)

        val lineData = LineData(lineDataSets)

        with(binding.salesChart) {
            description.isEnabled = false
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisRight.granularity = 1f
            axisLeft.isEnabled = false
            axisLeft.granularity = 1f
            axisLeft.valueFormatter = LargeValueFormatter()
            xAxis.isEnabled = false
            isAutoScaleMinMaxEnabled = true
            setPinchZoom(false)
            this.data = lineData
            invalidate()
        }
    }
}
