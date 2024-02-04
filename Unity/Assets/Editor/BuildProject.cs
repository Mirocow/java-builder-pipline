// -------------------------------------------------------------------------------------------------
// Assets/Editor/BuildProject.cs
// -------------------------------------------------------------------------------------------------
using UnityEngine;
using UnityEditor;
using System.Collections.Generic;
using UnityEditor.Build.Reporting;
using System;
using System.IO;
  
// ------------------------------------------------------------------------
// https://docs.unity3d.com/Manual/CommandLineArguments.html
// "unity.exe" \
//		-projectPath "C:/Users/Admin/Projects/MyProject" \
//		-executeMethod BuildProject.BuildAndroid \
//		-targetPath "C:\Users\Admin\Projects\Build"
// ------------------------------------------------------------------------
public class BuildProject 
{
  
    // ------------------------------------------------------------------------
    // called from Jenkins
    // ------------------------------------------------------------------------
    public static void BuildAndroid()
    {
        var args = FindArgs();
 
        string fullPathAndName = args.targetPath + args.appName + ".apk";
        Build(fullPathAndName, BuildTargetGroup.Standalone, BuildTarget.Android, BuildOptions.None);
    }  
  
    // ------------------------------------------------------------------------
    // called from Jenkins
    // ------------------------------------------------------------------------
    public static void BuildMacOS()
    {
        var args = FindArgs();
 
        string fullPathAndName = args.targetPath + args.appName + ".app";
        Build(fullPathAndName, BuildTargetGroup.Standalone, BuildTarget.StandaloneOSX, BuildOptions.None);
    }
	
    public static void BuildiOS ()
    {
        var args = FindArgs();
		
        string fullPathAndName = args.targetPath + args.appName;
        Build(fullPathAndName, BuildTargetGroup.Standalone, BuildTarget.iOS, BuildOptions.None);
    }	
 
    // ------------------------------------------------------------------------
    // called from Jenkins
    // ------------------------------------------------------------------------
    public static void BuildWindows()
    {
        var args = FindArgs();
 
        string fullPathAndName = args.targetPath + args.appName;
        Build(fullPathAndName, BuildTargetGroup.Standalone, BuildTarget.StandaloneWindows, BuildOptions.None);
    }
 
