package net.markjfisher.antlr4.gradle.tasks

import java.io.ByteArrayOutputStream;
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectories
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction

class GenerateTask extends AntlrBaseTask {
	@Input toolClass = "org.antlr.v4.Tool"
	@Input buildGenDir = "antlr4-gen"

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
			def buildPath = project.file("${project.buildDir}/$buildGenDir/${srcSet.name}")
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
		def compileJava = project.tasks.findByPath("compileJava")
		def addedSrcDirs = []
		project.antlr4.sourceSets.each { srcSet ->
			// output to build/antlr4-gen/<config>/
			def buildPath = project.file("${project.buildDir}/$buildGenDir/${srcSet.name}")

			srcSet.grammar.files.each { file ->
				def relativeDirPath = calcRelativePathToSoureSet(srcSet, file)
				def grammarName = file.name.split("\\.")[0..-2].join(".") // remove the .g4 extension
				def optionArgs = createOptionsArray("${relativeDirPath}.$grammarName")

				def outdir = project.file("$buildPath/$relativeDirPath")
				if (!addedSrcDirs.contains(outdir.canonicalPath)) {
					addedSrcDirs << outdir.canonicalPath
				}
				def grammarPackage = relativeDirPath.replaceAll(System.properties['file.separator'], '.')
				// run antlr tool to generate the output
				optionArgs = ["-o", "$outdir", "-package", grammarPackage, optionArgs, file.canonicalPath].flatten()
				project.logger.debug "running antlr\n - class   : $toolClass\n - path    : ${project.configurations.antlr4.files}\n - options : $optionArgs"
				new ByteArrayOutputStream().withStream { stream ->
					project.javaexec {
						main           = toolClass
						args           = optionArgs
						classpath      = project.configurations.antlr4
						standardOutput = stream
						// jvmArgs = ""
					}
				}
			}
			addedSrcDirs.each { outdir ->
				compileJava.source(outdir)
				compileJava.include("**/*.java")
			}
		}
	}

	def createOptionsArray(grammarName) {
		def booleanOptions = [
			"atn", "longMessages", "listener", "visitor", 
			"depend", "warnAsError", "dbgST", "forceATN", "log"
		]
		booleanOptions.each { option ->
			overrideOptionsFromCommandLine("tool", grammarName, option, project.antlr4.tool)
		}

		def stringOptions = ["encoding", "messageFormat"]
		stringOptions.each { option ->
			overrideOptionsFromCommandLine("tool", grammarName, option, project.antlr4.tool, false)
		}

		def optionArgs = applyOptions([], project.antlr4.tool."default") // global values, this is created by the plugin
		def grammarOptions = project.antlr4.tool.findByName(grammarName)
		if (grammarOptions) {
			optionArgs = applyOptions(optionArgs, grammarOptions) // override if we found a configuration block for the grammar
		}

		return optionArgs
	}

	def applyOptions(optionArgs, configBlock) {
		if (configBlock.encoding != "") 				optionArgs = addStringOption(optionArgs, "-encoding", configBlock.encoding)
		if (configBlock.messageFormat != "") 			optionArgs = addStringOption(optionArgs, "-message-format", configBlock.messageFormat)
		if (Boolean.valueOf(configBlock.atn)) 			optionArgs = addOption(optionArgs, "-atn")
		if (Boolean.valueOf(configBlock.longMessages)) 	optionArgs = addOption(optionArgs, "-long-messages")
		if (Boolean.valueOf(configBlock.listener)) 		optionArgs = addOption(optionArgs, "-listener", "-no-listener")
														else optionArgs = addOption(optionArgs, "-no-listener", "-listener")
		if (Boolean.valueOf(configBlock.visitor)) 		optionArgs = addOption(optionArgs, "-visitor", "-no-visitor")
														else optionArgs = addOption(optionArgs, "-no-visitor", "-visitor")
		if (Boolean.valueOf(configBlock.depend))		optionArgs = addOption(optionArgs, "-depend")
		if (Boolean.valueOf(configBlock.warnAsError))	optionArgs = addOption(optionArgs, "-Werror")
		if (Boolean.valueOf(configBlock.dbgST))			optionArgs = addOption(optionArgs, "-XdbgST")
		if (Boolean.valueOf(configBlock.forceATN))		optionArgs = addOption(optionArgs, "-Xforce-atn")
		if (Boolean.valueOf(configBlock.log))			optionArgs = addOption(optionArgs, "-Xlog")
		return optionArgs
	}

	def addOption(optionArgs, setValue, unsetValue = "") {
		if (!optionArgs.contains(setValue)) {
			optionArgs += setValue
		}
		if (unsetValue != "" && optionArgs.contains(unsetValue)) {
			optionArgs -= unsetValue
		}
		return optionArgs
	}

	def addStringOption(optionArgs, key, value) {
		// remove the [key, value] entry
		def keyPos = optionArgs.indexOf(key)
		if (keyPos > -1) {
			optionArgs -= optionArgs[keyPos..keyPos+1]
		}
		optionArgs << key << value
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