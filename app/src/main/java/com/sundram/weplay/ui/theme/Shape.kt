package com.sundram.weplay.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Vybora / WePlay corner radius token system.
 *
 * Use these values with RoundedCornerShape(Radius.MD) or
 * clip(RoundedCornerShape(Radius.LG)) so corner radii are
 * globally consistent and easy to tune.
 *
 *  RadiusSM   =  8.dp  – chips, small badges, input fields
 *  RadiusMD   = 12.dp  – card corners
 *  RadiusLG   = 16.dp  – bottom sheets, dialogs, large cards
 *  RadiusXL   = 20.dp  – game banners, featured content
 *  RadiusXXL  = 24.dp  – modals, full-width panels
 *  RadiusPill = 50.dp  – pill buttons, coin capsules, tags
 */
object Radius {
    val SM   =  8.dp
    val MD   = 12.dp
    val LG   = 16.dp
    val XL   = 20.dp
    val XXL  = 24.dp
    val Pill = 50.dp
}

