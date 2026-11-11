package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CashGreen
import com.example.ui.theme.EasypaisaTeal
import com.example.ui.theme.JazzCashOrange

@Composable
fun PaymentBadge(method: String, modifier: Modifier = Modifier) {
  val (bgColor, textColor, label) = when (method.lowercase()) {
    "jazzcash" -> Triple(JazzCashOrange.copy(alpha = 0.15f), JazzCashOrange, "JazzCash")
    "easypaisa" -> Triple(EasypaisaTeal.copy(alpha = 0.15f), EasypaisaTeal, "Easypaisa")
    else -> Triple(CashGreen.copy(alpha = 0.15f), CashGreen, "Cash")
  }

  Surface(
    shape = RoundedCornerShape(12.dp),
    color = bgColor,
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
      Box(
        modifier = Modifier
          .size(6.dp)
          .background(textColor, CircleShape)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = label,
        color = textColor,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold
      )
    }
  }
}

@Composable
fun StatCard(
  title: String,
  value: String,
  subtitle: String? = null,
  containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
  contentColor: Color = MaterialTheme.colorScheme.onSurface,
  icon: @Composable (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = containerColor,
    tonalElevation = 2.dp,
    modifier = modifier
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(14.dp)
    ) {
      if (icon != null) {
        Box(
          modifier = Modifier
            .size(40.dp)
            .background(
              MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
              RoundedCornerShape(10.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          icon()
        }
        Spacer(modifier = Modifier.width(12.dp))
      }

      Column {
        Text(
          text = title,
          style = MaterialTheme.typography.labelMedium,
          color = contentColor.copy(alpha = 0.7f)
        )
        Text(
          text = value,
          style = MaterialTheme.typography.titleLarge,
          fontWeight = FontWeight.Bold,
          color = contentColor
        )
        if (subtitle != null) {
          Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = contentColor.copy(alpha = 0.6f)
          )
        }
      }
    }
  }
}

fun shareText(context: Context, text: String, title: String) {
  val sendIntent: Intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, text)
    type = "text/plain"
  }
  val shareIntent = Intent.createChooser(sendIntent, title)
  context.startActivity(shareIntent)
}

fun shareCsv(context: Context, csvContent: String, fileName: String) {
  val sendIntent: Intent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, csvContent)
    putExtra(Intent.EXTRA_TITLE, fileName)
    type = "text/plain"
  }
  val shareIntent = Intent.createChooser(sendIntent, "Export $fileName")
  context.startActivity(shareIntent)
}