    // ------------------------------------------------------------------------
    // called from Jenkins
    // ------------------------------------------------------------------------
    public static void BuildLinux()
    {
        var args = FindArgs();
 
        string fullPathAndName = args.targetPath + args.appName;
        Build(fullPathAndName, BuildTargetGroup.Standalone, BuildTarget.StandaloneLinux64, BuildOptions.None);
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------  
    private static Args FindArgs()
    {
        var returnValue = new Args();
		
        string targetPath = GetArgByName("targetPath");
        if(targetPath != null){
            returnValue.targetPath = targetPath.TrimEnd(Path.DirectorySeparatorChar);
        }

        returnValue.targetPath = returnValue.targetPath + Path.DirectorySeparatorChar;
		
        string gradlePath = GetArgByName("gradlePath");
        if(gradlePath != null){
            returnValue.gradlePath = gradlePath;
        }

        string appName = GetArgByName("appName");
        if(appName != null){
            returnValue.appName = appName;
        }		
  
        return returnValue;
    }
	
    // ------------------------------------------------------------------------
    // Helper function for getting the command line arguments
    // ------------------------------------------------------------------------
    private static string GetArgByName(string name)
    {
        var args = System.Environment.GetCommandLineArgs();
        for (int i = 0; i < args.Length; i++)
        {
            if (args[i].Contains(name) && args.Length > i + 1)
            {
                return args[i + 1];
            }
        }
      
        return null;
    }	
 
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    private static string[] FindEnabledEditorScenes(){
  
        List<string> EditorScenes = new List<string>();
        foreach (EditorBuildSettingsScene scene in EditorBuildSettings.scenes)
            if (scene.enabled)
                EditorScenes.Add(scene.path);
 
        return EditorScenes.ToArray();
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------	
    private static void ChangeGradle()
    {
        EditorPrefs.SetBool("JdkUseEmbedded", true);
        EditorPrefs.SetBool("NdkUseEmbedded", true);
        EditorPrefs.SetBool("SdkUseEmbedded", true);
        EditorPrefs.SetBool("GradleUseEmbedded", false);
        EditorPrefs.SetBool("AndroidGradleStopDaemonsOnExit", true);
		
        var args = FindArgs();
        EditorPrefs.SetString("GradlePath", args.gradlePath);
        System.Console.WriteLine("[JenkinsBuild] PreProcessBuild - changed path: "+EditorPrefs.GetString("GradlePath"));
    }	
  
    // ------------------------------------------------------------------------
    // e.g. BuildTargetGroup.Standalone, BuildTarget.StandaloneOSX
    // ------------------------------------------------------------------------
    private static void Build(string outputPath, BuildTargetGroup buildTargetGroup, BuildTarget buildTarget, BuildOptions buildOptions)
    {
        System.Console.WriteLine("[JenkinsBuild] Building:" + outputPath + " buildTargetGroup:" + buildTargetGroup.ToString() + " buildTarget:" + buildTarget.ToString());
  
        // https://docs.unity3d.com/ScriptReference/EditorUserBuildSettings.SwitchActiveBuildTarget.html
        bool switchResult = EditorUserBuildSettings.SwitchActiveBuildTarget(buildTarget);
        if (switchResult)
        {
            System.Console.WriteLine("[JenkinsBuild] Successfully changed Build Target to: " + buildTarget.ToString());
        }
        else
        {
            System.Console.WriteLine("[JenkinsBuild] Unable to change Build Target to: " + buildTarget.ToString() + " Exiting...");
            return;
        }

        if(buildTarget == BuildTarget.Android)
        {
            ChangeGradle();
        }	
  
        // https://docs.unity3d.com/ScriptReference/BuildPipeline.BuildPlayer.html		
        BuildPlayerOptions buildPlayerOptions = new BuildPlayerOptions();
        buildPlayerOptions.scenes = FindEnabledEditorScenes();
        buildPlayerOptions.locationPathName = outputPath;
        buildPlayerOptions.target = buildTarget;
        buildPlayerOptions.options = buildOptions;        
        BuildReport buildReport = BuildPipeline.BuildPlayer(buildPlayerOptions);		
		
        BuildSummary buildSummary = buildReport.summary;
        if (buildSummary.result == BuildResult.Succeeded)
        {
            System.Console.WriteLine("[JenkinsBuild] Build Success: Time:" + buildSummary.totalTime + " Size:" + buildSummary.totalSize + " bytes");
        }
        else
        {
            System.Console.WriteLine("[JenkinsBuild] Build Failed: Time:" + buildSummary.totalTime + " Total Errors:" + buildSummary.totalErrors);
        }		
    }

    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------	
    private static void BuildLocationPath(string outputFolder)
    {
        bool exists = System.IO.Directory.Exists(outputFolder);
        if (!exists)
            System.IO.Directory.CreateDirectory(outputFolder);
    }	
	
    // ------------------------------------------------------------------------
    /// Retrieves Application.persistentDataPath depends on the given platform.
    /// 
    /// iOS            - /var/mobile/Applications/[program_ID]/Documents : read/write
    /// Android        - [External] /mnt/sdcard/Android/data/[bundle id]/files : read/write
    ///                  [Internal] /data/data/[bundle id]/files/ : read/write
    /// WEB Player     - /                
    /// Windows Player - [UserDirectory]/AppData/LocalLow/[Company]/[Product Name] : read/write
    /// OSX            - [UserDirectory]/Library/Caches/unity.[Company].[Product] : read/write
    /// Windows Edtor  - [UserDirectory]/AppData/LocalLow/[Company]/[Product] : read/write
    /// Mac Editor     - [UserDirectory]/Library/Caches/unity.[Company].[Product] : read/write
    // ------------------------------------------------------------------------
    public static string GetDocumentFilePath(string fileName)
    {
        string result = string.Empty;

        if (Application.isEditor)
        {
            string datapath = "Assets";
            string path = Application.dataPath.Substring(0, Application.dataPath.Length - datapath.Length);
            result = Path.Combine(path, fileName);
        }
        else if (Application.platform == RuntimePlatform.WindowsPlayer)
        {
            result = Path.Combine(Application.dataPath, fileName);
        }
        else
        {
            result = Path.Combine(Application.persistentDataPath, fileName);
        }

        BuildLocationPath(result);

        return result;
    }
 
    // ------------------------------------------------------------------------
    // ------------------------------------------------------------------------
    private class Args
    {			
        public string appName = "AppName";
        public string targetPath = GetDocumentFilePath("");
        public string gradlePath = GetDocumentFilePath("gradle-6.7.1");
    }
}
