package com.lalifa.main.ext

import com.lalifa.ext.Config
import com.lalifa.extension.gone
import com.lalifa.extension.invisible
import com.lalifa.extension.visible
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.net.URL

class MUtils {
    companion object{
        fun loadSvg(view: SVGAImageView,path:String, callback: () -> Unit){
            view.visible()
            Config.parser.decodeFromURL(
                URL(Config.FILE_PATH + path),
                object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        val drawable = SVGADrawable(videoItem)
                        view.setImageDrawable(drawable)
                        view.startAnimation()
                        view.gone()
                        callback()
                    }
                    override fun onError() {
                    }
                })
        }
    }
}