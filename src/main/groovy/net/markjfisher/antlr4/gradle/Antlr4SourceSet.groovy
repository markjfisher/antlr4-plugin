package net.markjfisher.antlr4.gradle

import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.internal.file.DefaultSourceDirectorySet
import org.gradle.util.ConfigureUtil

class Antlr4SourceSet {
	def name
	SourceDirectorySet grammar

	Antlr4SourceSet(name, project) {
		this.name = name
		def displayName = "$name Antlr4 Grammar"
		grammar = new DefaultSourceDirectorySet(displayName, project.fileResolver)
		grammar.filter.include('**/*.g4')
	}

	def grammar(Closure c) {
		ConfigureUtil.configure(c, grammar)
	}
}
