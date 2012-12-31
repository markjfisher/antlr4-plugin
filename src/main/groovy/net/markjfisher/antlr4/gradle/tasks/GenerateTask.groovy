package net.markjfisher.antlr4.gradle.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction

class GenerateTask extends AntlrBaseTask {

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
		def booleanOptions = ["atn", "longMessages", "listener", "visitor", "depend", "warnAsError", "dbgST", "forceATN", "log"]
		booleanOptions.each { option ->
			overrideOptionsFromCommandLine(option, project.antlr4.tool)
		}

		def stringOptions = ["encoding", "messageFormat"]
		stringOptions.each { option ->
			overrideOptionsFromCommandLine(option, project.antlr4.tool, false)
		}

		def optionArgs = []
		if (project.antlr4.tool.encoding != "") optionArgs << "-encoding=\"${project.antlr4.tool.encoding}\""
		if (project.antlr4.tool.messageFormat != "") optionArgs << "-message-format=\"${project.antlr4.tool.messageFormat}\""
		if (Boolean.valueOf(project.antlr4.tool.atn)) 			optionArgs << "-atn"
		if (Boolean.valueOf(project.antlr4.tool.longMessages)) 	optionArgs << "-long-messages"
		if (Boolean.valueOf(project.antlr4.tool.listener)) 		optionArgs << "-listener" else optionArgs << "-no-listener"
		if (Boolean.valueOf(project.antlr4.tool.visitor)) 		optionArgs << "-visitor"  else optionArgs << "-no-visitor"
		if (Boolean.valueOf(project.antlr4.tool.depend))		optionArgs << "-depend"
		if (Boolean.valueOf(project.antlr4.tool.warnAsError))	optionArgs << "-Werror"
		if (Boolean.valueOf(project.antlr4.tool.dbgST))			optionArgs << "-XdbgST"
		if (Boolean.valueOf(project.antlr4.tool.forceATN))		optionArgs << "-Xforce-atn"
		if (Boolean.valueOf(project.antlr4.tool.log))			optionArgs << "-Xlog"

		return optionArgs
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