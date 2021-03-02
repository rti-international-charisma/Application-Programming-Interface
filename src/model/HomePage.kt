package com.rti.charisma.api.model

data class HomePage(val title: String, val contentBody: String, val heroImage: List<Asset>, val videos: List<Asset>)
data class Asset(val id: String, val linkType: String)
