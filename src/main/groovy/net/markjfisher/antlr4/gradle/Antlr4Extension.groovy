package net.markjfisher.antlr4.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class Antlr4Extension {
	NamedDomainObjectContainer<Antlr4SourceSet> sourceSets

	Antlr4Extension(NamedDomainObjectContainer sourceSets) {
		this.sourceSets = sourceSets
	}

	def sourceSets(Closure c) {
		sourceSets.configure(c)
	}
}