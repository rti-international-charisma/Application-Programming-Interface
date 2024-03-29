package com.rti.charisma.api.fixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.content.Assessment
import com.rti.charisma.api.content.AssessmentSection
import com.rti.charisma.api.content.Option
import com.rti.charisma.api.content.Question
import com.rti.charisma.api.route.AssessmentResult
import com.rti.charisma.api.route.response.AssessmentScoreResponse
import com.rti.charisma.api.route.Question as AssessmentResultQuestion

object AssessmentFixture {
    fun assessmentSectionsJson(): String = """{"id":"section-id","section":"section name","introduction":"introduction","questions":[{"id":"qid-1","text":"question text1","description":"description1","positiveNarrative":true,"options":[{"text":"option1","weightage":1},{"text":"option2","weightage":2}]},{"id":"qid-2","text":"question text2","description":"description2","positiveNarrative":false,"options":[{"text":"option1","weightage":1},{"text":"option2","weightage":2}]}]}"""

    fun assessment(): Assessment {
        val option1 = Option("strongly agree", 5, 1)
        val option2 = Option("agree", 4, 2)
        val option3 = Option("neutral", 3, 3)
        val option4 = Option("disagree", 2, 4)
        val option5 = Option("strongly disagree", 1, 5)

        val question1 = Question("qid1", "question 1", "description 1", true, mutableListOf(option1, option2, option3))
        val question2 =
            Question(
                "qid2",
                "question 2",
                "description 2",
                true,
                mutableListOf(option1, option2, option3, option4, option5)
            )
        val question3 = Question("qid3", "question 3", "description 3", false, mutableListOf(option1, option2))


        val assessmentSection1 =
            AssessmentSection(
                "1",
                "Section 1",
                "published",
                "Introduction for section 1",
                mutableListOf(question1, question3)
            )
        val assessmentSection2 =
            AssessmentSection(
                "2",
                "section 2",
                "published",
                "introduction for section 2",
                mutableListOf(question2)
            )
        return Assessment(mutableListOf(assessmentSection1, assessmentSection2))
    }

    fun assessmentCmsContent(): Assessment {
        val content = """{
    "data": [
        {
            "id": "1",
            "title": "Section 1",
            "introduction" : "Introduction for section 1",
            "status" : "published",
            "questions": [
                {
                        "id": "qid1",
                        "text": "question 1",
                        "description": "description 1",
                        "positive_narrative": true,
                        "options": [
                            {
                                "options_id": {
                                    "id": 3,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:59+05:30",
                                    "text": "agree",
                                    "weightage": 4,
                                    "sort": 2
                                }
                            },
                            {
                                "options_id": {
                                    "id": 5,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:17+05:30",
                                    "text": "strongly agree",
                                    "weightage": 5,
                                    "sort": 1
                                }
                            },
                            {
                                "options_id": {
                                    "id": 4,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:10+05:30",
                                    "text": "neutral",
                                    "weightage": 3,
                                    "sort" : 3
                                }
                            }
                        ]
                    },
               {
                        "id": "qid3",
                        "text": "question 3",
                        "description": "description 3",
                        "positive_narrative": false,
                        "options": [
                            {
                                "options_id": {
                                    "id": 5,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:17+05:30",
                                    "text": "strongly agree",
                                    "weightage": 5,
                                    "sort" : 1
                                }
                            },
                            {
                                "options_id": {
                                    "id": 3,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:59+05:30",
                                    "text": "agree",
                                    "weightage": 4,
                                    "sort": 2
                                }
                            }
                        ]
                    }
            ]
        },
        {
            "id": "2",
            "title": "section 2",
            "introduction": "introduction for section 2",
            "status" : "published",
            "questions": [
                {
                        "id": "qid2",
                        "text": "question 2",
                        "description": "description 2",
                        "positive_narrative": true,
                        "options": [
                            {
                                "options_id": {
                                    "id": 3,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:59+05:30",
                                    "text": "disagree",
                                    "weightage": 2,
                                    "sort" : 4
                                }
                            },
                            {
                                "options_id": {
                                    "id": 1,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:43+05:30",
                                    "text": "strongly disagree",
                                    "weightage": 1,
                                    "sort" : 5
                                }
                            },
                            {
                                "options_id": {
                                    "id": 4,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:10+05:30",
                                    "text": "neutral",
                                    "weightage": 3,
                                    "sort" : 3
                                }
                            },
                            {
                                "options_id": {
                                    "id": 5,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:17+05:30",
                                    "text": "agree",
                                    "weightage": 4,
                                    "sort" : 2
                                }
                            },
                            {
                                "options_id": {
                                    "id": 6,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:25+05:30",
                                    "text": "strongly agree",
                                    "weightage": 5,
                                    "sort" : 1
                                }
                            }
                        ]
                    }
            ]
        }
    ]
}"""
        return jacksonObjectMapper().readValue(content, Assessment::class.java)
    }


