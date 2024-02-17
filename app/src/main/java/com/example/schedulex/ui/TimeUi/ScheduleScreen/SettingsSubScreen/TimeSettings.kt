package com.example.schedulex.ui.TimeUi.ScheduleScreen.SettingsSubScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.schedulex.ui.TimeUi.NavigationType

@Composable
fun SettingsPage(fontSize: String, fontType: String, darkTheme: Boolean, onChange: (darkTheme: Boolean, fontSize: String, fontType: String) -> Unit // the settings and a lambda that save them
, navigationType: NavigationType
) {

    var TempFontType by rememberSaveable { mutableStateOf(fontType) } // font type setting

    var TempFontSize by rememberSaveable { mutableStateOf(fontSize) } // font size setting

    var TempDarkTheme by rememberSaveable { mutableStateOf(darkTheme) } // dark theme setting


if(navigationType == NavigationType.BOTTOM_NAVIGATION) { // when the navigation type is bottom navigation

    LazyColumn { // organize the settings in a lazy column

        item { // item in a lazy column
            FontSizeSetting( // a function for a font size
                modifier = Modifier,
                title = "Font Size",
                options = listOf("Small", "Medium", "Large"), // options to choose from
                selectedOption = TempFontSize, // initial value
                onOptionSelected = { // when option is selected update the settings
                    TempFontSize = it; onChange(
                    TempDarkTheme,
                    TempFontSize,
                    TempFontType
                )
                },
                fontSize = when (TempFontSize) { // convert font size to actual sizes in sp
                    "Small" -> 14.sp; "Medium" -> 16.sp; else -> 18.sp
                }
            )
        }
        item {// item in a lazy column
            FontSetting( // a function for a font type
                modifier = Modifier,
                title = "Font Type",
                options = listOf("Sans Serif", "Serif", "Monospace"), // options to choose from
                selectedOption = TempFontType, // initial value
                onOptionSelected = {
                    TempFontType = it; onChange( // when option is selected update the settings
                    TempDarkTheme,
                    TempFontSize,
                    TempFontType
                )
                },
                fontType = when (TempFontType) {  // convert font type to actual font types
                    "Sans Serif" -> FontFamily.SansSerif; "Serif" -> FontFamily.Serif; else -> FontFamily.Monospace
                }
            )
        }
        item {// item in a lazy column
            SettingOption( // a function for a setting option
                modifier = Modifier,
                title = "Dark Mode",
                subtitle = "Enable dark mode for better night time usage",
                isChecked = TempDarkTheme, // initial value
                onCheckedChange = {
                    TempDarkTheme = it;onChange( // when option is selected update the settings
                    TempDarkTheme,
                    TempFontSize,
                    TempFontType
                )
                }
            )
        }
    }
}
    else if(navigationType == NavigationType.NAVIGATION_RAIL || navigationType == NavigationType.PERMANENT_NAVIGATION_DRAWER){ // if the navigation type is rail or permanent drawer
    LazyVerticalGrid(modifier = Modifier.padding(8.dp),columns = GridCells.Fixed(2)) {// oregnise the items in a grid

        item { // item in a lazy column
            FontSizeSetting( // a function for a font size
                modifier = Modifier.fillMaxWidth(0.5f).padding(8.dp),
                title = "Font Size",
                options = listOf("Small", "Medium", "Large"),
                selectedOption = TempFontSize, // initial value
                onOptionSelected = {
                    TempFontSize = it; onChange( // when option is selected update the settings
                    TempDarkTheme,
                    TempFontSize,
                    TempFontType
                )
                },
                fontSize = when (TempFontSize) {
                    "Small" -> 14.sp; "Medium" -> 16.sp; else -> 18.sp
                }
            )
        }

        item { // item in a lazy column
            FontSetting(  // a function for a font type
                modifier = Modifier.fillMaxWidth(0.5f).padding(8.dp),
                title = "Font Type",
                options = listOf("Sans Serif", "Serif", "Monospace"), // replace with actual fonts
                selectedOption = TempFontType, // initial value
                onOptionSelected = {
                    TempFontType = it; onChange( // when option is selected update the settings
                    TempDarkTheme,
                    TempFontSize,
                    TempFontType
                )
                },
                fontType = when (TempFontType) {
                    "Sans Serif" -> FontFamily.SansSerif; "Serif" -> FontFamily.Serif; else -> FontFamily.Monospace
                }
            )
        }
        item { // item in a lazy column
            SettingOption(  // a function for a setting option
                modifier = Modifier.fillMaxWidth(0.5f).padding(10.dp),
                title = "Dark Mode",
                subtitle = "Enable dark mode for better night time usage",
                isChecked = TempDarkTheme, // initial value
                onCheckedChange = {
                    TempDarkTheme = it;onChange( // when option is selected update the settings
                    TempDarkTheme,
                    TempFontSize,
                    TempFontType
                )
                }
            )
        }
    }
    }
}

@Composable
fun SettingOption(modifier: Modifier = Modifier ,title: String, subtitle: String?, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 10.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = title, fontWeight = FontWeight.Bold)
            if (subtitle != null) {
                Text(modifier = Modifier.padding(start = 5.dp) ,text = subtitle)
            }
        }
        Checkbox(checked = isChecked, onCheckedChange = onCheckedChange)
    }
}




@Composable
fun FontSizeSetting(modifier: Modifier = Modifier, title: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit, fontSize: TextUnit) {
    Column(modifier = modifier.padding(horizontal = 10.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold)
        options.forEach { option ->
            Row(
                Modifier.selectable(
                    selected = (option == selectedOption),
                    onClick = { onOptionSelected(option) }
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (option == selectedOption), onClick = { onOptionSelected(option) })
                Text(text = option, modifier = Modifier.padding(start = 16.dp), fontSize = fontSize)
            }
        }
    }
}


@Composable
fun FontSetting(modifier: Modifier = Modifier, title: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit,  fontType: FontFamily) {
    Column(modifier = modifier.padding(horizontal = 10.dp)) {
        Text(text = title, fontWeight = FontWeight.Bold)
        options.forEach { option ->
            Row(
                Modifier.selectable(selected = (option == selectedOption), onClick = { onOptionSelected(option) }),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(selected = (option == selectedOption), onClick = { onOptionSelected(option) })
                Text(text = option, modifier = Modifier.padding(start = 16.dp), fontFamily = fontType)
            }
        }
    }
}
