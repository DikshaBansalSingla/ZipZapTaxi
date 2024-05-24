package com.zipzaptaxi.live.utils

import androidx.annotation.IntDef
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.zipzaptaxi.live.R


object FragmentUtil {


    const val TRANSITION_POP = 0
    const val TRANSITION_FADE_IN_OUT = 1
    const val TRANSITION_SLIDE_LEFT_RIGHT = 2
    const val TRANSITION_SLIDE_LEFT_RIGHT_WITHOUT_EXIT = 3
    const val TRANSITION_NONE = 4


    @IntDef(
        TRANSITION_POP,
        TRANSITION_FADE_IN_OUT,
        TRANSITION_SLIDE_LEFT_RIGHT,
        TRANSITION_SLIDE_LEFT_RIGHT_WITHOUT_EXIT,
        TRANSITION_NONE
    )
    internal annotation class FragmentAnimation

    fun replaceFragment(
        activity: AppCompatActivity?,
        fragment: Fragment,
        id: Int,
        addToBackStack: Boolean, @FragmentAnimation animationType: Int
    ) {

        if (null == activity)
            return

        val fragmentManager = activity.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()

        when (animationType) {
            TRANSITION_POP -> transaction.setCustomAnimations(
                R.anim.anim_enter,
                R.anim.anim_exit,
                R.anim.anim_pop_enter,
                R.anim.anim_pop_exit
            )
            TRANSITION_FADE_IN_OUT -> transaction.setCustomAnimations(
                R.anim.anim_frag_fade_in,
                R.anim.anim_frag_fade_out
            )
            TRANSITION_SLIDE_LEFT_RIGHT -> transaction.setCustomAnimations(
                R.anim.slide_in_from_rigth,
                R.anim.slide_out_to_left,
                R.anim.slide_in_from_left,
                R.anim.slide_out_to_right
            )
            TRANSITION_SLIDE_LEFT_RIGHT_WITHOUT_EXIT -> transaction.setCustomAnimations(R.anim.slide_in_from_rigth, 0)

            TRANSITION_NONE -> transaction.setCustomAnimations(0, 0)
            else -> transaction.setCustomAnimations(0, 0)
        }


        var backstackname = fragment.javaClass.canonicalName

        var fragmentPopped = fragmentManager.popBackStackImmediate(backstackname, 0)

        if (!fragmentPopped) {
            transaction.replace(id, fragment, fragment.javaClass.canonicalName)
        }

        if (addToBackStack)
            transaction.addToBackStack(fragment.javaClass.canonicalName)
            transaction.commit()


    }

}