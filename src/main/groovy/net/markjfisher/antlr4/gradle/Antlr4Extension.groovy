package net.markjfisher.antlr4.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.util.ConfigureUtil

class Antlr4Extension {
	NamedDomainObjectContainer<Antlr4SourceSet> sourceSets
	Antlr4ToolOptions tool = new Antlr4ToolOptions()

	Antlr4Extension(sourceSets) {
		this.sourceSets = sourceSets
	}

	def sourceSets(Closure c) {
		sourceSets.configure(c)
	}

	def tool(Closure c) {
		ConfigureUtil.configure(c, tool)
	}
}