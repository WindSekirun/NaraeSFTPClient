package com.github.windsekirun.naraesftp.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.view.animation.ScaleAnimation
import com.github.windsekirun.naraesftp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.gordonwong.materialsheetfab.AnimatedFab


/**
 * NaraeSFTPClient
 * Class: SheetFab
 * Created by Pyxis on 2019-03-14.
 *
 * Description:
 */

class SheetFab(context: Context, attrs: AttributeSet) : FloatingActionButton(context, attrs), AnimatedFab {

    override fun show() {
        super.show()
        show(0f, 0f)
    }

    override fun show(translationX: Float, translationY: Float) {
        setTranslation(translationX, translationY)

        // Only use scale animation if FAB is hidden
        if (visibility != View.VISIBLE) {
            // Pivots indicate where the animation begins from
            val pivotX = pivotX + translationX
            val pivotY = pivotY + translationY

            val anim: ScaleAnimation
            // If pivots are 0, that means the FAB hasn't been drawn yet so just use the
            // center of the FAB
            anim = if (pivotX == 0f || pivotY == 0f) {
                ScaleAnimation(
                    0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )
            } else {
                ScaleAnimation(0f, 1f, 0f, 1f, pivotX, pivotY)
            }

            // Animate FAB expanding
            anim.duration = FAB_ANIM_DURATION
            anim.interpolator = getInterpolator()
            startAnimation(anim)
        }
        super.show()
    }

    override fun hide() {
        super.hide()
        if (visibility == View.VISIBLE) {
            // Pivots indicate where the animation begins from
            val pivotX = pivotX + translationX
            val pivotY = pivotY + translationY

            // Animate FAB shrinking
            val anim = ScaleAnimation(1f, 0f, 1f, 0f, pivotX, pivotY)
            anim.duration = FAB_ANIM_DURATION
            anim.interpolator = getInterpolator()
            startAnimation(anim)
        }
        super.hide()
    }

    private fun setTranslation(translationX: Float, translationY: Float) {
        animate().setInterpolator(getInterpolator()).setDuration(FAB_ANIM_DURATION)
            .translationX(translationX).translationY(translationY)
    }

    private fun getInterpolator(): Interpolator {
        return AnimationUtils.loadInterpolator(context, R.interpolator.msf_interpolator)
    }

    companion object {
        const val FAB_ANIM_DURATION = 200L
    }
}