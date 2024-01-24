#!/usr/bin/env groovy
package org.combine

class GlobalContext
{
    // Unity
	def UnityFolder = 'C:/Unity/'
	def MacUnityFolder = '/applications/unity/hub/editor/'
	def UnityExecuteMethod = 'ProjectBuilder.BuildWindows'
	def MacUnityVersion = '2019.3.9f1'
	def OverrideUnityVersion = ''

    // Переменные, устанавливаемые во время сборки
	def ExecutingPlatform = "Windows"
	def errorMessage = ""
	def ProjectFolder = "UnknownProjectFolder" // В эту папку скачивается проект. Устанавливается в SCM шаге
	def BuildName = "unnamed_build" // Устанавливается в шаге Build
	def ArtifactsList = [] // Устанавливается при архивации артефактов, используется при нотификации
	def BranchName = "/main" // Устанавливается перед чекаутом
	
	public GlobalContext()
	{
	}
	
	def GetUnityProjectAbsolutePath(WORKSPACE)
	{
		return "${WORKSPACE}/" + ProjectFolder
	}
	
}

