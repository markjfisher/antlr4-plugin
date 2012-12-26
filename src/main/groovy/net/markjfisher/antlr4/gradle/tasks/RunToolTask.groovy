package net.markjfisher.antlr4.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class RunToolTask extends DefaultTask {

	@TaskAction runTool() {
		println "runTool()"
	}
}