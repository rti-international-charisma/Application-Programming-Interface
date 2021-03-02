package com.rti.charisma.api.model

import com.contentful.java.cda.CDAAsset
import com.contentful.java.cda.CDAEntry
import java.util.*
import java.util.stream.Collector
import java.util.stream.Collectors

data class HomePage(val title: String, val contentBody: String, val heroImage: List<Asset>, val videos: List<Asset>)
data class Asset(val id: String, val title: String, val url: String, val mimeType: String)


fun converter(entry: CDAEntry) : HomePage  {
    val title = entry.getField("title") as String
    val contentBody = entry.getField("contentBody") as String
    val heroImage = toAsset(entry.getField("heroImage") as List<CDAAsset>)
    val videos  =  toAsset(entry.getField("videos") as List<CDAAsset>)

    return HomePage(title, contentBody, heroImage, videos)
}

private fun toAsset(cdaAssets: List<CDAAsset>) : List<Asset> {
    return cdaAssets
        .stream()
        .map { entry -> Asset(entry.id(), entry.title(), entry.url(), entry.mimeType()) }
        .collect(Collectors.toList())

}