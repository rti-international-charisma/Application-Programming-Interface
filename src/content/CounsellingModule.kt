package com.rti.charisma.api.content

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty


@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CounsellingModuleImage(
    @JsonProperty("id")
    val moduleImage: String? = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CounsellingSection(
    val id: String,
    val title: String?,
    val introduction: String?,
    val summary: String?,
    @JsonAlias("module_name", "module_id")
    val moduleName: String?,
    @JsonAlias("accordion_content", "accordions")
    val accordionContent: List<AccordionContent>?
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class AccordionContent(
    val id: String,
    val title: String,
    val description: String,
    @JsonProperty("image_url")
    var imageUrl: String? = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class CounsellingActionPoint(
    val id: String,
    val title: String,
    @JsonAlias("module_name", "module_id")
    val moduleName: String
)

