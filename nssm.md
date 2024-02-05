### Windows

#### Windows service install

```
nssm install JenkinsJavaWrapper "%PROGRAMFILES%\Java\jdk-21\bin\java.exe"
nssm set JenkinsJavaWrapper AppParameters -jar agent.jar -jnlpUrl http://localhost:8080/computer/win/jenkins-agent.jnlp -secret 35f8c665dc17acfd329d66b8897fc4c093ef636277fca3ab07121dd613a4d4a9 -workDir "C:\Jenkins\"
nssm set JenkinsJavaWrapper AppDirectory C:\Jenkins
nssm set JenkinsJavaWrapper AppStdout C:\Jenkins\jenkins.log
nssm set JenkinsJavaWrapper AppStderr C:\Jenkins\jenkins.log
nssm set JenkinsJavaWrapper AppStopMethodSkip 6
nssm set JenkinsJavaWrapper AppStopMethodConsole 1000
nssm set JenkinsJavaWrapper AppThrottle 5000
nssm start JenkinsJavaWrapper
```
