package net.markjfisher.antlr4.gradle

import org.gradle.api.NamedDomainObjectContainer

class Antlr4Extension {
	NamedDomainObjectContainer<Antlr4SourceSet> sourceSets
	NamedDomainObjectContainer<Antlr4ToolOptions> tool

	Antlr4Extension(sourceSets, tool) {
		this.sourceSets = sourceSets
		this.tool       = tool
	}

	def sourceSets(Closure c) {
		sourceSets.configure(c)
	}

	def tool(Closure c) {
		tool.configure(c)
	}
}