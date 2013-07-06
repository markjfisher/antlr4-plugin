package net.markjfisher.antlr4.gradle.tasks

import org.gradle.api.DefaultTask

class AntlrBaseTask extends DefaultTask {
	def overrideOptionsFromCommandLine(type, grammarName, option, property, isBoolean = true) {
		def propertyOption= "${type}.${grammarName}.${option}"
		if (project.hasProperty(propertyOption)) {
			def v = project."$propertyOption"
			if (isBoolean && (v == "" || v == null)) v = true
			property."$grammarName"."$option" = v
		}
	}
}