package com.rti.charisma.api.model

import com.contentful.java.cda.CDAAsset
import com.contentful.java.cda.CDAEntry
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.util.stream.Collectors
@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
data class HomePage(val title: String, val contentBody: String, val heroImage:  List<Asset>, val videos: List<Asset>)
//data class HomePage(val textContent: Map<String, String>, val assets: Map<String, List<Asset>>)

@JsonSerialize
@JsonIgnoreProperties(ignoreUnknown = true)
data class Asset(val id: String, val title: String, val url: String, val mimeType: String)


fun converter(entry: CDAEntry) : HomePage  {
    return HomePage(
        entry.getField("title") as String,
        entry.getField("contentBody") as String,
        toAsset(entry.getField("heroImage") as List<CDAAsset>),
        toAsset(entry.getField("videos") as List<CDAAsset>))
}

private fun toAsset(cdaAssets: List<CDAAsset>) : List<Asset> {
    return cdaAssets
        .stream().filter { entry -> entry is CDAAsset }
        .map { entry -> mapToAsset(entry as CDAAsset) }
        .collect(Collectors.toList())
}

fun mapToAsset(entry: CDAAsset) : Asset{
    return Asset(entry.id(), entry.title(), entry.url(), entry.mimeType())
}
