package com.sundram.weplay.data

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.sundram.weplay.R

/**
 * Definition of a premium VYBORA 2.5D character default avatar.
 */
data class DefaultAvatar(
    val id: String,
    val name: String,
    val title: String,
    val description: String,
    @DrawableRes val drawableRes: Int,
    val accentColor: Color,
    val frameGlowColor: Color,
    val category: String = "All",
)

/**
 * The 12 Original Animated Default Avatars for VYBORA.
 */
val ALL_DEFAULT_AVATARS: List<DefaultAvatar> = listOf(
    DefaultAvatar(
        id = "avatar_aurora_explorer",
        name = "Aurora Explorer",
        title = "Starlight Voyager",
        description = "Wanders neon horizons and celestial skies",
        drawableRes = R.drawable.vb_vr_avatar_demo_01,
        accentColor = Color(0xFF7C4DFF),
        frameGlowColor = Color(0xFFB388FF),
    ),
    DefaultAvatar(
        id = "avatar_lunar_artist",
        name = "Lunar Artist",
        title = "Moonlight Painter",
        description = "Draws dreams with stardust in the night",
        drawableRes = R.drawable.vb_vr_avatar_demo_02,
        accentColor = Color(0xFF00B0FF),
        frameGlowColor = Color(0xFF80D8FF),
    ),
    DefaultAvatar(
        id = "avatar_cyber_gamer",
        name = "Cyber Gamer",
        title = "Pixel Champion",
        description = "Master of party games and quick reflexes",
        drawableRes = R.drawable.vb_vr_avatar_demo_03,
        accentColor = Color(0xFF00E676),
        frameGlowColor = Color(0xFFB9F6CA),
    ),
    DefaultAvatar(
        id = "avatar_royal_dreamer",
        name = "Royal Dreamer",
        title = "Crown Sovereign",
        description = "Exudes royal charm across every lounge",
        drawableRes = R.drawable.vb_vr_avatar_demo_04,
        accentColor = Color(0xFFFFD600),
        frameGlowColor = Color(0xFFFFEA00),
    ),
    DefaultAvatar(
        id = "avatar_star_captain",
        name = "Star Captain",
        title = "Cosmic Navigator",
        description = "Guides friends through uncharted fun",
        drawableRes = R.drawable.vb_vr_avatar_demo_05,
        accentColor = Color(0xFFFF4081),
        frameGlowColor = Color(0xFFFF80AB),
    ),
    DefaultAvatar(
        id = "avatar_crystal_guardian",
        name = "Crystal Guardian",
        title = "Prism Sentinel",
        description = "Protector of good vibes and harmony",
        drawableRes = R.drawable.vb_vr_avatar_demo_06,
        accentColor = Color(0xFF00E5FF),
        frameGlowColor = Color(0xFF84FFFF),
    ),
    DefaultAvatar(
        id = "avatar_neon_musician",
        name = "Neon Musician",
        title = "Beat Crafter",
        description = "Fills voice lounges with energetic rhythm",
        drawableRes = R.drawable.vb_character_tricksters_cafe,
        accentColor = Color(0xFFFF6D00),
        frameGlowColor = Color(0xFFFFAB40),
    ),
    DefaultAvatar(
        id = "avatar_celestial_wanderer",
        name = "Celestial Wanderer",
        title = "Astral Nomad",
        description = "Silent observer beneath nebula clouds",
        drawableRes = R.drawable.vb_character_space_werewolf,
        accentColor = Color(0xFF651FFF),
        frameGlowColor = Color(0xFFB388FF),
    ),
    DefaultAvatar(
        id = "avatar_mystic_fox",
        name = "Mystic Fox Mascot",
        title = "Clever Trickster",
        description = "Playful spirit skilled at Hide & Seek",
        drawableRes = R.drawable.vb_character_hide_and_seek,
        accentColor = Color(0xFFFF5252),
        frameGlowColor = Color(0xFFFF8A80),
    ),
    DefaultAvatar(
        id = "avatar_cosmic_bunny",
        name = "Cosmic Bunny Mascot",
        title = "Paint Magician",
        description = "Coloring the world with joyful surprises",
        drawableRes = R.drawable.vb_character_paint_to_hide,
        accentColor = Color(0xFFF50057),
        frameGlowColor = Color(0xFFFF4081),
    ),
    DefaultAvatar(
        id = "avatar_diamond_crown",
        name = "Diamond Crown Mascot",
        title = "Lucky Roller",
        description = "Brings fortune and high rolls in Ludo",
        drawableRes = R.drawable.vb_character_royal_dice,
        accentColor = Color(0xFFFFAB00),
        frameGlowColor = Color(0xFFFFD740),
    ),
    DefaultAvatar(
        id = "avatar_galaxy_dj",
        name = "Galaxy DJ Mascot",
        title = "Sonic Sleuth",
        description = "Uncovers mysteries with pulsating beats",
        drawableRes = R.drawable.vb_character_whos_the_spy,
        accentColor = Color(0xFF00B8D4),
        frameGlowColor = Color(0xFF18FFFF),
    ),
)

fun getAvatarById(id: String?): DefaultAvatar {
    return ALL_DEFAULT_AVATARS.firstOrNull { it.id == id } ?: ALL_DEFAULT_AVATARS[0]
}
