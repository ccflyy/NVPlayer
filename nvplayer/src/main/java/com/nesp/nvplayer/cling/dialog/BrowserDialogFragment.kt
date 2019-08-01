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
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import android.widget.ProgressBar
import android.widget.TextView
import com.nesp.nvplayer.R
import android.webkit.WebView
import android.widget.ImageView

/**
 *
 *
 * @team NESP Technology
 * @author <a href="mailto:1756404649@qq.com">靳兆鲁 Email:1756404649@qq.com</a>
 * @time: Created 19-7-9 上午12:16
 * @project FishMovie
 **/
class BrowserDialogFragment(var url: String, var title: String = "") : BaseDialog() {

    private lateinit var webView: WebView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        contentView = inflater.inflate(R.layout.nvplayer_cling_dialog_browser, container, false)
        val tvTitle: TextView = findView(R.id.nvplayer_cling_dialog_browser_tv_title)
        val ivBack: ImageView = findView(R.id.nvplayer_cling_dialog_browser_iv_back)
        ivBack.setOnClickListener {
            dismiss()
        }
        val pbProgress: ProgressBar = findView(R.id.nvplayer_cling_dialog_pb_progress)
        webView = findView(R.id.nvplayer_cling_dialog_browser_wv)
        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress in 0 until 100) {
                    pbProgress.progress = newProgress
                    pbProgress.visibility = VISIBLE
                } else {
                    pbProgress.visibility = GONE
                }
            }

        }

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                tvTitle.text = if (title.isEmpty()) {
                    webView.title
                } else {
                    title
                }
            }
        }
        webView.loadUrl(url)

        return contentView
    }

    override fun onDismiss(dialog: DialogInterface) {
        webView.destroy()
        super.onDismiss(dialog)
    }

}