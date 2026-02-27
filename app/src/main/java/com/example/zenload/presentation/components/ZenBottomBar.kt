package com.example.zenload.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy

@Composable
fun ZenBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (Any) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .height(72.dp)
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTabs.items.forEach { item ->
                val selected = currentDestination?.hierarchy?.any {
                    it.route?.toString()?.contains(item.route::class.simpleName ?: "") == true
                } == true

                ZenBottomBarItem(
                    item = item,
                    selected = selected,
                    onClick = { onNavigate(item.route) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ZenBottomBarItem(
    item: BottomNavItem,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeColor = MaterialTheme.colorScheme.primary
    val inactiveColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)

    val color by animateColorAsState(
        targetValue = if (selected) activeColor else inactiveColor,
        animationSpec = tween(300),
        label = "tab_color_anim"
    )

    Column(
        modifier = modifier
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            )
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val iconModifier = Modifier
            .size(26.dp)
            .drawBehind {
                if (selected) {
                    drawIntoCanvas { canvas ->
                        val paint = Paint().asFrameworkPaint().apply {
                            this.color = activeColor.copy(alpha = 0.3f).toArgb()
                            this.setShadowLayer(40f, 0f, 0f, activeColor.toArgb())
                        }
                        canvas.nativeCanvas.drawCircle(
                            center.x, center.y, 35f, paint
                        )
                    }
                }
            }

        Icon(
            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
            contentDescription = item.title,
            tint = color,
            modifier = iconModifier
        )

        Text(
            text = item.title,
            color = color,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}