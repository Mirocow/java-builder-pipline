#!/usr/bin/env groovy
package org.combine

class GlobalContext
{
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

    private final Script script

    GlobalContext(Script script) {
        this.script = script

        if(this.script.params.APPNAME){
            this.ApplicationName = this.script.params.APPNAME
        }

        if(this.script.params.WORKSPACE){
            this.Workspace = this.script.params.WORKSPACE
        }

        if(!this.Workspace){
            this.Workspace = this.script.pwd()
        }
    }	
}

