package net.markjfisher.antlr4.gradle.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction

class GenerateTask extends DefaultTask {

	@InputFiles getGrammars() {
		def inputGrammars = []
		project.antlr4.sourceSets.each { srcSet ->
			inputGrammars << srcSet.grammar.files
		}
		return project.files(inputGrammars)
	}

	@OutputDirectories getOutGenDirs() {
		def outputGenDirs = []
		project.antlr4.sourceSets.each { srcSet ->
			def buildPath = project.file("${project.buildDir}/antlr4-gen/${srcSet.name}")
			srcSet.grammar.files.each { file ->
				def relativeDirPath = calcRelativePathToSoureSet(srcSet, file)
				def outdir = project.file("$buildPath/$relativeDirPath")
				if (!outputGenDirs.contains(outdir.canonicalPath)) {
					outputGenDirs << outdir
				}
			}
		}
		return project.files(outputGenDirs)
	}

	@TaskAction generate() {
		def optionArgs = createOptionsArray()
		def compileJava = project.tasks.findByPath("compileJava")
		def addedSrcDirs = []
		project.antlr4.sourceSets.each { srcSet ->
			// output to build/antlr4-gen/<config>/
			def buildPath = project.file("${project.buildDir}/antlr4-gen/${srcSet.name}")

			srcSet.grammar.files.each { file ->
				def relativeDirPath = calcRelativePathToSoureSet(srcSet, file)

				def outdir = project.file("$buildPath/$relativeDirPath")
				if (!addedSrcDirs.contains(outdir.canonicalPath)) {
					// add this dir as a source dir for java compilation too
					compileJava.setSource(outdir)
					addedSrcDirs << outdir.canonicalPath
				}
				def grammarPackage = relativeDirPath.replaceAll(System.properties['file.separator'], '.')
				// run antlr tool to generate the output
				// antlr4 -o outdir -package p 
				project.logger.quiet "running antlr for $file with options: -o $outdir, -package $grammarPackage, $optionArgs"
				new ByteArrayOutputStream().withStream { stream ->
					project.javaexec {
						main           = "org.antlr.v4.Tool"
						args           = ["-o", "$outdir", "-package", grammarPackage, optionArgs, file.canonicalPath].flatten()
						classpath      = project.configurations.antlr4
						standardOutput = stream
						// jvmArgs = ""
					}
				}
			}
		}
	}

	def createOptionsArray() {
		def options = ["atn", "encoding", "messageFormat", "longMessages", "listener", "visitor", "depend", "warnAsError", "dbgST", "forceATN", "log"]
		options.each { option ->
			overrideOptionsFromCommandLine(option)
		}

		def optionArgs = []
		if (project.antlr4.encoding != "") optionArgs << "-encoding=\"$project.antlr4.encoding\""
		if (project.antlr4.messageFormat != "") optionArgs << "-message-format=\"$project.antlr4.messageFormat\""
		if (Boolean.valueOf(project.antlr4.atn)) 			optionArgs << "-atn"
		if (Boolean.valueOf(project.antlr4.longMessages)) 	optionArgs << "-long-messages"
		if (Boolean.valueOf(project.antlr4.listener)) 		optionArgs << "-listener" else optionArgs << "-no-listener"
		if (Boolean.valueOf(project.antlr4.visitor)) 		optionArgs << "-visitor"  else optionArgs << "-no-visitor"
		if (Boolean.valueOf(project.antlr4.depend))			optionArgs << "-depend"
		if (Boolean.valueOf(project.antlr4.warnAsError))	optionArgs << "-Werror"
		if (Boolean.valueOf(project.antlr4.dbgST))			optionArgs << "-XdbgST"
		if (Boolean.valueOf(project.antlr4.forceATN))		optionArgs << "-Xforce-atn"
		if (Boolean.valueOf(project.antlr4.log))			optionArgs << "-Xlog"

		return optionArgs
	}

	def overrideOptionsFromCommandLine(option) {
		if(project.hasProperty(option)) {
			project.antlr4."$option" = project."$option"
		}
	}


	def calcRelativePathToSoureSet(srcSet, file) {
		// used for creating same dir path structure as grammar
		// e.g. main/antlr4/example/Expr.g4 -> example
		def returnPath = ""
		srcSet.grammar.srcDirs.each { dir ->
			if (file.canonicalPath.startsWith(dir.canonicalPath)) {
				returnPath = file.parentFile.canonicalPath - (dir.canonicalPath + System.properties['file.separator'])
			}
		}
		return returnPath
	}
}