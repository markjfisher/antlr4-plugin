# Antlr4Example project

A simple project to test/demonstrate running antlr grammars from a gradle build.

## Usage

> ./gradlew calc -Pargs="test.expr"

This will generate the antlr files, compile them, and then run the named test.expr against the grammar.