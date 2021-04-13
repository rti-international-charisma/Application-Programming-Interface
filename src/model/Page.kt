package com.rti.charisma.api.model

data class Page( val title: String,
                 val description: String,
                 val introduction: String,
                 val images: List<PageImage>,) {
}