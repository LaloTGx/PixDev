package com.lalo.pixdev.ui.components.colorcomp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.lalo.pixdev.R
import com.lalo.pixdev.viewmodel.ColorViewModel
import com.lalo.pixdev.ui.components.generalscomp.PageIndicators

@Composable
fun ColorHarmoniesSection(
    viewModel: ColorViewModel,
    selectedColor: Color
) {
    val context = LocalContext.current

    val harmonyTypes = remember {
        listOf(
            Pair("Complementary", R.string.harmony_complementary),
            Pair("Triadic", R.string.harmony_triadic),
            Pair("Analogous", R.string.harmony_analogous),
            Pair("Monochromatic", R.string.harmony_monochromatic),
            Pair("Compound", R.string.harmony_compound)
        )
    }

    val harmonyPagerState = rememberPagerState(initialPage = 0) {
        harmonyTypes.size
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = harmonyPagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { pageIndex ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = context.getString(harmonyTypes[pageIndex].second),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val currentHarmonyType = harmonyTypes[pageIndex].first
                val harmonyColors: List<Color> by remember(selectedColor, currentHarmonyType) {
                    derivedStateOf {
                        when (currentHarmonyType) {
                            "Complementary" -> listOf(selectedColor, viewModel.getComplementaryColor(selectedColor))
                            "Triadic" -> listOf(selectedColor) + viewModel.getTriadicColors(selectedColor)
                            "Analogous" -> listOf(selectedColor) + viewModel.getAnalogousColors(selectedColor)
                            "Monochromatic" -> listOf(selectedColor) + viewModel.getMonochromaticColors(selectedColor)
                            "Compound" -> listOf(selectedColor) + viewModel.getCompoundColors(selectedColor)
                            else -> emptyList()
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    harmonyColors.forEach { color ->
                        ColorBlock(
                            color = color,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .heightIn(max = 120.dp)
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        PageIndicators(pagerState = harmonyPagerState, pageCount = harmonyTypes.size)
    }
}