package com.sundram.weplay.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.ui.theme.TextMain
import com.sundram.weplay.ui.theme.TextMuted

@Composable
fun TopBackBar(
    title: String,
    modifier: Modifier = Modifier,
    sub: String = "",
    onBack: (() -> Unit)? = null,
    rightContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (onBack != null) {
            RoundIconButton(
                painter = painterResource(R.drawable.ic_back),
                onClick = onBack,
                contentDescription = "Back",
                size = 44.dp,
                iconSize = 20.dp,
                tint = TextMain,
                modifier = Modifier.defaultMinSize(minWidth = 48.dp, minHeight = 48.dp),
            )
            Spacer(Modifier.width(8.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextMain)
            if (sub.isNotBlank()) {
                Text(sub, fontSize = 11.sp, color = TextMuted)
            }
        }
        if (rightContent != null) {
            Spacer(Modifier.width(8.dp))
            rightContent()
        }
    }
}

@Composable
fun TopBackBar(
    nav: NavHostController,
    title: String,
    sub: String = "",
    modifier: Modifier = Modifier,
    rightContent: (@Composable () -> Unit)? = null,
) {
    TopBackBar(
        title = title,
        modifier = modifier,
        sub = sub,
        onBack = { nav.popBackStack() },
        rightContent = rightContent,
    )
}

