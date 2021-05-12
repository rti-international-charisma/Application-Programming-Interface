package com.rti.charisma.api.content

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.rti.charisma.api.content.serialiser.ReferralsConversions

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonSerialize(using = ReferralsConversions.Serializer::class)
data class Referrals(
    @JsonProperty("referrals")
    @JsonAlias("data", "referrals")
    val referrals: List<Referral>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Referral(
    val type: String,
    val name: String,
    @JsonAlias("contact_number")
    val contact: String?,
    val address: String?,
    @JsonAlias("image")
    val imageUrl: String?
)
