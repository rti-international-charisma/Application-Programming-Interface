package com.rti.charisma.api.model

import com.rti.charisma.api.config.ACCESSIBILITY_STATUS
import com.rti.charisma.api.config.ConfigProvider
import com.rti.charisma.api.model.AssessmentSection.Companion.toAssessmentSection
import com.rti.charisma.api.model.Option.Companion.toOption
import com.rti.charisma.api.model.Question.Companion.toQuestion
import java.util.stream.Collectors

data class Assessment(val assessment: List<AssessmentSection>) {
    companion object {
        fun toAssessment(data: List<Any>): Assessment {
            return Assessment(
                assessment = toSections(data)
            )
        }

        private fun toSections(data: List<Any?>): List<AssessmentSection> {
            if (data.isNotEmpty()) {
                val sections = data as List<*>
                return sections.stream()
                    .map { section -> toAssessmentSection(section as Map<String, Any>) }
                    .collect(Collectors.toList())
            }
            return emptyList()
        }

    }

}

data class AssessmentSection(val section: String, val introduction: String, val questions: List<Question>) {
    companion object {
        fun toAssessmentSection(section: Map<String, Any>): AssessmentSection {
            val status = section["status"]
            val states: List<String> = ConfigProvider.getList(ACCESSIBILITY_STATUS)
            if (states.contains(status)) {
                return AssessmentSection(
                    section = (section["title"] ?: "") as String,
                    introduction = (section["introduction"] ?: "") as String,
                    questions = toQuestions(section["questions"]!!)
                )
            }
            return AssessmentSection("", "", emptyList<Question>())
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

data class Question(
    var text: String = "",
    var description: String = "",
    var options: List<Option> = emptyList()
) {
    companion object {
        fun toQuestion(data: Map<String, Any>): Question {
            if (data["questions_id"] != null && data["questions_id"] is Map<*, *>) {
                val questions = data["questions_id"] as Map<String, Any>
                return Question(
                    text = (questions["text"] ?: "") as String,
                    description = (questions["description"] ?: "") as String,
                    options = toOptions(questions["options"])
                )
            }
            return Question()
        }

        private fun toOptions(data: Any?): List<Option> {
            if (data !== null && data is List<*>) {
                val options = data as List<Map<String, Any>>
                return options.stream()
                    .map { option -> toOption(option) }
                    .collect(Collectors.toList())
            }
            return emptyList()

        }
    }
}

data class Option(
    var text: String = "",
    var weightage: Number = 0
) {
    companion object {
        fun toOption(data: Map<String, Any>): Option {
            if (data["options_id"] !== null && data["options_id"] is Map<*, *>) {
                val option = data["options_id"] as Map<String, Any>
                return Option(
                    text = (option["text"] ?: "") as String,
                    weightage = (option["weight"] ?: 0) as Number
                )
            }
            return Option()
        }
    }

}
