package com.rti.charisma.api.model

import com.contentful.java.cda.CDAAsset
import com.contentful.java.cda.CDAEntry
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.stream.Collectors
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
//data class HomePage(val title: String, val contentBody: String, val heroImage:  List<Asset>, val videos: List<Asset>)
data class HomePage(val textContent: MutableMap<String, String>, val assets: MutableMap<String, List<Asset>>)


@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Asset(val id: String, val title: String, val url: String, val mimeType: String)


fun converter(entry: CDAEntry) : HomePage  {
    val homePage = HomePage(mutableMapOf(), mutableMapOf() )
    entry.rawFields().keys.map {
        if (entry.getField<Any>(it) is String) {
            homePage.textContent[it] = entry.getField<String>(it)
        }
        if (entry.getField<Any>(it) is CDAAsset) {
            homePage.assets[it] = toAsset(entry.getField<List<CDAAsset>>(it))
        }

        if (entry.getField<Any>(it) is ArrayList<*>) {
            homePage.assets[it] = toAsset(entry.getField<List<CDAAsset>>(it))
        }
    }
    return homePage
}

private fun toAsset(cdaAssets: List<CDAAsset>) : List<Asset> {
    return cdaAssets
        .stream().filter { entry -> entry is CDAAsset }
        .map { entry -> mapToAsset(entry as CDAAsset) }
        .collect(Collectors.toList())
}

fun mapToAsset(entry: CDAAsset) : Asset{
    return Asset(entry.id(), entry.title(), "https:"+entry.url(), entry.mimeType())
}