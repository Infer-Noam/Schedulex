package com.example.kashio.ui.TimeUi

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kashio.R
import com.example.kashio.data.local.database.Title
import com.example.kashio.ui.HSLA
import com.github.skydoves.colorpicker.compose.AlphaTile
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.reflect.KSuspendFunction0

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
fun TitleListScreen(
    modifier: Modifier, list: List<Title>, insertTitle: (String, String, Int) -> Unit, onNewTitle: (Boolean) -> Unit, newTitle: Boolean, deleteTitle: (String, String, Int) -> Unit,
    onPlay: (Int) -> Unit, onStop: (String, String) -> Unit, getPlayId: KSuspendFunction0<Int>, sortIndex: HSLA, onSortIndex: (HSLA) -> Unit
){//, onShowSnackbar: (Boolean) -> Unit){

    var playId by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        playId = getPlayId()
    }




    val options = listOf("Delete", "Edit")

    var selectedTitle by rememberSaveable { mutableStateOf("") }
    var selectedColor by rememberSaveable { mutableStateOf("") }
    var selectedId by rememberSaveable { mutableIntStateOf(-1) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.Top),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(list) {
                Card(modifier = Modifier.fillMaxWidth(0.9f),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
                ) {
                    var moreVert by rememberSaveable {
                        mutableStateOf(false)
                    }

                        Row (verticalAlignment = Alignment.CenterVertically, modifier = Modifier
                            .padding(horizontal = 5.dp)
                            .padding(5.dp)
                            .fillMaxWidth() ,horizontalArrangement = Arrangement.SpaceBetween){

                            IconButton(onClick = {
                                if(playId != it.uid){ playId = it.uid; onPlay(it.uid) } else{ onStop(it.title, it.color);playId = (-1) ;onPlay(it.uid) } }) {

                                if(playId != it.uid) {
                                    Icon(Icons.Filled.PlayArrow,
                                    contentDescription = "end a live time block"
                                )} else{
                                    Icon(painter =
                                    painterResource(R.drawable.baseline_stop_24),
                                        contentDescription = "end a live time block"
                                    )}
                                }

                                Text(it.title, fontSize = 24.sp, modifier = Modifier.weight(1f), maxLines = 1)

                            Spacer(modifier = Modifier.width(5.dp))

                            AlphaTile(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                selectedColor = Color(android.graphics.Color.parseColor(it.color))
                            )

                            IconButton(onClick = { moreVert = true
                                selectedTitle = it.title
                                selectedColor = it.color
                                selectedId = it.uid}) {
                                Icon(Icons.Filled.MoreVert, contentDescription = "options")

                                MaterialTheme(
                                    shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                                ) {
                                    DropdownMenu(expanded = moreVert, onDismissRequest = { moreVert = false
                                        selectedTitle = ""
                                        selectedColor = ""
                                        selectedId = -1}) {
                                        options.forEach { selectionOption ->
                                            DropdownMenuItem(onClick = {
                                                moreVert = false
                                                when (selectionOption) {
                                                    "Edit" -> {
                                                        onNewTitle(true)
                                                    }

                                                    "Delete" -> {
                                                        deleteTitle(selectedTitle, selectedColor, selectedId)
                                                    }
                                                }
                                            }, text = { Text(text = selectionOption) })
                                        }
                                    }
                                }

                            }
                        }

                }
            }

        }





        FloatingActionButton(
            onClick = {
                onNewTitle(true)
            },

            modifier = modifier
                .align(Alignment.BottomEnd)
                .padding(15.dp),
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Floating action button")
        }

        if(newTitle)
            {
                NewTitleDialog(onNewTitle = onNewTitle, onSave = insertTitle, oldTitle = selectedTitle, oldColor = selectedColor, oldId = selectedId, sortIndex = sortIndex, onSortIndex = onSortIndex)//, onShowSnackbar = onShowSnackbar)
            }
        }
}

