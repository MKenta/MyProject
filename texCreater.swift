#!/usr/bin/swift
import Foundation
  
var texargs = NSString()
var dviargs = NSString()


for arg in Process.arguments {
  texargs =  "platex"+" \(arg)"+".tex"
  dviargs =  "dvipdfmx"+" \(arg)"+".dvi"
}

var texTask = NSTask()
texTask.launchPath = "/bin/sh"
texTask.arguments = ["-c", texargs]
texTask.standardInput = NSFileHandle.fileHandleWithNullDevice()
texTask.standardError = NSFileHandle.fileHandleWithStandardError()
texTask.standardOutput = NSFileHandle.fileHandleWithStandardOutput()
texTask.launch()
texTask.waitUntilExit()




let dviTask = NSTask()
dviTask.launchPath = "/bin/sh"
dviTask.arguments = ["-c", dviargs]
//task.arguments = Process.arguments
dviTask.standardInput = NSFileHandle.fileHandleWithNullDevice()
dviTask.standardError = NSFileHandle.fileHandleWithStandardError()
dviTask.standardOutput = NSFileHandle.fileHandleWithStandardOutput()
dviTask.launch()
dviTask.waitUntilExit()#!/usr/bin/swift

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

