#!/usr/bin/env groovy
package org.combine

import org.combine.GlobalContext

class Builder {

	def CleanUpBeforeBuild(GlobalContext context)
	{
		println "CleanUpBeforeBuild"
		dir(context.ProjectFolder + '/Builds') 
		{
			deleteDir()
		}
		if (params.CleanLibrary)
		{
			dir(context.ProjectFolder + '/Library') 
			{
				deleteDir()
			}
		}
		
		def logFile = "\"${WORKSPACE}/UnityEditor.log\""
		deleteFile(context, logFile)
	}

	def GetUnityVersion(GlobalContext context)
	{
		// TODO: пока не работает чтение файлов, на маке задаем версию Unity в контексте
		//	if (context.ExecutingPlatform == "Mac")
		//		return context.MacUnityVersion
		if (context.OverrideUnityVersion != '')
			return context.OverrideUnityVersion
		
		def fileName = context.GetUnityProjectAbsolutePath(WORKSPACE) + '/ProjectSettings/ProjectVersion.txt'
		println fileName
		def file = readFile fileName
		def lines = file.readLines()
		def text = lines[0].split(' ')        
		print 'Unity version: ' + text[1]
		return text[1]
	}

	def BuildUnity(GlobalContext context, buildTarget)
	{
		stage('Build ' + buildTarget)
		{
			println "================= Starting Unity build (on WIndows) ============================="
			CleanUpBeforeBuild(context)
			
			def unityVersion = GetUnityVersion(context)
			def unityExe = context.UnityFolder + unityVersion + '/Editor/unity.exe'

			// Проверка на наличие версии Unity
			def exists = fileExists unityExe
			if (!exists)
			{
				error('Can\'t find Unity version: ' + unityVersion + '. Please, check Notion for more information: https://www.notion.so/helloio/Unity-02ce4afae3f24fdc8565f96d7d37a6c0')
			}

			// Запуск сборки
			bat label: '', script: context.UnityFolder + unityVersion + '/Editor/unity.exe -projectPath "' + context.GetUnityProjectAbsolutePath(WORKSPACE) + 
			'" -executeMethod ' + context.UnityExecuteMethod + ' -logFile "UnityEditor.log" -buildTarget ' + buildTarget + ' -quit -batchmode -quitTimeout 6000'
			
			// Проверка на наличие папки с билдом
			def folderPath = "${WORKSPACE}/${JOB_BASE_NAME}/Builds/" + context.BuildName
			if (buildTarget == "Android")
				folderPath = context.ProjectFolder + '\\Builds\\' + context.BuildName + '.apk'
			
			def buildExists = fileExists folderPath 
			if (!buildExists)
				throw new Exception("Build folder doesnt exists")
		}
	}

}