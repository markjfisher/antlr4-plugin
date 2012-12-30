package net.markjfisher.antlr4.gradle

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class Antlr4Extension {
	NamedDomainObjectContainer<Antlr4SourceSet> sourceSets

	// these can be set in configuration block, or overridden with -Pkey=value
	def atn            = false
	def encoding       = ""
	def messageFormat  = ""
	def listener       = true
	def visitor        = false
	def longMessages   = false
	def depend         = false
	def warnAsError    = false
	def dbgST          = false
	def forceATN       = false
	def log            = false

	Antlr4Extension(NamedDomainObjectContainer sourceSets) {
		this.sourceSets = sourceSets
	}

	def sourceSets(Closure c) {
		sourceSets.configure(c)
	}
}