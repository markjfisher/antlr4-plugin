package net.markjfisher.antlr4.gradle.tasks

import org.gradle.api.DefaultTask

class AntlrBaseTask extends DefaultTask {
	def overrideOptionsFromCommandLine(option, property, isBoolean = true) {
		if (project.hasProperty(option)) {
			def v = project."$option"
			if (isBoolean && (v == "" || v == null)) v = "true" // allows -PbooleanValue to mean true
			property."$option" = v
		}
	}
}