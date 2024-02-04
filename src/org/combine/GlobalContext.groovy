#!/usr/bin/env groovy
package org.combine

class GlobalContext
{
	// Unity
	def UnityFolder = 'C:/Program Files/Unity/Hub/Editor/'
	def MacUnityFolder = '/applications/unity/hub/editor/'
	def UnityExecuteMethod = 'BuildProject.BuildWindows'
	def MacUnityVersion = '2022.3.17f1'
	def OverrideUnityVersion = ''

	// Переменные, устанавливаемые во время сборки
	def ExecutingPlatform = "Windows"
	def errorMessage = ""
	def ArtifactsList = [] // Устанавливается при архивации артефактов, используется при нотификации
	def BranchName = "/main" // Устанавливается перед чекаутом
	
}

