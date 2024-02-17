package com.example.schedulex.ui.TimeUi.StatisticsScreen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedulex.data.local.database.Time
import com.example.schedulex.data.local.database.Title
import com.example.schedulex.ui.TimeUi.NavigationType
import com.example.schedulex.ui.TimeUi.getLength
import com.example.schedulex.ui.TimeUi.toHex
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun Statistics(modifier: Modifier, blockList: List<Time>, rangeStart: String, rangeEnd: String, savedTitles: List<Title>, fontSize: String, fontType: String, navigationType: NavigationType){
    val range = daysBetween(rangeStart, rangeEnd) // calculate the number of days between 2 dates
    val totalTime = range * 1440 // total number of minutes in the range * he number of days between 2 dates

    var emptyTime = totalTime // default value of empty which will be changed

    val titles: MutableMap<String, Pair<Int, String>> = mutableMapOf() // list of titles


    for(block in blockList) {
        if (block.uid != -1) {
            emptyTime -= getLength(endTime = block.endTime, startTime = block.startTime) // subtract the non empty time from empty time
        }

        savedTitles.forEach{title ->
            if(block.color == title.color && block.title == title.title) { // if the block's title match a title from the save titles it add the title to the statistics
                if (titles.containsKey(title.title)) {
                    titles[title.title] = Pair(
                        titles[title.title]?.first ?: (0 + getLength(
                            endTime = block.endTime,
                            startTime = block.startTime
                        )), block.color)
                } else {
                    titles[title.title] = Pair(getLength(endTime = block.endTime, startTime = block.startTime), block.color)
                }
            }
        }
    }


    val donutChartData = titles.map { DonutChartData(amount = it.value.first.toFloat(), color = Color(android.graphics.Color.parseColor(it.value.second)), label = it.key) } + DonutChartData(label = "Empty time", amount = emptyTime.toFloat(), color = Color.White) + DonutChartData(label = "Non saved", amount = (1440 - emptyTime - titles.toList().sumOf { it.second.first }).toFloat(), color = Color.Gray) // the data of the pai chart


    if(navigationType == NavigationType.BOTTOM_NAVIGATION){ // if the navigation type is bottom navigation

        Box(modifier = modifier.fillMaxSize(1f) , contentAlignment = Alignment.TopCenter) {
            Column {

                Card(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .padding(bottom = 15.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    // Create the pie chart
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {


                        Spacer(modifier = Modifier.height(15.dp))

                        DonutChart(data = donutChartData) // pai chart

                        Spacer(modifier = Modifier.height(15.dp))

                        val lengthColumn = (titles.toList() + Pair(
                            "None saved",
                            Pair(
                                1440 - emptyTime - titles.toList().sumOf { it.second.first },
                                Color.Gray.toHex()
                            )
                        ) + Pair(
                            "Empty time",
                            Pair(emptyTime.toInt(), Color.White.toHex())
                        )).sortedBy { 1440 - it.second.first.toFloat() }


                        LazyColumn(modifier = Modifier.padding(bottom = 5.dp)) {// each item is a row which display the length, title and color
                            items(lengthColumn) {
                                if (it.second.first != 0) {
                                    StatisticRow( //
                                        title = it.first,
                                        length = it.second.first.toString(),
                                        color = it.second.second,
                                        fontType = fontType,
                                        fontSize = fontSize
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    else { // if the navigation type is not bottom navigation
        Box(modifier = modifier.fillMaxSize(1f), contentAlignment = Alignment.TopCenter) {
            Column {

                Card(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp)
                        .padding(vertical = 5.dp),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    // Create the pie chart
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {

                        Row (modifier = Modifier .fillMaxSize()){
                            Spacer(modifier = Modifier.weight(0.5f))

                            DonutChart(data = donutChartData, modifier = Modifier.weight(10f).align(Alignment.CenterVertically)) // pai chart

                            val lengthColumn = (titles.toList() + Pair(
                                "None saved",
                                Pair(
                                    1440 - emptyTime - titles.toList().sumOf { it.second.first },
                                    Color.Gray.toHex()
                                )
                            ) + Pair(
                                "Empty time",
                                Pair(emptyTime.toInt(), Color.White.toHex())
                            )).sortedBy { 1440 - it.second.first.toFloat() }

                            Spacer(modifier = Modifier.weight(1f))

                            LazyColumn(
                                modifier = Modifier
                                    .padding(bottom = 5.dp)
                                    .weight(7f)
                            ) {
                                items(lengthColumn) { // each item is a row which display the length, title and color
                                    if (it.second.first != 0) {
                                        StatisticRow(
                                            title = it.first,
                                            length = it.second.first.toString(),
                                            color = it.second.second,
                                            fontType = fontType,
                                            fontSize = fontSize
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


fun daysBetween(date1: String, date2: String): Long {
    val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
    val firstDate = LocalDate.parse(date1, formatter)
    val secondDate = LocalDate.parse(date2, formatter)
    return ChronoUnit.DAYS.between(firstDate, secondDate) + 1
}

@Composable fun StatisticRow(title: String, length: String, color: String, fontType: String, fontSize: String) {
    if(length.toInt() != 0) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text(modifier = Modifier.weight(8f) ,text = title, color = Color(android.graphics.Color.parseColor(color)), maxLines = 1) // title
            // Text(text = color) // color

            Spacer(modifier = Modifier.weight(1f))

            val minutes = if (length.toInt() % 60 == 0 && (length.toInt() / 60 != 0)) {
                ""
            } else {
                if (length.toInt() / 60 == 0) {
                    (length.toInt() % 60).toString() + " min"
                } else {
                    (length.toInt() % 60).toString() + " m"
                }
            }
            val hours = if (length.toInt() / 60 == 0) {
                ""
            } else {
                if (length.toInt() % 60 == 0) {
                    if (length.toInt() / 60 > 1) {
                        (length.toInt() / 60).toString() + " hours "
                    } else {
                        (length.toInt() / 60).toString() + " hour "
                    }
                } else {
                    (length.toInt() / 60).toString() + " h "
                }
            }
            Text("${hours}${minutes}", color = Color.Gray, fontSize = when(fontSize){"Large" -> 18.sp; "Small" -> 14.sp; else -> 16.sp},
                fontFamily = when(fontType){"Sans Serif" -> FontFamily.SansSerif; "Monospace" -> FontFamily.Monospace; else -> FontFamily.SansSerif})
        }
    }
}

@Composable
fun DonutChart(
    modifier: Modifier = Modifier,
    data: List<DonutChartData>,
    strokeWidth: Dp = 60.dp
) {
    val total = (data.sumOf { it.amount.toDouble() }).toFloat()
    var startAngle = 0f

    Box(
        modifier = modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(1f)
            .padding(strokeWidth / 2),
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            data.forEach { dataPoint ->
                val sweep = 360f * (dataPoint.amount / total)
                drawArc(
                    color = dataPoint.color,
                    startAngle = startAngle,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx())
                )
                startAngle += sweep
            }
        }

        // Add labels
        /* Column(
            modifier = Modifier.align(Alignment.Center),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            data.forEach { dataPoint ->
                Text(
                    text = dataPoint.label,
                    color = dataPoint.color,
                    fontSize = 14.sp
                )
            }
        } */
    }
}


data class DonutChartData(val amount: Float, val color: Color, val label: String)
