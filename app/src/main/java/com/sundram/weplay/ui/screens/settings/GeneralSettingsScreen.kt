package com.sundram.weplay.ui.screens.settings

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.UserPrefs
import com.sundram.weplay.ui.screens.SubScreen
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.MintGreen
import com.sundram.weplay.ui.theme.TextMuted
import com.sundram.weplay.ui.theme.Gold

@Composable
fun GeneralSettingsScreen(nav: NavHostController) {
    val context = nav.context
    val prefs = remember { UserPrefs(context) }
    var showLanguage by remember { mutableStateOf(false) }

    val languageLabel = if (prefs.language == "hi") "Hindi" else "English"
    val localSize = remember { directorySize(context.filesDir) }

    SubScreen(nav, "General", "Language, storage and data") {
        SettingsGroup("App") {
            SettingsRow(
                icon = R.drawable.ic_comment,
                tint = CrystalBlue,
                title = "Language",
                subtitle = "Format used for dates, alerts and labels",
                value = languageLabel,
                onClick = { showLanguage = true },
            )
            SettingsRowDivider()
            SettingsRow(
                icon = R.drawable.ic_theme,
                tint = MintGreen,
                title = "Appearance",
                subtitle = "Unified dark celestial theme across Paradise",
                value = "Aurora Dark",
                enabled = false,
            )
            SettingsRowDivider()
            SettingsSwitchRow(
                icon = R.drawable.ic_bolt,
                tint = Gold,
                title = "Data saver",
                subtitle = "Lower audio bitrate on cellular data",
                checked = prefs.dataSaver,
            ) { prefs.dataSaver = it }
        }

        Spacer(Modifier.height(16.dp))

        SettingsGroup("Storage") {
            SettingsRow(
                icon = R.drawable.ic_bag,
                tint = MintGreen,
                title = "Local cache & data",
                subtitle = "Profile photos, logs and local preview state on this device",
                value = if (localSize > 0L) formatBytes(localSize) else "—",
                enabled = false,
            )
        }

        Spacer(Modifier.height(14.dp))

        Text(
            "Paradise uses the unified Aurora Paradise 3.0 dark palette so contrast and highlights stay consistent everywhere.",
            color = TextMuted,
            fontSize = 11.sp,
            lineHeight = 16.sp,
        )
    }

    if (showLanguage) {
        SettingsChoiceDialog(
            title = "Language",
            options = listOf(
                SettingsChoice("en", "English"),
                SettingsChoice("hi", "Hindi"),
            ),
            selected = prefs.language,
            onSelect = { prefs.language = it },
            onDismiss = { showLanguage = false },
        )
    }
}
