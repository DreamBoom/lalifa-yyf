package com.lalifa.main.ext

import com.lalifa.ext.Config
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.net.URL

class MUtils {
    companion object{
        fun loadSvg(view: SVGAImageView,path:String, callback: () -> Unit){
            Config.parser.decodeFromURL(
                URL(Config.FILE_PATH + path),
                object : SVGAParser.ParseCompletion {
                    override fun onComplete(videoItem: SVGAVideoEntity) {
                        val drawable = SVGADrawable(videoItem)
                        view.setImageDrawable(drawable)
                        view.startAnimation()
                        callback()
                    }
                    override fun onError() {
                    }
                })
        }
    }
}