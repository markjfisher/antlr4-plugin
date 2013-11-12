# Antlr4Plugin

A plugin library for antlr4, based on antlr3 plugin.

## Usage

An example project is in the Antlr4Example subproject.

## Compiling

> ./gradlew build

## Configuration

The "antlr4" configuration block controls parameters used by the antlr4 compiler.
All major flags should be supported through configuration.

	antrl4 {
	    tool {
	       "example.Expr" {
	           atn = true
	           ...
	       }
	    }
	}

See Antlr4ToolOptions for supported flags.

Different grammars can be configured with different values through named configurations as shown above.

## Conventions

Put grammar sources in src/main/antlr4, subdirs become package like names in the configuration block.
e.g. in the block above, the Expr.g4 lives in src/main/antlr4/example.

Generated classes by default go into build/antlr4-gen, but this is controlled by the buildGenDir property of GenerateTask.

## Dependencies / Build configuration

At the moment add typical configuration of build requires following elements

	apply plugin: 'antlr4'
	
	dependencies {
		compile "org.codehaus.groovy:groovy:1.8+"
		compile 'org.antlr:antlr4:4.0'
	
		antlr4 'org.antlr:antlr4:4.0'
		antlr4 'org.antlr:antlr4-runtime:4.0'
		antlr4 'org.antlr:antlr-runtime:3.5'
		antlr4 'org.antlr:ST4:4.0.7'
	}

As soon as I work out how to hook into the compile configuration, this will become easier.

## Overriding values on command line.

All options can be overridden with command line args.
e.g.

> -Ptool.example.Expr.listener=true

would cause the build to generate a listener.

## Running example project

This is also documented in the example folder itself.

> cd Antlr4Example
> ./gradlew calc -Pargs="test.expr"
> ...
> :calc
> 193
> 17
> 9

License
-------

    Copyright 2013 the original author or authors

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
