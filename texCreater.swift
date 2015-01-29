#!/usr/bin/swift

import Foundation

let task = NSTask()
task.launchPath = "/bin/sh"
task.arguments = ["-c", "ls "]
//task.arguments = Process.arguments
task.standardInput = NSFileHandle.fileHandleWithNullDevice()
task.standardError = NSFileHandle.fileHandleWithStandardError()
task.standardOutput = NSFileHandle.fileHandleWithStandardOutput()
task.launch()
task.waitUntilExit()

