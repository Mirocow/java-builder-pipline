#!/usr/bin/env groovy
package org.combine

import org.combine.GlobalContext

class Builder {

    private final Script script

    Builder(Script script) {
        this.script = script
    }

    def CleanUpBeforeBuild(GlobalContext context)
    {
        println "CleanUpBeforeBuild"
        script.dir("${context.Workspace}\\Build") 
        {
            script.deleteDir()
        }
        if (script.params.CleanLibrary)
        {
            script.dir("${context.Workspace}\\Library") 
            {
                script.deleteDir()
            }
        }
        
        def logFile = "\"${context.Workspace}\\Build/Build.log\""
        println "Logfile: ${logFile}"
        deleteFile(context, logFile)

        if(script.params.APPNAME){
            context.ApplicationName = script.params.APPNAME
        }

        if(script.params.WORKSPACE){
            context.Workspace = script.params.WORKSPACE
        }

        if(!context.Workspace){
            context.Workspace = script.pwd()
        }	    

    }

    def deleteFile(GlobalContext context, String path)
    {
        if (script.fileExists(path)) {
            // echo "File ${path} already exists. Deleting"
            new File(path).delete()
        } else {
            // echo "File ${path} does not exist."
        }
    }

    def GetUnityVersion(GlobalContext context)
    {
        // TODO: пока не работает чтение файлов, на маке задаем версию Unity в контексте
        if (context.ExecutingPlatform == "Mac")
            return context.MacUnityVersion
        
        if (context.OverrideUnityVersion != '')
            return context.OverrideUnityVersion
        
        def fileName = "${context.Workspace}\\ProjectSettings\\ProjectVersion.txt"
        println fileName
        def file = script.readFile fileName
        def lines = file.readLines()
        def text = lines[0].split(' ')        
        println 'Unity version: ' + text[1]
        return text[1]
    }

    def BuildUnity(GlobalContext context, buildTarget)
    {
        script.stage('Build ' + buildTarget)
        {
            println "================= Starting Unity build (on WIndows) ============================="
            CleanUpBeforeBuild(context)
            
            def unityVersion = GetUnityVersion(context)
            def unityExe = context.UnityFolder + unityVersion + '\\Editor\\unity.exe'

            // Проверка на наличие версии Unity
            def exists = script.fileExists unityExe
            if (!exists)
            {
                error('Can\'t find Unity version: ' + unityVersion + ". Path ${unityExe}")
            }

            // Запуск сборки              
            script.bat label: '', script: '"' + unityExe + '"' +
		' -appName ' + context.ApplicationName +
		' -projectPath "' + "${context.Workspace}" + '"' +
		' -executeMethod ' + context.UnityExecuteMethod + 
		' -targetPath "' + "${context.Workspace}\\Build" + '"' +
		' -logFile "' + "${context.Workspace}\\Build\\Build.log" + '"' +
		' -gradlePath "' + "${context.Workspace}\\gradle-6.7.1" + '"' +
		' -quit -batchmode -quitTimeout 6000'
            
            // Проверка на наличие папки с билдом
            def folderPath = "${context.Workspace}\\Build\\" + context.ApplicationName
            
            if (buildTarget == "Android")
                folderPath = folderPath + '.apk'
            
            def buildExists = script.fileExists folderPath 
            if (!buildExists)
                throw new Exception("Build folder doesnt exists")
        }
    }

}
