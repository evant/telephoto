package me.saket.telephoto.viewport

import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.times
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.toOffset
import me.saket.telephoto.viewport.internal.discardFractionalParts

// todo: doc.
interface ZoomableContentLocation {

  // todo: doc.
  fun boundsIn(parent: Rect, direction: LayoutDirection): Rect

  companion object {
    @Stable
    fun fitToBoundsAndAlignedToCenter(size: Size?): ZoomableContentLocation {
      return when (size) {
        null -> Unspecified
        else -> RelativeContentLocation(
          size = size,
          scale = ContentScale.Fit,
          alignment = Alignment.Center,
        )
      }
    }

    @Stable
    internal val Unspecified = object : ZoomableContentLocation {
      override fun boundsIn(parent: Rect, direction: LayoutDirection) =
        error("location is unspecified")
    }
  }
}

internal data class RelativeContentLocation(
  val size: Size,
  val scale: ContentScale,
  val alignment: Alignment,
) : ZoomableContentLocation {
  override fun boundsIn(parent: Rect, direction: LayoutDirection): Rect {
    val scaleFactor = scale.computeScaleFactor(
      srcSize = size,
      dstSize = parent.size,
    )
    val scaledSize = size.times(scaleFactor)
    val alignedOffset = alignment.align(
      size = scaledSize.discardFractionalParts(),
      space = parent.size.discardFractionalParts(),
      layoutDirection = direction,
    )
    return Rect(
      offset = alignedOffset.toOffset(),
      size = scaledSize
    )
  }
}

internal val ZoomableContentLocation.isSpecified get() = this !== ZoomableContentLocation.Unspecified