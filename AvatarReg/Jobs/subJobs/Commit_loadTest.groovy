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
	serviceConfigFile = new File("${masterWorkspace}/serviceConfig.groovy")
	def configObject = new ConfigSlurper().parse(serviceConfigFile.text)
	buildPublishTargetNode=configObject.buildPublishTargetNode
	gitLoadTestURL=configObject.gitLoadTestURL
	gitCredentials=configObject.gitCredentials
	rootPomPath=configObject.loadTestRootPomPath
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
    def artifactURL = subJob1.description.tokenize('#')[0].tokenize('=')[1]
    def artifactVersion = subJob1.description.tokenize('#')[1].tokenize('=')[1]
    def commitID = subJob1.description.tokenize('#')[2].tokenize('=')[1]
    
currentBuild.setDescription("#artifactURL="+artifactURL+"#artifactVersion="+artifactVersion+"#commitID="+commitID)
    
def getConfigFile(baseURL,fileName) {
    def workspace = pwd()
    def file = new File("${workspace}/${fileName}").newOutputStream()  
    file << new URL("${baseURL}/${fileName}").openStream()  
    file.close()
}