@Composable
fun NewTitleDialog(
    oldTitle: String, oldColor: String, oldId: Int,
    onNewTitle: (Boolean) -> Unit, onSave: (String, String, Int) -> Unit, sortIndex: HSLA, onSortIndex: (HSLA) -> Unit
){// onShowSnackbar: (Boolean) -> Unit) {
    var title by rememberSaveable { mutableStateOf(oldTitle) }

    var color by rememberSaveable { mutableStateOf(if(oldColor != ""){oldColor}else{"#FFFFFFFF"}) } // default color

    var showToast by rememberSaveable { mutableStateOf(false) }

    Box(modifier = Modifier.padding(20.dp)) {

        AlertDialog(
            onDismissRequest = { onNewTitle(false) },
            title = { Text("Enter new title") },
            text = {
                Column (horizontalAlignment = Alignment.CenterHorizontally,  modifier = Modifier.heightIn(max = LocalConfiguration.current.screenHeightDp.dp * 0.6f)) {

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                painter = painterResource(
                                    R.drawable.title_input_icon
                                ), contentDescription = "title field icon"
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { title = "" },
                                content = {
                                    Icon(Icons.Filled.Clear, contentDescription = "clear the title")
                                },
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                    Row (modifier = Modifier.clickable{onSortIndex(sortIndex)}.fillMaxWidth().padding(horizontal = 5.dp)) {
                     Text(text = "Colors sorted by ${when(sortIndex){HSLA.H -> "hue" ; HSLA.S -> "saturation"; HSLA.L -> "lightness"; HSLA.A -> "alphabetical order"}}" )

                        Spacer(modifier = Modifier.weight(1f))

                    Icon(
                        painter = painterResource(R.drawable.round_sort_24),
                        contentDescription = "sort color by"
                    ) 
                }
                    Spacer(modifier = Modifier.height(10.dp))

                    ColorPicker(selectedColor = Color(android.graphics.Color.parseColor(if(color != ""){color}else{"#FFFFFFFF"})), onColorSelected = { color = it.toHex()}, sortIndex = sortIndex)


                    Spacer(modifier = Modifier.height(15.dp))

                   // Text(text = color, color = Color(android.graphics.Color.parseColor(color)))

                }


            },
            confirmButton = {
                TextButton(onClick = {
                    if (title.isNotBlank()) {
                        onSave(title, color, oldId)
                        onNewTitle(false)
                    } else {
                        showToast = true
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { onNewTitle(false) }) {
                    Text("Dismiss")
                }
            })
    }
    if(showToast){
        Toast.makeText(LocalContext.current, "Title must not be blank", Toast.LENGTH_SHORT).show()
        showToast = false
    }
}

@Composable
fun ColorPicker(selectedColor: Color?, onColorSelected: (Color) -> Unit, sortIndex: HSLA) {
    LazyColumn {

        val colors = listOf(      "DarkBlue" to Color(0xFF00008B),
            "DarkCyan" to Color(0xFF008B8B),
            "DarkMagenta" to Color(0xFF8B008B),
            "DarkOliveGreen" to Color(0xFF556B2F),
            "DarkOrange" to Color(0xFFFF8C00),
            "DarkOrchid" to Color(0xFF9932CC),
            "DarkGoldenRod" to Color(0xFFB8860B),
            "DarkGray" to Color(0xFFA9A9A9),
            "DarkGreen" to Color(0xFF006400),
            "DarkKhaki" to Color(0xFFBDB76B),
            "Brown" to Color(0xFFA52A2A),
            "BurlyWood" to Color(0xFFDEB887),
            "CadetBlue" to Color(0xFF5F9EA0),
            "Chartreuse" to Color(0xFF7FFF00),
            "Chocolate" to Color(0xFFD2691E),
            "Coral" to Color(0xFFFF7F50),
            "CornflowerBlue" to Color(0xFF6495ED),
            "Cornsilk" to Color(0xFFFFF8DC),
            "Crimson" to Color(0xFFDC143C),
            "Cyan" to Color(0xFF00FFFF),
            "Tomato" to Color(0xFFF44336),
            "Tangerine" to Color(0xFFFF9800),
            "Banana" to Color(0xFFFFEB3B),
            "Basil" to Color(0xFF4CAF50),
            "Sage" to Color(0xFF8BC34A),
            "Peacock" to Color(0xFF009688),
            "Blueberry" to Color(0xFF3F51B5),
            "Lavender" to Color(0xFF9C27B0),
            "Grape" to Color(0xFF673AB7),
            "Flamingo" to Color(0xFFE91E63),
            "Graphite" to Color(0xFF607D8B),
            "Crimson" to Color(0xFFDC143C),
            "Gold" to Color(0xFFFFD700),
            "ForestGreen" to Color(0xFF228B22),
            "Lime" to Color(0xFFADFF2F),
            "Teal" to Color(0xFF008080),
            "Indigo" to Color(0xFF4B0082),
            "Maroon" to Color(0xFF800000),
            "Olive" to Color(0xFF808000),
            "Sienna" to Color(0xFFA0522D),
            "SlateBlue" to Color(0xFF6A5ACD),
            "SlateGray" to Color(0xFF708090),
            "LemonChiffon" to Color(0xFFFFFACD),
            "LightCyan" to Color(0xFFE0FFFF),
            "Wheat" to Color(0xFFF5DEB3),
            "Bisque" to Color(0xFFFFE4C4),
            "BlanchedAlmond" to Color(0xFFFFEBCD),
            "LightGoldenRodYellow" to Color(0xFFFAFAD2),
            "LightGray" to Color(0xFFD3D3D3),
            "LightGreen" to Color(0xFF90EE90),
            "LightPink" to Color(0xFFFFB6C1),
            "LightSalmon" to Color(0xFFFFA07A),
            "LightSeaGreen" to Color(0xFF20B2AA),
            "LightSkyBlue" to Color(0xFF87CEFA),
            "LightSlateGray" to Color(0xFF778899),
            "LightSteelBlue" to Color(0xFFB0C4DE),
            "Linen" to Color(0xFFFFFAF0),
            "OldLace" to Color(0xFFFDF5E6),
            "Beige" to Color(0xFFF5F5DC),
            "MintCream" to Color(0xFFF5FFFA),
            "HoneyDew" to Color(0xFFF0FFF0),
            "AliceBlue" to Color(0xFFF0F8FF),
            "GhostWhite" to Color(0xFFF8F8FF),
            "Khaki" to Color(0xFFF0E68C),
            "Lavender" to Color(0xFFE6E6FA),
            "LavenderBlush" to Color(0xFFFFF0F5),
            "LawnGreen" to Color(0xFF7CFC00),
            "SeaShell" to Color(0xFFFFF5EE),
            "WhiteSmoke" to Color(0xFFF5F5F5),
            "Snow" to Color(0xFFFFFAFA),
            "Aqua" to Color(0xFF00FFFF),
            "Aquamarine" to Color(0xFF7FFFD4),
            "Azure" to Color(0xFFF0FFFF),
            "AntiqueWhite" to Color(0xFFFAEBD7),
            "Ivory" to Color(0xFFFFFFF0)).sortedBy {
            val hsl = rgbToHsl(it.second)

            when (sortIndex) {
                HSLA.H -> hsl[0].toString()
                HSLA.S -> hsl[1].toString()
                HSLA.L -> hsl[2].toString()
                HSLA.A -> it.first // Sort by name
            }
        }

            items(colors) { (colorName, color) ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clickable { onColorSelected(color) }
                    .padding(horizontal = 5.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = colorName,
                    color = Color.Black,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Canvas(modifier = Modifier.size(24.dp)) {
                    if (color == selectedColor) {
                        drawCircle(color = color, radius = size.minDimension / 2f)
                    } else {
                        drawCircle(
                            color = color,
                            radius = size.minDimension / 2f,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}


