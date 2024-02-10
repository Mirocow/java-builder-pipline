#!/usr/bin/env groovy
package org.combine

class GlobalContext
{

    private final Script script
	
    GlobalContext(Script script) {
        this.script = script
    }
	
    // Unity
    def UnityFolder = 'C:\\Program Files\\Unity\\Hub\\Editor\\'
    def MacUnityFolder = '/applications/unity/hub/editor/'
    def UnityExecuteMethod = 'BuildProject.BuildAndroid'
    def MacUnityVersion = '2022.3.17f1'
    def OverrideUnityVersion = ''
    def Workspace = ''
    def ApplicationName = 'App'
	
    // Переменные, устанавливаемые во время сборки
    def ExecutingPlatform = "Windows"
    def errorMessage = ""
    def ArtifactsList = [] // Устанавливается при архивации артефактов, используется при нотификации
    def BranchName = "/main" // Устанавливается перед чекаутом

    if(script.params.APPNAME){
        ApplicationName = script.params.APPNAME
    }

    if(script.params.WORKSPACE){
        Workspace = script.params.WORKSPACE
    }

    if(!script.Workspace){
        Workspace = script.pwd()
    }	
	
}

