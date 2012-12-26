package net.markjfisher.antlr4.gradle

import net.markjfisher.antlr4.gradle.tasks.RunToolTask

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.SourceSet

class Antlr4Plugin implements Plugin<Project> {
	void apply(Project project) {
		project.plugins.apply(JavaPlugin.class)
		applyExtensions(project)
		addSources(project)
		applyTasks(project)
	}

	void applyExtensions(project) {
		def sourceSets = project.container(Antlr4SourceSet) { name ->
			new Antlr4SourceSet(name, project)
		}
		project.configure(project) {
			extensions.create("antlr4", Antlr4Extension, sourceSets)
		}
	}

	void addSources(project) {
		project.with {
			antlr4 {
				sourceSets {
					main {}
				}
			}
		}
		project.antlr4.sourceSets.all { sourceSet ->
			sourceSet.grammar.srcDir "src/${sourceSet.name}/antlr4"
		}
	}

	void applyTasks(project) {
		def runAntlr4Tool = project.tasks.add(name: "runAntlr4Tool", type: RunToolTask)
	}
}