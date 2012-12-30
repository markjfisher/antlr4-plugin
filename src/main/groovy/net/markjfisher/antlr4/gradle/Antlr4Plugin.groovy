package net.markjfisher.antlr4.gradle

import net.markjfisher.antlr4.gradle.tasks.GenerateTask

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.SourceSet

class Antlr4Plugin implements Plugin<Project> {
	private static final String ANTLR4_CONFIGURATIONS_NAME = "antlr4"
	private static final String ANTLR4_EXTENSIONS_NAME     = "antlr4"
	private static final String ANTLR4_SOURCESET_DIR       = "antlr4"

	void apply(Project project) {
		project.plugins.apply(JavaPlugin.class)
		applyExtensions(project)
		applyConfigurations(project)
		addSources(project)
		applyTasks(project)
	}

	def applyExtensions(project) {
		def sourceSets = project.container(Antlr4SourceSet) { name ->
			new Antlr4SourceSet(name, project)
		}
		project.configure(project) {
			extensions.create(ANTLR4_EXTENSIONS_NAME, Antlr4Extension, sourceSets)
		}
	}

	def applyConfigurations(project) {
		project.configurations.add(ANTLR4_CONFIGURATIONS_NAME).with {
			visible = false
			transitive = false
			description = "Antlr4 libraries"
		}
		// i'd really like to add the antlr4 classpath entries to compile at this point?
	}

	def addSources(project) {
		project.with {
			"$ANTLR4_SOURCESET_DIR" {
				sourceSets {
					// create the default "main" source set
					main {}
				}
			}
		}
		project."$ANTLR4_SOURCESET_DIR".sourceSets.all { sourceSet ->
			sourceSet.grammar.srcDir "src/${sourceSet.name}/$ANTLR4_SOURCESET_DIR"
		}
	}

	def applyTasks(project) {
		def generate = project.tasks.add(name: "generate", type: GenerateTask)
		def compileJava = project.tasks.findByPath("compileJava")
		compileJava.dependsOn(generate)
	}
}