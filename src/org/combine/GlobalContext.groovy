#!/usr/bin/env groovy
package org.combine

class GlobalContext
{
	// Unity
	def UnityFolder = 'C:/Program Files/Unity/Hub/Editor/2022.3.17f1/'
	def MacUnityFolder = '/applications/unity/hub/editor/'
	def UnityExecuteMethod = 'ProjectBuilder.BuildWindows'
	def MacUnityVersion = '2022.3.17f1'
	def OverrideUnityVersion = ''

	// Переменные, устанавливаемые во время сборки
	def ExecutingPlatform = "Windows"
	def errorMessage = ""
	def ProjectFolder = "" // В эту папку скачивается проект. Устанавливается в SCM шаге
	def BuildName = "unnamed_build" // Устанавливается в шаге Build
	def ArtifactsList = [] // Устанавливается при архивации артефактов, используется при нотификации
	def BranchName = "/main" // Устанавливается перед чекаутом
	
	def GetUnityProjectAbsolutePath(String path)
	{
		return "${path}/" + ProjectFolder
	}
	
}