    fun archivedAssessmentCmsContent(): Assessment {
        val content = """{
    "data": [
        {
            "id": 1,
            "title": "Section 1",
            "introduction" : "Introduction for section 1",
            "status" : "published",
            "questions": [
                {
                    "id": "qid1",
                    "text": "question 1",
                    "description": "description 1",
                    "options": [
                        {
                            "options_id": {
                                "id": 3,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:36:59+05:30",
                                "text": "disagree",
                                "weightage": 2,
                                "sort": 3
                            }
                        },
                        {
                            "options_id": {
                                "id": 5,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:37:17+05:30",
                                "text": "agree",
                                "weightage": 4,
                                "sort": 1
                            }
                        },
                        {
                            "options_id": {
                                "id": 4,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:37:10+05:30",
                                "text": "neutral",
                                "weightage": 3,
                                "sort": 2
                            }
                        }
                    ]
                },
            {
                    "id": "qid3",
                    "text": "question 3",
                    "description": "description 3",
                    "options": [
                        {
                            "options_id": {
                                "id": 5,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:37:17+05:30",
                                "text": "strongly agree",
                                "weightage": 5,
                                "sort": 1
                            }
                        },
                        {
                            "options_id": {
                                "id": 3,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:36:59+05:30",
                                "text": "agree",
                                "weightage": 4,
                                "sort": 2
                            }
                        }
                    ]
                }
            ]
        },
        {
            "id": 2,
            "title": "section 2",
            "introduction": "introduction for section 2",
            "status" : "archived",
            "questions": [
                {
                    "id": "qid3",
                    "text": "question 2",
                    "description": "description 2",
                    "options": [
                        {
                            "options_id": {
                                "id": 3,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:36:59+05:30",
                                "text": "strongly agree",
                                "weightage": 5,
                                "sort": 5
                            }
                        },
                        {
                            "options_id": {
                                "id": 1,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:36:43+05:30",
                                "text": "agree",
                                "weightage": 4,
                                "sort": 6
                            }
                        },
                        {
                            "options_id": {
                                "id": 4,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:37:10+05:30",
                                "text": "neutral",
                                "weightage": 3,
                                "sort": 3
                            }
                        },
                        {
                            "options_id": {
                                "id": 5,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:37:17+05:30",
                                "text": "disagree",
                                "weightage": 2,
                                "sort": 2
                            }
                        },
                        {
                            "options_id": {
                                "id": 6,
                                "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                "date_created": "2021-04-16T00:37:25+05:30",
                                "text": "strongly disagree",
                                "weightage": 1,
                                "sort": 1
                            }
                        }
                    ]
                }
            ]
        }
    ]
}"""
        return jacksonObjectMapper().readValue(content, Assessment::class.java)
    }

    fun onlyArchived(): Assessment {
        val content = """{
    "data": [
        {
            "id": 1,
            "title": "Section 1",
            "introduction" : "Introduction for section 1",
            "status" : "archived",
            "questions": [
                {
                        "id": "qid1",
                        "text": "question 1",
                        "description": "description 1",
                        "options": [
                            {
                                "options_id": {
                                    "id": 3,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:59+05:30",
                                    "text": "disagree",
                                    "weightage": 2,
                                    "sort": 3
                                }
                            },
                            {
                                "options_id": {
                                    "id": 5,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:17+05:30",
                                    "text": "agree",
                                    "weightage": 4,
                                    "sort": 1
                                }
                            },
                            {
                                "options_id": {
                                    "id": 4,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:10+05:30",
                                    "text": "neutral",
                                    "weightage": 3,
                                    "sort": 2
                                }
                            }
                        ]
                    },
                {
                        "id": "qid3",
                        "text": "question 3",
                        "description": "description 3",
                        "options": [
                            {
                                "options_id": {
                                    "id": 5,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:17+05:30",
                                    "text": "agree",
                                    "weightage": 4,
                                    "sort": 1
                                }
                            },
                            {
                                "options_id": {
                                    "id": 3,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:59+05:30",
                                    "text": "disagree",
                                    "weightage": 2,
                                    "sort": 2
                                }
                            }
                        ]
                    }
            ]
        }
    ]
}"""
        return jacksonObjectMapper().readValue(content, Assessment::class.java)
    }

