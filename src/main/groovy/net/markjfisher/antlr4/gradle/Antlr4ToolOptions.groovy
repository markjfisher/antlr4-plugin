package net.markjfisher.antlr4.gradle

class Antlr4ToolOptions {
	def name

	def Antlr4ToolOptions(name) {
		this.name = name
	}

	// configure with named block for particular grammar:
	// antrl4 {
	//     tool {
	//        "example.Grammar" {
	//            atn = true
	//            // ...
	//        }
	//     }
	// }
	// or use command line values, which override above:
	// -Ptool.example.Grammar.atn=true
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
}