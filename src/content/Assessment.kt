package com.rti.charisma.api.content

import com.fasterxml.jackson.annotation.*
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.rti.charisma.api.content.serialiser.AssessmentSectionConversions

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class Assessment(
    @JsonProperty("assessment")
    @JsonAlias("data", "assessment")
    val assessment: MutableList<AssessmentSection>
)

@JsonIgnoreProperties(value = ["status"], allowSetters = true, ignoreUnknown = true)
@JsonSerialize(using = AssessmentSectionConversions.Serializer::class)
data class AssessmentSection(
    val id: String,
    @JsonProperty("section")
    @JsonAlias("title", "section")
    val section: String,
    val status: String,
    val introduction: String,
    val questions: List<Question>
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonTypeName("questions_id")
//@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
data class Question(
    val id: String,
    val text: String,
    @JsonProperty(required = false)
    val description: String?,
    @JsonProperty("positiveNarrative")
    @JsonAlias("positive_narrative")
    val positiveNarrative: Boolean,
    val options: List<Option>
)

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeName("options_id")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
data class Option(
    val text: String,
    val weightage: Int
)