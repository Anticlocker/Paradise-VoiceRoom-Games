package com.sundram.weplay.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.sundram.weplay.R
import com.sundram.weplay.data.Demo
import com.sundram.weplay.ui.components.CoverThumb
import com.sundram.weplay.ui.nav.Routes
import com.sundram.weplay.ui.theme.CardWhite
import com.sundram.weplay.ui.theme.CrystalBlue
import com.sundram.weplay.ui.theme.CyanBrand
import com.sundram.weplay.ui.theme.ChipBg
import com.sundram.weplay.ui.theme.PageWhite
import com.sundram.weplay.ui.theme.Stroke
import com.sundram.weplay.ui.theme.TextMain
import com.sundram.weplay.ui.theme.TextMuted

@Composable
fun SearchScreen(nav: NavHostController) {
    var query by remember { mutableStateOf("") }
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    val matchingGames = remember(query) {
        if (query.isBlank()) emptyList()
        else Demo.games.filter {
            it.title.contains(query, ignoreCase = true) || it.desc.contains(query, ignoreCase = true)
        }
    }

    val matchingRooms = remember(query) {
        if (query.isBlank()) emptyList()
        else Demo.rooms.filter {
            it.title.contains(query, ignoreCase = true) || it.host.contains(query, ignoreCase = true) || it.tag.contains(query, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PageWhite)
            .statusBarsPadding(),
    ) {
        // Search bar header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable { nav.popBackStack() },
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "Back",
                    tint = TextMain,
                    modifier = Modifier.size(20.dp),
                )
            }

            Spacer(Modifier.width(8.dp))

            // Search input field
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clip(RoundedCornerShape(21.dp))
                    .background(CardWhite)
                    .border(1.dp, Stroke, RoundedCornerShape(21.dp))
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_search),
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = TextMain, fontSize = 14.sp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            Text("Search games, rooms, tags…", color = TextMuted, fontSize = 14.sp)
                        }
                        innerTextField()
                    },
                )
                if (query.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(ChipBg)
                            .clickable { query = "" },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Clear",
                            tint = Color.White,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }
        }

        // Search results
        if (query.isBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.sundram.weplay.R.drawable.ic_search),
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Type to discover games & voice rooms", color = TextMuted, fontSize = 14.sp)
                }
            }
        } else if (matchingGames.isEmpty() && matchingRooms.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Icon(
                        painter = androidx.compose.ui.res.painterResource(id = com.sundram.weplay.R.drawable.ic_search),
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("No results for \"$query\"", color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    Text("Try searching for 'Ludo', 'Spy', or 'Casual'", color = TextMuted, fontSize = 13.sp)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (matchingGames.isNotEmpty()) {
                    item {
                        Text("Games (${matchingGames.size})", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    items(matchingGames, key = { "game_${it.id}" }) { game ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardWhite)
                                .border(1.dp, Stroke, RoundedCornerShape(12.dp))
                                .clickable {
                                    when (game.id) {
                                        "ludo" -> nav.navigate(Routes.LUDO)
                                        "spy" -> nav.navigate(Routes.SPY)
                                        else -> nav.navigate(Routes.ALL_GAMES)
                                    }
                                }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CoverThumb(game.title, Modifier.size(46.dp), corner = 10)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(game.title, color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Text(game.desc, color = TextMuted, fontSize = 12.sp)
                            }
                            Text(
                                if (game.playable) "Play" else "Catalog",
                                color = CrystalBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                            )
                        }
                    }
                }

                if (matchingRooms.isNotEmpty()) {
                    item {
                        Text("Voice rooms (${matchingRooms.size})", color = TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    items(matchingRooms, key = { "room_${it.id}" }) { room ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(CardWhite)
                                .border(1.dp, Stroke, RoundedCornerShape(12.dp))
                                .clickable { nav.navigate(Routes.roomRoute(room.id)) }
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CoverThumb(room.title, Modifier.size(46.dp), corner = 10)
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(room.title, color = TextMain, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                                Text("Host: ${room.host} · ${room.members} listeners", color = TextMuted, fontSize = 12.sp)
                            }
                            Text("Join", color = CrystalBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
