package net.markjfisher.antlr4.gradle.tasks

import java.io.ByteArrayOutputStream;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction

class GrunTask extends DefaultTask {
	@Input grunClass = "org.antlr.v4.runtime.misc.TestRig"
	@TaskAction grun() {
		def optionArgs = []
		if (project.hasProperty("args")) {
			// split arguments by spaces or quotes, e.g. -Pargs='this "and that"'' -> ["this", "and that"]
			optionArgs = (project.args =~ /([^\s"']+)|["']([^'"]*)["']/).collect{it[1] ?: it[2]}
		}
		println "running test rig:\n - class     : $grunClass\n - args      : $optionArgs"
		new ByteArrayOutputStream().withStream { stream ->
			project.javaexec {
				main           = grunClass
				args           = optionArgs
				classpath      = project.files(project.configurations.antlr4, project.configurations.compile, project.configurations.runtime, project.sourceSets.main.output)
				standardOutput = stream
				// jvmArgs = ""
			}
			project.logger.quiet stream.toString()
		}
	}
}