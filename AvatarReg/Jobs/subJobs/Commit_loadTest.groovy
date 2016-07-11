//incoming parameters
//def serviceName = "avreg"
//def buildTrigger = "NIGHTLY"
//def serviceConfigBaseURL


def buildPublishTargetNode
def rootPomPath
def parentstageName = "Commit"
def gitLoadTestURL
def gitCredentials

node('master') {
	def masterWorkspace = pwd()
	getConfigFile(serviceConfigBaseURL,"serviceConfig.groovy")
	//get values using NonCPS as not serializable
	def values = getValues(new File("${masterWorkspace}/serviceConfig.groovy"))
	buildPublishTargetNode=values[0]
	gitLoadTestURL=values[1]
	gitCredentials=values[2]
	rootPomPath=values[3]
}


stage "${parentstageName}::BuildAndPublish"

    subJob1 = build  job: '../../wppCommon/subJobs/buildAndPublish',
                    parameters: [
							[$class: 'StringParameterValue', name: 'gitCredentials', value: gitCredentials ],
							[$class: 'StringParameterValue', name: 'url', value: gitLoadTestURL ],
							[$class: 'StringParameterValue', name: 'buildTrigger', value: buildTrigger ],
							[$class: 'StringParameterValue', name: 'serviceName', value: serviceName ],
							[$class: 'StringParameterValue', name: 'targetNode', value: buildPublishTargetNode ],
							[$class: 'StringParameterValue', name: 'rootPomPath', value: rootPomPath ],
                    ] ;
    
    // Return Values
    def artifactVersion = subJob1.description.tokenize('#')[0].tokenize('=')[1]
    def commitID = subJob1.description.tokenize('#')[1].tokenize('=')[1]
    
currentBuild.setDescription("#artifactVersion="+artifactVersion+"#commitID="+commitID)
    
def getConfigFile(baseURL,fileName) {
    def workspace = pwd()
	sh """mkdir -p "${workspace}"
	"""
    def file = new File("${workspace}/${fileName}").newOutputStream()  
    file << new URL("${baseURL}/${fileName}").openStream()  
    file.close()
}
@NonCPS
def getValues(serviceConfigFile)
{
    def configObject = new ConfigSlurper().parse(serviceConfigFile.text)
    def values = new String[4]
	values[0]=configObject.buildPublishTargetNode
	values[1]=configObject.gitLoadTestURL
	values[2]=configObject.gitCredentials
	values[3]=configObject.loadTestRootPomPath
	return (values)
}