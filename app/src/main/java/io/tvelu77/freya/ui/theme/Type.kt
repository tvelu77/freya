package io.tvelu77.freya.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val Nunito = FontFamily(
  Font(io.tvelu77.freya.R.font.nunito, FontWeight.Normal),
  Font(io.tvelu77.freya.R.font.nunito_medium, FontWeight.Medium),
  Font(io.tvelu77.freya.R.font.nunito_semibold, FontWeight.SemiBold),
  Font(io.tvelu77.freya.R.font.nunito_bold, FontWeight.Bold)
)

val FreyaTypography = Typography(
  headlineLarge = TextStyle(
    fontFamily = Nunito, fontWeight = FontWeight.Bold,
    fontSize = 28.sp, lineHeight = 36.sp
  ),
  headlineMedium = TextStyle(
    fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
    fontSize = 22.sp, lineHeight = 30.sp
  ),
  titleLarge = TextStyle(
    fontFamily = Nunito, fontWeight = FontWeight.SemiBold,
    fontSize = 18.sp, lineHeight = 26.sp
  ),
  titleMedium = TextStyle(
    fontFamily = Nunito, fontWeight = FontWeight.Medium,
    fontSize = 16.sp, lineHeight = 24.sp
  ),
  bodyLarge = TextStyle(
    fontFamily = Nunito, fontWeight = FontWeight.Normal,
    fontSize = 16.sp, lineHeight = 24.sp
  ),
  bodyMedium = TextStyle(
    fontFamily = Nunito, fontWeight = FontWeight.Normal,
    fontSize = 14.sp, lineHeight = 22.sp
  ),
  labelSmall = TextStyle(
    fontFamily = Nunito, fontWeight = FontWeight.Medium,
    fontSize = 11.sp, lineHeight = 16.sp
  )
)