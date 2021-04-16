package com.rti.charisma.api.model

import com.rti.charisma.api.model.AssessmentSection.Companion.toAssessmentSection
import com.rti.charisma.api.model.Question.Companion.toQuestion
import java.util.stream.Collectors

data class Assessment(val sections: List<AssessmentSection>) {
    companion object {
        fun toAssessment(data: List<Any>): Assessment {
            return Assessment(
                sections = toSections(data)
            )
        }

        private fun toSections(data: List<Any?>): List<AssessmentSection> {
            if (data.isNotEmpty()) {
                val sections = data as List<*>
                sections.stream()
                    .map { section -> toAssessmentSection(section as Map<String, Any>) }
                    .collect(Collectors.toList())
            }
            return emptyList()
        }

    }

}

data class AssessmentSection(val title: String, val introduction: String, val questions: List<Question>) {
    companion object {
        fun toAssessmentSection(section: Map<String, Any>): AssessmentSection {
            return AssessmentSection(
                title = (section["title"] ?: "") as String,
                introduction = (section["introduction"] ?: "") as String,
                questions = toQuestions(section["questions"]!!)
            )
        }

        private fun toQuestions(data: Any?): List<Question> {
            if (data !== null && data is List<*>) {
                val questions = data as List<Map<String, Any>>
                return questions.stream()
                    .map { question -> toQuestion(question) }
                    .collect(Collectors.toList())
            }
            return emptyList()
        }
    }

}

data class Question(val text: String, val options: List<Option>) {
    companion object {
        fun toQuestion(data: Map<String, Any>): Question =
            Question(
                text = data["text"],
                options = toOptions(data["options"])
            )
    }

}

data class Option(val test: String, val weight: Number) {

}