    fun assessmentResponseJson(): String {
        return """{
  "assessment" : [ {
    "id" : "1",
    "section" : "Section 1",
    "introduction" : "Introduction for section 1",
    "questions" : [ {
      "id" : "qid1",
      "text" : "question 1",
      "description" : "description 1",
      "positiveNarrative" : true,
      "options" : [ {
        "text" : "strongly agree",
        "weightage" : 5
      }, {
        "text" : "agree",
        "weightage" : 4
      }, {
        "text" : "neutral",
        "weightage" : 3
      } ]
    }, {
      "id" : "qid3",
      "text" : "question 3",
      "description" : "description 3",
      "positiveNarrative" : false,
      "options" : [ {
        "text" : "strongly agree",
        "weightage" : 5
      }, {
        "text" : "agree",
        "weightage" : 4
      } ]
    } ]
  }, {
    "id" : "2",
    "section" : "section 2",
    "introduction" : "introduction for section 2",
    "questions" : [ {
      "id" : "qid2",
      "text" : "question 2",
      "description" : "description 2",
      "positiveNarrative" : true,
      "options" : [ {
        "text" : "strongly agree",
        "weightage" : 5
      }, {
        "text" : "agree",
        "weightage" : 4
      }, {
        "text" : "neutral",
        "weightage" : 3
      }, {
        "text" : "disagree",
        "weightage" : 2
      }, {
        "text" : "strongly disagree",
        "weightage" : 1
      } ]
    } ]
  } ]
}"""
    }

    fun onlyPublishedSectionsJson(): String {
        return """{
  "assessment" : [ {
    "id" : "1",
    "section" : "Section 1",
    "introduction" : "Introduction for section 1",
    "questions" : [ {
      "id" : "qid1",
      "text" : "question 1",
      "description" : "description 1",
      "positiveNarrative" : false,
      "options" : [ {
        "text" : "disagree",
        "weightage" : 2
      }, {
        "text" : "agree",
        "weightage" : 4
      }, {
        "text" : "neutral",
        "weightage" : 3
      } ]
    }, {
      "id" : "qid3",
      "text" : "question 3",
      "description" : "description 3",
      "positiveNarrative" : false,
      "options" : [ {
        "text" : "strongly agree",
        "weightage" : 5
      }, {
        "text" : "agree",
        "weightage" : 4
      } ]
    } ]
  } ]
}"""

    }

    fun assessmentResult(): AssessmentScoreResponse {

        val section1 = AssessmentResult(
            "sectionid-1", "sectiontype-1", mutableListOf(
                AssessmentResultQuestion("question1", 11),
                AssessmentResultQuestion("question2", 12)
            )
        )
        val section2 = AssessmentResult(
            "sectionid-2", "sectiontype-2", mutableListOf(
                AssessmentResultQuestion("question3", 21),
                AssessmentResultQuestion("question4", 22)
            )
        )
        return AssessmentScoreResponse(mutableListOf(section1, section2), 6)
    }

    //DO not remove
    fun assessmentScoreJson(): String {
        return """{
  "sections" : [ {
    "sectionId" : "sectionid-1",
    "sectionType" : "sectiontype-1",
    "answers" : [ {
      "questionId" : "question1",
      "score" : 11
    }, {
      "questionId" : "question2",
      "score" : 12
    } ]
  }, {
    "sectionId" : "sectionid-2",
    "sectionType" : "sectiontype-2",
    "answers" : [ {
      "questionId" : "question3",
      "score" : 21
    }, {
      "questionId" : "question4",
      "score" : 22
    } ]
  } ],
  "totalSections" : 6
}"""
    }

    fun emptyAssessment(): String {
        return """{
  "assessment" : [ ]
}"""
    }

    fun emptySections(): String {
        return """{
  "sections" : [ ],
  "totalSections" : 0
}"""

    }

}