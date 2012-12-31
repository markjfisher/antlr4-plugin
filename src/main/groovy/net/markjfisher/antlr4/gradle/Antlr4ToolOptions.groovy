package net.markjfisher.antlr4.gradle

class Antlr4ToolOptions {
	// these can be set overridden with -Pkey=tool.value or config block:
	// antrl4 {
	//     tool {
	//        atn = true
	//        // ...
	//     }
	// }
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