package com.rti.charisma.api.route.response

import com.rti.charisma.api.route.AssessmentResult

data class AssessmentScoreResponse(val sections: List<AssessmentResult>, val totalSections: Int)
