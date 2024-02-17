package com.example.kashio.ui.TimeUi

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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.kashio.data.local.database.Time
import com.example.kashio.data.local.database.Title
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@Composable
fun Statistics(modifier: Modifier, blockList: List<Time>, rangeStart: String, rangeEnd: String, savedTitles: List<Title>){
    val range = daysBetween(rangeStart, rangeEnd)
    val totalTime = range * 1440 // total number of minutes in the range

    var emptyTime = totalTime // default value of empty which will be changed

    var titles: MutableMap<String, Pair<Int, String>> = mutableMapOf()


    for(block in blockList) {
        if (block.uid != -1) {
            emptyTime -= getLength(endTime = block.endTime, startTime = block.startTime)
        }

        savedTitles.forEach{title ->
            if(block.color == title.color && block.title == title.title) {
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
            else {
               // if (block.uid != -1) {
              //      otherTime += getLength(endTime = block.endTime, startTime = block.startTime)
              //  }
            }
        }
       // if(savedTitles.isEmpty() && block.uid != -1){
       //     otherTime += getLength(endTime = block.endTime, startTime = block.startTime)
      //  }
    }


    val donutChartData = titles.map { DonutChartData(amount = it.value.first.toFloat(), color = Color(android.graphics.Color.parseColor(it.value.second)), label = it.key) } + DonutChartData(label = "Empty time", amount = emptyTime.toFloat(), color = Color.White) + DonutChartData(label = "Non saved", amount = (1440 - emptyTime - titles.toList().sumOf { it.second.first }).toFloat(), color = Color.Gray)



    Box(modifier = modifier.fillMaxSize(1f) , contentAlignment = Alignment.TopCenter) {

        Card(shape = MaterialTheme.shapes.medium, modifier = Modifier.padding(horizontal = 15.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)) {
            // Create the pie chart
            Column(horizontalAlignment = Alignment.CenterHorizontally) {


                Spacer(modifier = Modifier.height(15.dp))

                DonutChart(data = donutChartData)

                Spacer(modifier = Modifier.height(15.dp))

                val lengthColumn = (titles.toList() + Pair("Non saved", Pair(1440 - emptyTime - titles.toList().sumOf { it.second.first }, Color.Gray.toHex())) + Pair("Empty time", Pair(emptyTime.toInt(), Color.White.toHex()))).sortedBy { 1440 - it.second.first.toFloat()}


                LazyColumn {
                    items(lengthColumn) {
                        if (it.second.first != 0) {
                            StatisticRow(
                                title = it.first,
                                length = it.second.first.toString(),
                                color = it.second.second
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
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

@Composable fun StatisticRow(title: String, length: String, color: String) {
    if(length.toInt() != 0) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)) {
            Text(text = title, color = Color(android.graphics.Color.parseColor(color))) // title
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
            Text("${hours}${minutes}", color = Color.Gray)
        }
    }
}

@Composable
fun DonutChart(
    data: List<DonutChartData>,
    strokeWidth: Dp = 60.dp,
) {
    val total = (data.sumOf { it.amount.toDouble() }).toFloat()
    var startAngle = 0f

    Box(
        modifier = Modifier
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
