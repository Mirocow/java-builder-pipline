@Library('java-builder-pipline') _

def context = new org.combine.GlobalContext()
def builder =  new org.combine.Builder()

node('NODE'){
  builder.BuildUnity(context, "Android")
}