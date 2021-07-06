package com.rti.charisma.api.route

import com.rti.charisma.api.service.ContentService
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.locations.*
import io.ktor.response.*
import io.ktor.routing.*

/**
 * All the endpoints are wrapper for CMS.
 *
 * get("/home") :
 * Return Home Page data from CMS
 *
 * get("/assessments") :
 * Returns Assessment data from CMS.
 *
 * get("/referrals") :
 * Returns referrals data from CMS
 *
 * get("/modules") :
 * Calculates and returns the recommended counselling module based on score calculations.
 *
 * get("/modules/{moduleId}") :
 * Returns the Counselling module by its Id.
 *  Possible values [ContentService.PrePModules]
 *
 * get("/content/{pageId}") :
 * Returns other contents from CMS fetched based on their Ids.
 *    Possible values used at the moment for pageId.
 *
 * hiv_prevention_prep
 *
 * aboutus
 *
 * assessment-intro
 *
 * male_partner_info_pack
 *
 * referral_intro
 */
@KtorExperimentalLocationsAPI
fun Routing.contentRoute(contentService: ContentService) {

    get("/home") {
        val homePage = contentService.getHomePage()
        call.respond(homePage)
    }

    get("/assessments") {
        val assessment = contentService.getAssessments()
        call.respond(assessment)
    }

    get("/referrals") {
        // Pass in the referral TYPE field as a filter parameter
        // to get only those type of referrals in results
        // Example: ?filter=Counselling
        // OR
        // Example: ?filter=Counselling,Shelters (Youth),Hotlines
        val referralTypes = call.request.queryParameters["filter"]
        if (referralTypes.isNullOrEmpty()) {
            val referrals = contentService.getReferrals()
            call.respond(referrals)
        } else {
            val referrals = contentService.getReferrals(referralTypes)
            call.respond(referrals)
        }
    }

    get("/modules") {
        val partnerScore = call.parameters["partner_score"]
        val prepConsent = call.parameters["prep_consent"]
        if (partnerScore.isNullOrEmpty() || prepConsent.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Missing required query parameters")
        } else {
            val module = contentService.getModule(partnerScore.toInt(), CONSENT.valueOf(prepConsent.toUpperCase()))
            call.respond(module)
        }
    }

    get("/modules/{moduleId}") {
        val moduleId = call.parameters["moduleId"]
        if (moduleId.isNullOrEmpty()) {
            call.respond(HttpStatusCode.BadRequest, "Missing module identifier")
        } else {
            val module = contentService.getModule(moduleId)
            call.respond(module)
        }
    }

    get("/content/{pageId}") {
        val pageId = call.parameters["pageId"]
        pageId?.let { call.respond(contentService.getPage(pageId)) }
    }

    get("/assets/{assetID}") {
        val asset = contentService.getAsset("${call.parameters["assetID"]}")
        call.respondBytes(asset)
    }
}

enum class CONSENT(val value: String) {
    AGREE("agree"),
    NEUTRAL("neutral"),
    OPPOSE("oppose"),
    UNAWARE("unaware")
}