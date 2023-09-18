package com.mfarzan80.zoneswitch


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp


import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ZeroOneSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    animationDuration: Int = defaultAnimationDuration,
    thumbBounce: Boolean = true,
    trackElevation: Dp = 10.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: SwitchColors = SwitchDefaults.colors()
) {

    val easing = if (thumbBounce) bounceEasing else normalEasing

    val minBound = 0f
    val maxBound = with(LocalDensity.current) { ThumbPathLength.toPx() }
    val swipeableState =
        rememberSwipeableStateFor(checked, onCheckedChange ?: {}, animationSpec = TweenSpec(
            durationMillis = animationDuration,
            easing = CubicBezierEasing(0f, 0f, 0.0f, 1f)
        ))

    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    val toggleableModifier =
        if (onCheckedChange != null) {
            Modifier.toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                enabled = enabled,
                role = Role.Switch,
                interactionSource = interactionSource,
                indication = null
            )
        } else {
            Modifier
        }

    Box(
        modifier
            .then(toggleableModifier)
            .swipeable(
                state = swipeableState,
                anchors = mapOf(minBound to false, maxBound to true),
                thresholds = { _, _ -> FractionalThreshold(0.5f) },
                orientation = Orientation.Horizontal,
                enabled = enabled && onCheckedChange != null,
                reverseDirection = isRtl,
                interactionSource = interactionSource,
                resistance = null
            )
            .wrapContentSize(Alignment.Center)
            .padding(DefaultSwitchPadding)
            .requiredSize(SwitchWidth, SwitchHeight)
    ) {


        SwitchImpl(
            checked = checked,
            enabled = enabled,
            colors = colors,
            trackElevation = trackElevation,
            animationDuration = animationDuration,
            easing = easing,
            thumbValue = swipeableState.offset,
            interactionSource = interactionSource
        )

    }
}


@Composable
private fun BoxScope.SwitchImpl(
    checked: Boolean,
    enabled: Boolean,
    colors: SwitchColors,
    easing: Easing,
    trackElevation: Dp,
    animationDuration: Int,
    thumbValue: State<Float>,
    interactionSource: InteractionSource
) {
    val interactions = remember { mutableStateListOf<Interaction>() }



    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> interactions.add(interaction)
                is PressInteraction.Release -> interactions.remove(interaction.press)
                is PressInteraction.Cancel -> interactions.remove(interaction.press)
                is DragInteraction.Start -> interactions.add(interaction)
                is DragInteraction.Stop -> interactions.remove(interaction.start)
                is DragInteraction.Cancel -> interactions.remove(interaction.start)
            }
        }
    }


    val colorAnimation =
        TweenSpec<Color>(durationMillis = animationDuration / 2, easing = FastOutSlowInEasing)
    val trackColor = animateColorAsState(colors.trackColor(enabled, checked).value, colorAnimation)
    Canvas(
        Modifier.align(Alignment.Center).shadow(
            elevation = trackElevation,
            ambientColor = trackColor.value,
            clip = false,
            spotColor = trackColor.value,
        ).fillMaxSize()
    ) {
        drawTrack(trackColor.value, TrackWidth.toPx(), TrackStrokeWidth.toPx())
    }
    val thumbColor by colors.thumbColor(enabled, checked)
    val padding = if (checked) 7.dp else 0.dp
    val paddingAnim1 = animateDpAsState(
        padding, animationSpec = TweenSpec(
            durationMillis = animationDuration,
            easing = easing,
            delay = animationDuration * 15 / 100
        )
    )

    val finalPadding = paddingAnim1.value

    Spacer(
        Modifier
            .align(Alignment.CenterStart)
            .offset { IntOffset(thumbValue.value.roundToInt(), 0) }
            .indication(
                interactionSource = interactionSource,
                indication = null
            )
            .requiredSize(ThumbDiameter)
            .padding(horizontal = finalPadding)
            .border(width = ThumbBorderWidth, color = thumbColor, CircleShape)
    )
}

private fun DrawScope.drawTrack(trackColor: Color, trackWidth: Float, strokeWidth: Float) {
    val strokeRadius = strokeWidth / 2
    drawLine(
        trackColor,
        Offset(strokeRadius - ThumbHorizontalPadding.toPx(), center.y),
        Offset(ThumbHorizontalPadding.toPx() + trackWidth - strokeRadius, center.y),
        strokeWidth,
        StrokeCap.Round
    )
}

internal val TrackWidth = 44.dp
internal val TrackStrokeWidth = 28.dp
internal val ThumbDiameter = 20.dp
internal val ThumbHorizontalPadding = 4.dp
internal val ThumbBorderWidth = 6.dp

private val DefaultSwitchPadding = 2.dp
private val SwitchWidth = TrackWidth
private val SwitchHeight = ThumbDiameter
private val ThumbPathLength = TrackWidth - ThumbDiameter


@Composable
@OptIn(ExperimentalMaterialApi::class)
fun <T : Any> rememberSwipeableStateFor(
    value: T,
    onValueChange: (T) -> Unit,
    animationSpec: AnimationSpec<Float> = SwipeableDefaults.AnimationSpec
): SwipeableState<T> {
    val swipeableState = remember {
        SwipeableState(
            initialValue = value,
            animationSpec = animationSpec,
            confirmStateChange = { true }
        )
    }
    val forceAnimationCheck = remember { mutableStateOf(false) }
    LaunchedEffect(value, forceAnimationCheck.value) {
        if (value != swipeableState.currentValue) {
            swipeableState.animateTo(value)
        }
    }
    DisposableEffect(swipeableState.currentValue) {
        if (value != swipeableState.currentValue) {
            onValueChange(swipeableState.currentValue)
            forceAnimationCheck.value = !forceAnimationCheck.value
        }
        onDispose { }
    }
    return swipeableState
}

const val defaultAnimationDuration = 500
val bounceEasing: Easing = Easing { fraction ->
    val n1 = 7.5625f
    val d1 = 2.75f
    var newFraction = fraction

    return@Easing if (newFraction < 1f / d1) {
        n1 * newFraction * newFraction
    } else if (newFraction < 2f / d1) {
        newFraction -= 1.5f / d1
        n1 * newFraction * newFraction + 0.75f
    } else if (newFraction < 2.5f / d1) {
        newFraction -= 2.25f / d1
        n1 * newFraction * newFraction + 0.9375f
    } else {
        newFraction -= 2.625f / d1
        n1 * newFraction * newFraction + 0.984375f
    }
}

val normalEasing = FastOutSlowInEasing;
