package com.rti.charisma.api.fixtures

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rti.charisma.api.client.CmsList
import com.rti.charisma.api.model.Assessment
import com.rti.charisma.api.model.AssessmentSection
import com.rti.charisma.api.model.Option
import com.rti.charisma.api.model.Question

class AssessmentContentFixture {
    fun assessment(): Assessment {
        val option1 = Option("disagree", 2)
        val option2 = Option("agree", 4)
        val option3 = Option("neutral", 3)
        val option4 = Option("strongly disagree", 1)
        val option5 = Option("strongly agree", 5)

        val question1 = Question("question 1", mutableListOf(option1, option2, option3))
        val question2 = Question("question 2", mutableListOf(option1, option4, option3, option2, option5))
        val question3 = Question("question 3", mutableListOf(option2, option1))


        val assessmentSection1 =
            AssessmentSection("Section 1", "Introduction for section 1", mutableListOf(question1, question3))
        val assessmentSection2 = AssessmentSection("section 2", "introduction for section 2", mutableListOf(question2))
        return Assessment(mutableListOf(assessmentSection1, assessmentSection2))
    }

    fun assessmentContent(): CmsList {
        val content = """{
    "data": [
        {
            "id": 1,
            "title": "Section 1",
            "introduction" : "Introduction for section 1",
            "status" : "published",
            "questions": [
                {
                    "questions_id": {
                        "text": "question 1",
                        "options": [
                            {
                                "options_id": {
                                    "id": 3,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:59+05:30",
                                    "text": "disagree",
                                    "weight": 2
                                }
                            },
                            {
                                "options_id": {
                                    "id": 5,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:17+05:30",
                                    "text": "agree",
                                    "weight": 4
                                }
                            },
                            {
                                "options_id": {
                                    "id": 4,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:10+05:30",
                                    "text": "neutral",
                                    "weight": 3
                                }
                            }
                        ]
                    }
                },
                {
                    "questions_id": {
                        "text": "question 3",
                        "options": [
                            {
                                "options_id": {
                                    "id": 5,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:17+05:30",
                                    "text": "agree",
                                    "weight": 4
                                }
                            },
                            {
                                "options_id": {
                                    "id": 3,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:59+05:30",
                                    "text": "disagree",
                                    "weight": 2
                                }
                            }
                        ]
                    }
                }
            ]
        },
        {
            "id": 2,
            "title": "section 2",
            "introduction": "introduction for section 2",
            "status" : "published",
            "questions": [
                {
                    "questions_id": {
                        "text": "question 2",
                        "options": [
                            {
                                "options_id": {
                                    "id": 3,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:59+05:30",
                                    "text": "disagree",
                                    "weight": 2
                                }
                            },
                            {
                                "options_id": {
                                    "id": 1,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:36:43+05:30",
                                    "text": "strongly disagree",
                                    "weight": 1
                                }
                            },
                            {
                                "options_id": {
                                    "id": 4,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:10+05:30",
                                    "text": "neutral",
                                    "weight": 3
                                }
                            },
                            {
                                "options_id": {
                                    "id": 5,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:17+05:30",
                                    "text": "agree",
                                    "weight": 4
                                }
                            },
                            {
                                "options_id": {
                                    "id": 6,
                                    "user_created": "078ad2fa-44c9-4cf7-8e3e-a0199bb3049d",
                                    "date_created": "2021-04-16T00:37:25+05:30",
                                    "text": "strongly agree",
                                    "weight": 5
                                }
                            }
                        ]
                    }
                }
            ]
        }
    ]
}"""
        return jacksonObjectMapper().readValue(content, CmsList::class.java)
    }

    fun getEmptyContent(): CmsList {
        val content = """{
    "data": [
        {
            "id": 1,
            "status" : "published",
            "questions": [
                {
                    "questions_id": {}
                }
            ]
        }
    ]
}"""
        return jacksonObjectMapper().readValue(content, CmsList::class.java)
    }


    fun archived(): CmsList {
        val content = """{
    "data": [
        {
            "id": 1,
            "status" : "archived",
            "questions": [
                {
                    "questions_id": {}
                }
            ]
        }
    ]
}"""
        return jacksonObjectMapper().readValue(content, CmsList::class.java)
    }


    fun assessmentResponseJson(): String {
        return """{
  "assessment" : [ {
    "section" : "Section 1",
    "introduction" : "Introduction for section 1",
    "questions" : [ {
      "text" : "question 1",
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
      "text" : "question 3",
      "options" : [ {
        "text" : "agree",
        "weightage" : 4
      }, {
        "text" : "disagree",
        "weightage" : 2
      } ]
    } ]
  }, {
    "section" : "section 2",
    "introduction" : "introduction for section 2",
    "questions" : [ {
      "text" : "question 2",
      "options" : [ {
        "text" : "disagree",
        "weightage" : 2
      }, {
        "text" : "strongly disagree",
        "weightage" : 1
      }, {
        "text" : "neutral",
        "weightage" : 3
      }, {
        "text" : "agree",
        "weightage" : 4
      }, {
        "text" : "strongly agree",
        "weightage" : 5
      } ]
    } ]
  } ]
}"""
    }

}