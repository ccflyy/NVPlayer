/*
 *
 *   Copyright (c) 2019  NESP Technology Corporation. All rights reserved.
 *
 *   This program is free software; you can redistribute it and/or modify it
 *   under the terms and conditions of the GNU General Public License,
 *   version 2, as published by the Free Software Foundation.
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License.See the License for the specific language governing permission and
 *   limitations under the License.
 *
 *   If you have any questions or if you find a bug,
 *   please contact the author by email or ask for Issues.
 *
 *   Author:JinZhaolu <1756404649@qq.com>
 */

package com.nesp.nvplayer.cling.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import com.nesp.nvplayer.R

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-6 下午7:04
 * @project FishMovie
 **/
open class BaseDialog : DialogFragment() {

    var isShown = false
    var iBaseDialog: IBaseDialog? = null

    protected var contentView: View? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val window = dialog?.window
        window!!.requestFeature(Window.FEATURE_NO_TITLE)
        window.navigationBarColor = Color.BLACK
        super.onActivityCreated(savedInstanceState)
        window.setWindowAnimations(R.style.nesp_nvplayer_bottom_dialog_animation)
        window.setBackgroundDrawable(ColorDrawable(0X00000000))
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        dialog?.setOnShowListener {
            onShown(it)
        }
    }


    protected fun <T : View> findView(resId: Int): T {
        return contentView!!.findViewById(resId)
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        isShown = false
        iBaseDialog?.onDialogDismiss(dialog)

    }


    @CallSuper
    open fun onShown(dialog: DialogInterface) {
        isShown = true
        iBaseDialog?.onDialogShown(dialog)
    }

    protected fun showShortToast(msg: String) {
        val toast = Toast.makeText(context, null, Toast.LENGTH_SHORT)
        toast.setText(msg)
        toast.show()
    }

}